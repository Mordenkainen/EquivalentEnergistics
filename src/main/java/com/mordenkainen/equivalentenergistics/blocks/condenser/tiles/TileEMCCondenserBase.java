package com.mordenkainen.equivalentenergistics.blocks.condenser.tiles;

import java.util.ArrayList;
import java.util.List;

import com.mordenkainen.equivalentenergistics.blocks.condenser.BlockEMCCondenser;
import com.mordenkainen.equivalentenergistics.blocks.condenser.CondenserState;
import com.mordenkainen.equivalentenergistics.integration.Integration;
import com.mordenkainen.equivalentenergistics.integration.ae2.cache.storage.IEMCStorageGrid;
import com.mordenkainen.equivalentenergistics.integration.ae2.grid.GridAccessException;
import com.mordenkainen.equivalentenergistics.integration.ae2.grid.GridUtils;
import com.mordenkainen.equivalentenergistics.integration.ae2.tiles.TileAEInv;
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
    private int updateCounter = 19;
    
    public TileEMCCondenserBase(final ItemStack repItem) {
        super(repItem);
        internalInventory = new CondenserInventory();
        gridProxy.setFlags(GridFlags.REQUIRE_CHANNEL);
        gridProxy.setIdlePowerUsage(BlockEMCCondenser.idlePower);
    }

    @Override
    protected void getPacketData(final NBTTagCompound nbttagcompound) {
        super.getPacketData(nbttagcompound);
        nbttagcompound.setInteger(STATE_TAG, state.ordinal());
        if (updateCounter >= 20) {
            updateCounter = 0;
            internalInventory.saveToNBT(nbttagcompound, INVENTORY_TAG);
        }
    }

    @Override
    protected boolean readPacketData(final NBTTagCompound nbttagcompound) {
        boolean flag = super.readPacketData(nbttagcompound);
        final CondenserState newState = CondenserState.values()[nbttagcompound.getInteger(STATE_TAG)];
        if (newState != state) {
            state = newState;
            flag = true;
        }
        if(nbttagcompound.hasKey(INVENTORY_TAG)) {
            internalInventory.loadFromNBT(nbttagcompound, INVENTORY_TAG);
            flag = true;
        }
        return flag;
    }

    @Override
    public TickingRequest getTickingRequest(final IGridNode node) {
        return new TickingRequest(1, 20, false, true);
    }

    @Override
    public TickRateModulation tickingRequest(final IGridNode node, final int ticksSinceLast) {
        updateCounter++;
        if (refreshNetworkState() || updateCounter == 20) {
            markForUpdate();
        }
            
        final TickRateModulation result = tickingRequest();
        if(result != null) {
            return result;
        }
        
        CondenserState newState = state;
        
        if (!isActive() || getInventory().isEmpty()) {
            updateState(CondenserState.IDLE);
        } else {
            newState = processInv();
            updateState(newState);
        }

        return state.getTickRate();
    }
    
    protected abstract TickRateModulation tickingRequest();

    protected abstract double getEMCPerTick();

    public CondenserState getState() {
        return state;
    }

    protected int getMaxItemsForPower(final int stackSize, final double emcValue) {
        final double powerPerItem = emcValue * BlockEMCCondenser.activePower;
        final double powerRequired = stackSize * powerPerItem;
        final double powerAvail = GridUtils.extractAEPower(getProxy(), powerRequired, Actionable.SIMULATE, PowerMultiplier.CONFIG);
        return (int) (powerAvail / powerPerItem);
    }

    protected double processItems(final int slot, final double remainingEMC, final boolean usePower) {
        ItemStack stack = getInventory().getStackInSlot(slot);
        final double itemEMC = Integration.emcHandler.getSingleEnergyValue(stack);
        try {
            final IEMCStorageGrid emcGrid = GridUtils.getEMCStorage(getProxy());
            final double availEMC = emcGrid.getAvail();

            if (itemEMC > availEMC) {
                return -1;
            }

            if (itemEMC > remainingEMC) {
                return remainingEMC;
            }

            int maxToDo = Math.min(stack.stackSize, Math.min((int) (availEMC / itemEMC), (int) (remainingEMC / itemEMC)));
            if (usePower) {
                maxToDo = Math.min(getMaxItemsForPower(maxToDo, itemEMC), maxToDo);
            }

            if (maxToDo <= 0) {
                return -3;
            }

            final double toStore = itemEMC * maxToDo;
            if (usePower) {
                GridUtils.extractAEPower(getProxy(), toStore * BlockEMCCondenser.activePower, Actionable.MODULATE, PowerMultiplier.CONFIG);
            }
            emcGrid.addEMC(toStore, Actionable.MODULATE);
            stack.stackSize -= maxToDo;
            getInventory().setInventorySlotContents(slot, CommonUtils.filterForEmpty(stack));

            return remainingEMC - toStore;
        } catch (GridAccessException e) {
            CommonUtils.debugLog("processItems: Error accessing grid:", e);
            return -1;
        }
    }

    protected double processStorage(final int slot, final double remainingEMC) {
        final ItemStack stack = getInventory().getStackInSlot(slot);
        final double itemEMC = Integration.emcHandler.getStoredEMC(stack);
        double toStore = 0;

        try {
            if (itemEMC > 0) {
                final IEMCStorageGrid emcGrid = GridUtils.getEMCStorage(getProxy());
                toStore = Math.min(Math.min(remainingEMC, itemEMC), emcGrid.getAvail());
                if (toStore <= 0) {
                    return -1;
                }

                emcGrid.addEMC(toStore, Actionable.MODULATE);
                Integration.emcHandler.extractEMC(stack, toStore);
                getInventory().setInventorySlotContents(slot, stack);
            }

            if (Integration.emcHandler.getStoredEMC(stack) <= 0) {
                getInventory().setInventorySlotContents(slot, ejectItem(stack));
                if (getInventory().getStackInSlot(slot) != null) {
                    return -2;
                }
            }

            return remainingEMC - toStore;
        } catch (GridAccessException e) {
            CommonUtils.debugLog("processStorage: Error accessing grid:", e);
            return -1;
        }
    }

    protected CondenserState processInv() {
        double remainingEMC = getEMCPerTick();
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
                    remainingEMC = -2;
                }
            }
        }
        
        switch ((int) remainingEMC) {
            case -1:
                return CondenserState.NOEMCSTORAGE;
            case -2:
                return CondenserState.NOITEMSTORAGE;
            case -3:
                return CondenserState.NOPOWER;
            default:
                return CondenserState.ACTIVE;
        }
    }

    protected boolean updateState(final CondenserState newState) {
        if (state != newState) {
            state = newState;
            markForUpdate();
            return true;
        }
        return false;
    }

    protected ItemStack ejectItem(final ItemStack stack) {
        return GridUtils.injectItemsForPower(getProxy(), stack, mySource);
    }
    
    protected class CondenserInventory extends InternalInventory {

        CondenserInventory() {
            super("EMCCondenserInventory", 4, 64);
        }

        @Override
        public boolean isItemValidForSlot(final int slotId, final ItemStack itemStack) {
            return Integration.emcHandler.isEMCStorage(itemStack) || Integration.emcHandler.hasEMC(itemStack) && Integration.emcHandler.getSingleEnergyValue(itemStack) <= getEMCPerTick();
        }
        
    }

    public List<ItemStack> getDisplayStacks() {
        final List<ItemStack> stacks = new ArrayList<ItemStack>();
        for (int i = 0; i < internalInventory.getSizeInventory(); i++) {
            stacks.add(internalInventory.getStackInSlot(i));
        }
        return stacks;
    }
}
