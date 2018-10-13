package com.mordenkainen.equivalentenergistics.blocks.condenser.tiles;

import java.util.List;

import javax.annotation.Nonnull;

import com.mordenkainen.equivalentenergistics.blocks.ModBlocks;
import com.mordenkainen.equivalentenergistics.blocks.condenser.CondenserState;
import com.mordenkainen.equivalentenergistics.core.config.EqEConfig;
import com.mordenkainen.equivalentenergistics.integration.ae2.grid.GridUtils;
import com.mordenkainen.equivalentenergistics.integration.ae2.tiles.TileAEBase;
import com.mordenkainen.equivalentenergistics.items.ModItems;
import com.mordenkainen.equivalentenergistics.util.CommonUtils;
import com.mordenkainen.equivalentenergistics.util.IDropItems;
import com.mordenkainen.equivalentenergistics.util.InvUtils;

import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.networking.GridFlags;
import appeng.api.networking.IGridNode;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.item.IItemEmc;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;

public class TileEMCCondenser extends TileAEBase implements IGridTickable, IDropItems {

    private final static String STATE_TAG = "state";

    protected CondenserState state = CondenserState.IDLE;
    protected CondenserInventoryHandler inv = createInv();

    private boolean doDrops = true;

    public TileEMCCondenser() {
        this(new ItemStack(Item.getItemFromBlock(ModBlocks.CONDENSER), 1, 0));
    }

    public TileEMCCondenser(final ItemStack stack ) {
        super(stack);
        gridProxy.setFlags(GridFlags.REQUIRE_CHANNEL);
        gridProxy.setIdlePowerUsage(EqEConfig.emcCondenser.idlePower);
    }

    @Override
    protected void getPacketData(final NBTTagCompound nbttagcompound) {
        super.getPacketData(nbttagcompound);
        nbttagcompound.setInteger(STATE_TAG, state.ordinal());
    }

    @Override
    protected boolean readPacketData(final NBTTagCompound nbttagcompound) {
        boolean flag = super.readPacketData(nbttagcompound);
        final CondenserState newState = CondenserState.values()[nbttagcompound.getInteger(STATE_TAG)];
        if (!newState.equals(state)) {
            state = newState;
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
        if (refreshNetworkState()) {
            markForUpdate();
        }

        CondenserState newState = state;

        if (!isActive() || inv.isEmpty()) {
            updateState(CondenserState.IDLE);
        } else {
            newState = processInv();
            updateState(newState);
        }

        return state.getTickRate();
    }

    @Override
    public void disableDrops() {
        doDrops  = false;
    }

    @Override
    public void getDrops(final World world, final BlockPos pos, final List<ItemStack> drops) {
        if(doDrops) {
            drops.addAll(InvUtils.getInvAsList(inv));
        }
    }
    
    protected double getEMCPerTick() {
        return EqEConfig.emcCondenser.emcPerTick;
    }

    public CondenserState getState() {
        return state;
    }
    
    protected int getMaxItemsForPower(final int stackSize, final double emcValue) {
        final double powerPerItem = emcValue * EqEConfig.emcCondenser.powerPerEMC;
        final double powerRequired = stackSize * powerPerItem;
        final double powerAvail = GridUtils.extractAEPower(getProxy(), powerRequired, Actionable.SIMULATE, PowerMultiplier.CONFIG);
        return (int) (powerAvail / powerPerItem);
    }
    
    protected double processItems(final int slot, final double remainingEMC, final boolean usePower) {
        final ItemStack stack = inv.getStackInSlot(slot);
        final long itemEMC = ProjectEAPI.getEMCProxy().getValue(ItemHandlerHelper.copyStackWithSize(stack, 1));
        if (itemEMC > remainingEMC) {
            return remainingEMC;
        }
        
        int numToStore = (int) Math.min(stack.getCount(), remainingEMC / itemEMC);
        long emcToStore = itemEMC * numToStore;
        
        final double amountStored = GridUtils.injectEMC(getProxy(), emcToStore, Actionable.SIMULATE, mySource);
        if (amountStored == 0) {
            return -1;
        }
        
        numToStore = (int) (amountStored / itemEMC);
        if (usePower) {
            numToStore = Math.min(getMaxItemsForPower(numToStore, itemEMC), numToStore);
        }
        
        if (numToStore <= 0) {
            return -3;
        }
        
        emcToStore = itemEMC * numToStore;
        if (usePower) {
            GridUtils.extractAEPower(getProxy(), emcToStore * EqEConfig.emcCondenser.powerPerEMC, Actionable.MODULATE, PowerMultiplier.CONFIG);
        }
        
        GridUtils.injectEMC(getProxy(), emcToStore, Actionable.MODULATE, mySource);
        
        stack.shrink(numToStore);
        inv.setStackInSlot(slot, CommonUtils.filterForEmpty(stack));

        return remainingEMC - numToStore;
    }
    
    protected double processStorage(final int slot, final double remainingEMC) {
        final ItemStack stack = inv.getStackInSlot(slot);
        final double itemEMC = ((IItemEmc) stack.getItem()).getStoredEmc(stack);
        double stored = 0;

        if (itemEMC > 0) {
            final double toStore = Math.min(remainingEMC, itemEMC);
            
            stored = GridUtils.injectEMC(getProxy(), toStore, Actionable.MODULATE, mySource);
            if (stored == 0) {
                return -1;
            }
            
            ((IItemEmc) stack.getItem()).extractEmc(stack, stored);
            inv.setStackInSlot(slot, stack);
        }

        if (((IItemEmc) stack.getItem()).getStoredEmc(stack) <= 0) {
            inv.setStackInSlot(slot, ejectItem(stack));
            if (inv.getStackInSlot(slot) != ItemStack.EMPTY) {
                return -2;
            }
        }

        return remainingEMC - stored;
    }
    
    protected CondenserState processInv() {
        double remainingEMC = getEMCPerTick();
        for (int slot = 0; slot < 4 && remainingEMC > 0; slot++) {
            final ItemStack stack = inv.getStackInSlot(slot);
            if (stack.isEmpty()) {
                continue;
            }

            if (stack.getItem() instanceof IItemEmc) {
                remainingEMC = processStorage(slot, remainingEMC);
            } else if (ProjectEAPI.getEMCProxy().getValue(stack) > 0) {
                remainingEMC = processItems(slot, remainingEMC, stack.getItem() != ModItems.CRYSTAL);
            } else {
                inv.setStackInSlot(slot, ejectItem(stack));
                if (!inv.getStackInSlot(slot).isEmpty()) {
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
        if (!state.equals(newState)) {
            state = newState;
            markForUpdate();
            return true;
        }
        return false;
    }
    
    protected ItemStack ejectItem(final ItemStack stack) {
        return GridUtils.injectItemsForPower(getProxy(), stack, mySource);
    }

    @Override
    public boolean hasCapability(final @Nonnull Capability<?> cap, final EnumFacing side) {
        return cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(cap, side);
    }

    @Override
    public <T> T getCapability(final @Nonnull Capability<T> cap, final EnumFacing side) {
        if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(inv);
        }
        return super.getCapability(cap, side);
    }

    protected CondenserInventoryHandler createInv() {
        return new CondenserInventoryHandler(this);
    }

    @Override
    public void readFromNBT(final NBTTagCompound data) {
        super.readFromNBT(data);
        inv = createInv();
        inv.deserializeNBT(data);
    }

    @Override
    public NBTTagCompound writeToNBT(final NBTTagCompound data) {
        super.writeToNBT(data);
        data.merge(inv.serializeNBT());
        return data;
    }

    protected boolean isValidItem(final ItemStack stack) {
        return stack.getItem() instanceof IItemEmc || ProjectEAPI.getEMCProxy().getValue(stack) > 0 && ProjectEAPI.getEMCProxy().getValue(stack) <= getEMCPerTick();
    }

    protected static class CondenserInventoryHandler extends ItemStackHandler {

        private final TileEMCCondenser tile;

        public CondenserInventoryHandler(final TileEMCCondenser tile) {
            super(4);
            this.tile = tile;
        }

        @Nonnull
        @Override
        public ItemStack insertItem(final int slot, final @Nonnull ItemStack stack, final boolean simulate) {
            if(tile.isValidItem(stack)) {
                return super.insertItem(slot, stack, simulate);
            } else {
                return stack;
            }
        }

        @Override
        public void onContentsChanged(final int slot) {
            tile.markDirty();
        }

        public boolean isEmpty() {
            for (final ItemStack stack : stacks) {
                if (!stack.isEmpty()) {
                    return false;
                }
            }
            return true;
        }
    }

}
