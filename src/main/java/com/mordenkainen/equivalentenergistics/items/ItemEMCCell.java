package com.mordenkainen.equivalentenergistics.items;

import appeng.api.AEApi;
import appeng.api.implementations.tiles.IChestOrDrive;
import appeng.api.storage.ICellHandler;
import appeng.api.storage.IMEInventory;
import appeng.api.storage.IMEInventoryHandler;
import appeng.api.storage.ISaveProvider;
import appeng.api.storage.StorageChannel;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class ItemEMCCell extends Item implements ICellHandler {

	public ItemEMCCell() {
		super();
		
		AEApi.instance().registries().cell().addCellHandler( this );

		// Set max stack size to 1
		this.setMaxStackSize( 1 );

		// No damage
		this.setMaxDamage( 0 );
	}
	
	@Override
	public boolean isCell(final ItemStack stack) {
		return stack.getItem() == this;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public IMEInventoryHandler getCellInventory(final ItemStack stack, final ISaveProvider host, final StorageChannel channel) {
		if(channel != StorageChannel.ITEMS || !(stack.getItem() instanceof ItemEMCCell)) {
			return null;
		}
		
		return new HandlerEMCCell(stack, host);
	}

	@Override
	public IIcon getTopTexture_Light() {
		return null;
	}

	@Override
	public IIcon getTopTexture_Medium() {
		return null;
	}

	@Override
	public IIcon getTopTexture_Dark() {
		return null;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void openChestGui(final EntityPlayer player, final IChestOrDrive chest, final ICellHandler cellHandler, final IMEInventoryHandler inv, final ItemStack is, final StorageChannel chan) {}

	@SuppressWarnings("rawtypes")
	@Override
	public int getStatusForCell(final ItemStack is, final IMEInventory handler) {
		// TODO return cell status
		return 1;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public double cellIdleDrain(final ItemStack is, final IMEInventory handler) {
		//TODO Cell cost
		return 0;
	}

}
