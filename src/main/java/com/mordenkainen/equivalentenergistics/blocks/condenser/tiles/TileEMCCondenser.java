package com.mordenkainen.equivalentenergistics.blocks.condenser.tiles;

import java.util.List;

import javax.annotation.Nonnull;

import com.mordenkainen.equivalentenergistics.blocks.ModBlocks;
import com.mordenkainen.equivalentenergistics.blocks.base.tile.TileAEBase;
import com.mordenkainen.equivalentenergistics.blocks.condenser.CondenserState;
import com.mordenkainen.equivalentenergistics.core.config.Config;
import com.mordenkainen.equivalentenergistics.integration.ae2.cache.storage.IEMCStorageGrid;
import com.mordenkainen.equivalentenergistics.integration.ae2.grid.GridAccessException;
import com.mordenkainen.equivalentenergistics.integration.ae2.grid.GridUtils;
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
	
	public TileEMCCondenser(ItemStack stack ) {
		super(stack);
		gridProxy.setFlags(GridFlags.REQUIRE_CHANNEL);
        gridProxy.setIdlePowerUsage(Config.condenser_Idle_Power);
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
        if (newState != state) {
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

    public CondenserState getState() {
        return state;
    }
    
    protected boolean updateState(final CondenserState newState) {
        if (state != newState) {
            state = newState;
            markForUpdate();
            return true;
        }
        return false;
    }
    
    @Override
	public boolean hasCapability(@Nonnull Capability<?> cap, EnumFacing side) {
		return cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(cap, side);
	}

	@Override
	public <T> T getCapability(@Nonnull Capability<T> cap, EnumFacing side) {
		if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
			return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(inv);
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
	
	protected ItemStack ejectItem(final ItemStack stack) {
        return GridUtils.injectItemsForPower(getProxy(), stack, mySource);
    }
	
	protected CondenserState processInv() {
        float remainingEMC = getEMCPerTick();
        for (int slot = 0; slot < 4 && remainingEMC > 0; slot++) {
            final ItemStack stack = inv.getStackInSlot(slot);
            if (stack == ItemStack.EMPTY) {
                continue;
            }

            if (stack.getItem() instanceof IItemEmc) {
                remainingEMC = processStorage(slot, remainingEMC);
            } else if (ProjectEAPI.getEMCProxy().getValue(stack) > 0) {
                remainingEMC = processItems(slot, remainingEMC, stack.getItem() != ModItems.CRYSTAL);
            } else if (!inv.getStackInSlot(slot).isEmpty()) {
                inv.setStackInSlot(slot, ejectItem(stack));
                if (inv.getStackInSlot(slot) != ItemStack.EMPTY) {
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
	
	protected float processStorage(final int slot, final float remainingEMC) {
        final ItemStack stack = inv.getStackInSlot(slot);
        final float itemEMC = (float) ((IItemEmc) stack.getItem()).getStoredEmc(stack);
        float toStore = 0;

        try {
            if (itemEMC > 0) {
                final IEMCStorageGrid emcGrid = GridUtils.getEMCStorage(getProxy());
                toStore = Math.min(Math.min(remainingEMC, itemEMC), emcGrid.getAvail());
                if (toStore <= 0) {
                    return -1;
                }

                emcGrid.addEMC(toStore, Actionable.MODULATE);
                ((IItemEmc) stack.getItem()).extractEmc(stack, toStore);
                inv.setStackInSlot(slot, stack);
            }

            if (((IItemEmc) stack.getItem()).getStoredEmc(stack) <= 0) {
                inv.setStackInSlot(slot, ejectItem(stack));
                if (inv.getStackInSlot(slot) != ItemStack.EMPTY) {
                    return -2;
                }
            }

            return remainingEMC - toStore;
        } catch (GridAccessException e) {
            CommonUtils.debugLog("processStorage: Error accessing grid:", e);
            return -1;
        }
    }
	
	protected float processItems(final int slot, final float remainingEMC, final boolean usePower) {
        ItemStack stack = inv.getStackInSlot(slot);
        final float itemEMC = ProjectEAPI.getEMCProxy().getValue(ItemHandlerHelper.copyStackWithSize(stack, 1));
        try {
            final IEMCStorageGrid emcGrid = GridUtils.getEMCStorage(getProxy());
            final float availEMC = emcGrid.getAvail();

            if (itemEMC > availEMC) {
                return -1;
            }

            if (itemEMC > remainingEMC) {
                return remainingEMC;
            }

            int maxToDo = Math.min(stack.getCount(), Math.min((int) (availEMC / itemEMC), (int) (remainingEMC / itemEMC)));
            if (usePower) {
                maxToDo = Math.min(getMaxItemsForPower(maxToDo, itemEMC), maxToDo);
            }

            if (maxToDo <= 0) {
                return -3;
            }

            final float toStore = itemEMC * maxToDo;
            if (usePower) {
                GridUtils.extractAEPower(getProxy(), toStore * Config.condenser_Active_Power, Actionable.MODULATE, PowerMultiplier.CONFIG);
            }
            emcGrid.addEMC(toStore, Actionable.MODULATE);
            stack.shrink(maxToDo);
            inv.setStackInSlot(slot, InvUtils.filterForEmpty(stack));

            return remainingEMC - toStore;
        } catch (GridAccessException e) {
            CommonUtils.debugLog("processItems: Error accessing grid:", e);
            return -1;
        }
    }
	
	protected int getMaxItemsForPower(final int stackSize, final float emcValue) {
        final double powerPerItem = emcValue * Config.condenser_Active_Power;
        final double powerRequired = stackSize * powerPerItem;
        final double powerAvail = GridUtils.extractAEPower(getProxy(), powerRequired, Actionable.SIMULATE, PowerMultiplier.CONFIG);
        return (int) (powerAvail / powerPerItem);
    }
	
    protected float getEMCPerTick() {
        return Config.condenser_EMC_Per_Tick;
    }
    
    protected boolean isValidItem(ItemStack stack) {
    	return stack.getItem() instanceof IItemEmc || (ProjectEAPI.getEMCProxy().hasValue(stack) && ProjectEAPI.getEMCProxy().getValue(stack) <= getEMCPerTick());
    }
	
	protected static class CondenserInventoryHandler extends ItemStackHandler {
		
		private final TileEMCCondenser tile;
		
		public CondenserInventoryHandler(TileEMCCondenser tile) {
			super(4);
			this.tile = tile;
		}
		
		@Nonnull
		@Override
		public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
			if(tile.isValidItem(stack)) {
				return super.insertItem(slot, stack, simulate);
			} else {
				return stack;
			}
		}
		
		@Override
		public void onContentsChanged(int slot) {
			tile.markDirty();
		}
		
		public boolean isEmpty() {
			for (ItemStack stack : stacks) {
				if (!stack.isEmpty()) {
					return false;
				}
			}
			return true;
		}
	}
	
}
