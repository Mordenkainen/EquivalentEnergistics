package com.mordenkainen.equivalentenergistics.blocks.condenser.tiles;

import java.util.EnumSet;

import com.mordenkainen.equivalentenergistics.blocks.condenser.BlockEMCCondenser;
import com.mordenkainen.equivalentenergistics.blocks.condenser.CondenserState;
import com.mordenkainen.equivalentenergistics.integration.Integration;
import com.mordenkainen.equivalentenergistics.integration.ae2.cache.storage.IEMCStorageGrid;
import com.mordenkainen.equivalentenergistics.integration.ae2.grid.GridAccessException;
import com.mordenkainen.equivalentenergistics.integration.ae2.grid.GridUtils;
import com.mordenkainen.equivalentenergistics.integration.ae2.tiles.TileAEInv;
import com.mordenkainen.equivalentenergistics.items.ItemEMCCell;
import com.mordenkainen.equivalentenergistics.items.ItemEnum;
import com.mordenkainen.equivalentenergistics.util.CommonUtils;
import com.mordenkainen.equivalentenergistics.util.inventory.InternalInventory;

import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.networking.GridFlags;
import appeng.api.networking.IGridNode;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public abstract class TileEMCCondenserBase extends TileAEInv implements IGridTickable {

    private final static String STATE_TAG = "state";

    protected CondenserState state = CondenserState.IDLE;
    protected final EnumSet<CondenserState> failedStates = EnumSet.of(CondenserState.MISSING_CHANNEL, CondenserState.UNPOWERED);

    protected class CondenserInventory extends InternalInventory {

        CondenserInventory() {
            super("EMCCondenserInventory", 4, 64);
        }

        @Override
        public boolean isItemValidForSlot(final int slotId, final ItemStack itemStack) {
            return Integration.emcHandler.hasEMC(itemStack) || itemStack.getItem() instanceof ItemEMCCell;
        }
    }

    public TileEMCCondenserBase(final ItemStack repItem) {
        super(repItem);
        internalInventory = new CondenserInventory();
        gridProxy.setFlags(GridFlags.REQUIRE_CHANNEL);
        gridProxy.setIdlePowerUsage(BlockEMCCondenser.idlePower);
    }

    @Override
    protected void getPacketData(final NBTTagCompound nbttagcompound) {
        nbttagcompound.setInteger(STATE_TAG, state.ordinal());
    }

    @Override
    protected void readPacketData(final NBTTagCompound nbttagcompound) {
        state = CondenserState.values()[nbttagcompound.getInteger(STATE_TAG)];
    }

    @Override
    public TickingRequest getTickingRequest(final IGridNode node) {
        return new TickingRequest(1, 20, false, true);
    }

    @Override
    public TickRateModulation tickingRequest(final IGridNode node, final int ticksSinceLast) {
        CondenserState newState = state;

        newState = checkRequirements();

        if (newState != CondenserState.IDLE && updateState(newState) || failedStates.contains(newState)) {
            return state.getTickRate();
        }

        if (!getInventory().isEmpty()) {
            newState = processInv();
            updateState(newState);
            return state.getTickRate();
        }

        updateState(CondenserState.IDLE);
        return state.getTickRate();
    }

    protected abstract float getEMCPerTick();

    public CondenserState getState() {
        return state;
    }

    protected CondenserState checkRequirements() {
        if (!gridProxy.meetsChannelRequirements()) {
            return CondenserState.MISSING_CHANNEL;
        }

        if (!gridProxy.isPowered()) {
            return CondenserState.UNPOWERED;
        }

        return CondenserState.IDLE;
    }

    protected int getMaxItemsForPower(final int stackSize, final float emcValue) {
        final double powerPerItem = emcValue * BlockEMCCondenser.activePower;
        final double powerRequired = stackSize * powerPerItem;
        final double powerAvail = GridUtils.extractAEPower(getProxy(), powerRequired, Actionable.SIMULATE, PowerMultiplier.CONFIG);
        return (int) (powerAvail / powerPerItem);
    }

    protected float processItems(final int slot, final float remainingEMC, final boolean usePower) {
        ItemStack stack = getInventory().getStackInSlot(slot);
        final float itemEMC = Integration.emcHandler.getSingleEnergyValue(stack);
        try {
            final IEMCStorageGrid emcGrid = GridUtils.getEMCStorage(getProxy());
            final float availEMC = emcGrid.getAvail();

            if (itemEMC > availEMC) {
                return -1;
            }

            if (itemEMC > remainingEMC) {
                return remainingEMC;
            }

            int maxToDo = Math.min(stack.stackSize, Math.min((int) (availEMC / itemEMC), (int) (remainingEMC / itemEMC)));
            if (usePower) {
                maxToDo = getMaxItemsForPower(maxToDo, itemEMC);
            }

            if (maxToDo <= 0) {
                return -1;
            }

            final float toStore = itemEMC * maxToDo;
            if (usePower) {
                GridUtils.getEnergy(getProxy()).extractAEPower(toStore * BlockEMCCondenser.activePower, Actionable.MODULATE, PowerMultiplier.CONFIG);
            }
            emcGrid.injectEMC(toStore, Actionable.MODULATE);
            stack.stackSize -= maxToDo;
            getInventory().setInventorySlotContents(slot, CommonUtils.filterForEmpty(stack));

            return remainingEMC - toStore;
        } catch (GridAccessException e) {
            CommonUtils.debugLog("processItems: Error accessing grid:", e);
            return -1;
        }
    }

    protected float processStorage(final int slot, final float remainingEMC) {
        final ItemStack stack = getInventory().getStackInSlot(slot);
        final float itemEMC = Integration.emcHandler.getStoredEMC(stack);
        float toStore = 0;

        try {
            if (itemEMC > 0) {
                final IEMCStorageGrid emcGrid = GridUtils.getEMCStorage(getProxy());
                toStore = Math.min(Math.min(remainingEMC, itemEMC), emcGrid.getAvail());
                if (toStore <= 0) {
                    return -1;
                }

                emcGrid.injectEMC(toStore, Actionable.MODULATE);
                Integration.emcHandler.extractEMC(stack, toStore);
                getInventory().setInventorySlotContents(slot, stack);
            }

            if (Integration.emcHandler.getStoredEMC(stack) <= 0) {
                getInventory().setInventorySlotContents(slot, ejectItem(stack));
                if (getInventory().getStackInSlot(slot) != null) {
                    return -1;
                }
            }

            return remainingEMC - toStore;
        } catch (GridAccessException e) {
            CommonUtils.debugLog("processStorage: Error accessing grid:", e);
            return -1;
        }
    }

    protected CondenserState processInv() {
        float remainingEMC = getEMCPerTick();
        for (int slot = 0; slot < 4 && remainingEMC > 0; slot++) {
            final ItemStack stack = getInventory().getStackInSlot(slot);
            if (stack == null) {
                continue;
            }

            if (Integration.emcHandler.isEMCStorage(stack)) {
                remainingEMC = processStorage(slot, remainingEMC);
            } else if (Integration.emcHandler.hasEMC(stack)) {
                remainingEMC = processItems(slot, remainingEMC, !ItemEnum.isCrystal(stack));
            } else {
                getInventory().setInventorySlotContents(slot, ejectItem(stack));
                if (getInventory().getStackInSlot(slot) != null) {
                    remainingEMC = -1;
                }
            }
        }

        return remainingEMC == -1 ? CondenserState.BLOCKED : CondenserState.ACTIVE;
    }

    protected boolean updateState(final CondenserState newState) {
        if (state != newState) {
            state = newState;
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
            return true;
        }
        return false;
    }

    protected ItemStack ejectItem(final ItemStack stack) {
        return GridUtils.injectItemsForPower(getProxy(), stack, mySource);
    }
}
