package com.mordenkainen.equivalentenergistics.blocks.condenser.tiles;

import java.util.HashMap;
import java.util.Map;

import com.mordenkainen.equivalentenergistics.blocks.ModBlocks;
import com.mordenkainen.equivalentenergistics.blocks.condenser.CondenserState;
import com.mordenkainen.equivalentenergistics.core.config.EqEConfig;
import com.mordenkainen.equivalentenergistics.util.InvUtils;

import appeng.api.networking.IGridNode;
import appeng.api.networking.ticking.TickRateModulation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

public class TileEMCCondenserExt extends TileEMCCondenserAdv {

	private final static String SIDE_TAG = "sides";

    private final Map<EnumFacing, SideSetting> sides = new HashMap<EnumFacing, SideSetting>();
    
    public enum SideSetting {
        NONE,
        INPUT,
        OUTPUT;
        
        public SideSetting getNext() {
            int setting = this.ordinal() + 1;
            if (setting >= 3) {
                setting = 0;
            }
            return SideSetting.values()[setting];
        }
    }
    
	public TileEMCCondenserExt() {
		this(new ItemStack(Item.getItemFromBlock(ModBlocks.CONDENSER), 1, 2));
	}

	public TileEMCCondenserExt(final ItemStack repItem) {
		super(repItem);
    	for (final EnumFacing side : EnumFacing.VALUES) {
            sides.put(side, SideSetting.NONE);
        }
	}

	@Override
	protected float getEMCPerTick() {
        return EqEConfig.emcCondenser.emcPerTick * 100;
    }
	
	@Override
	protected boolean isValidItem(final ItemStack stack) {
    	return true;
    }
	
	@Override
	public boolean hasFastRenderer() {
        return true;
    }
	
	@Override
    public TickRateModulation tickingRequest(final IGridNode node, final int ticksSinceLast) {
        if (refreshNetworkState()) {
            markForUpdate();
        }
        
        if (isActive() && getWorld().isBlockIndirectlyGettingPowered(pos) > 0) {
        	updateState(CondenserState.IDLE);
            return TickRateModulation.IDLE;
        }
        
        importItems();

        return super.tickingRequest(node, ticksSinceLast);
        
	}
	
	public void toggleSide(final EnumFacing side) {
        sides.put(side, sides.get(side).getNext());
        markForUpdate();
    }

    public SideSetting getSide(final EnumFacing side) {
        return sides.get(side);
    }
    
    @Override
    protected void getPacketData(final NBTTagCompound nbttagcompound) {
        super.getPacketData(nbttagcompound);
        final NBTTagCompound list = new NBTTagCompound();
        for (final EnumFacing side : sides.keySet()) {
            list.setInteger(side.name(), sides.get(side).ordinal());
        }
        nbttagcompound.setTag(SIDE_TAG, list);
    }

    @Override
    protected boolean readPacketData(final NBTTagCompound nbttagcompound) {
        boolean flag = super.readPacketData(nbttagcompound);
        final NBTTagCompound list = (NBTTagCompound) nbttagcompound.getTag(SIDE_TAG);
        for (final EnumFacing side : sides.keySet()) {
            final SideSetting newData = SideSetting.values()[list.getInteger(side.name())];
            if (newData != sides.get(side)) {
                sides.put(side, newData);
                flag = true;
            }
            
        }
        return flag;
    }
    
    @Override
    public void readFromNBT(final NBTTagCompound data) {
        super.readFromNBT(data);
        final NBTTagCompound list = (NBTTagCompound) data.getTag(SIDE_TAG);
        if (list != null) {
            for (final EnumFacing side : sides.keySet()) {
                sides.put(side, SideSetting.values()[list.getInteger(side.name())]);
            }
        }
    }
	
	@Override
    public NBTTagCompound writeToNBT(final NBTTagCompound data) {
        super.writeToNBT(data);
        final NBTTagCompound list = new NBTTagCompound();
        for (final EnumFacing side : sides.keySet()) {
            list.setInteger(side.name(), sides.get(side).ordinal());
        }
        data.setTag(SIDE_TAG, list);
        return data;
    }
	
	protected int itemsToTransfer() {
        return 16;
    }
	
	@Override
	protected ItemStack ejectItem(final ItemStack stack) {
		if (stack.isEmpty()) {
			return stack;
		}
		final int numItems = Math.min(itemsToTransfer(), stack.getCount());
		final int overflow = stack.getCount() - numItems;
		
		ItemStack toStore = ItemHandlerHelper.copyStackWithSize(stack, numItems);
        for (final EnumFacing side : sides.keySet()) {
        	if (sides.get(side) != SideSetting.OUTPUT) {
                continue;
            }
        	TileEntity tile = getWorld().getTileEntity(pos.offset(side));
        	if (tile != null && tile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side.getOpposite())) {
        		IItemHandler tileInv = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side.getOpposite());
        		toStore = ItemHandlerHelper.insertItemStacked(tileInv, toStore, false);
        		if (toStore.isEmpty()) {
        			break;
        		}
        	}
        }
        
        if (overflow > 0) {
        	toStore.grow(overflow);
        }
        
        
        return toStore.isEmpty() ? toStore : super.ejectItem(toStore);        
	}
	
	protected void importItems() {
        int numItems = itemsToTransfer();
        for (final EnumFacing side : sides.keySet()) {
            if (sides.get(side) != SideSetting.INPUT) {
                continue;
            }
            TileEntity tile = getWorld().getTileEntity(pos.offset(side));
            if (tile != null && tile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side.getOpposite())) {
            	IItemHandler tileInv = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side.getOpposite());
            	numItems -= InvUtils.extractWithCount(inv, tileInv, numItems);
            }
            if (numItems <= 0) {
            	break;
            }
        }
        
	}
	
}
