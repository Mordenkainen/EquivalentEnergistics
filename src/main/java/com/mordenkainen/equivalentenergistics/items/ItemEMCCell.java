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
		AEApi.instance().registries().cell().addCellHandler( this );

		// Set max stack size to 1
		this.setMaxStackSize( 1 );

		// No damage
		this.setMaxDamage( 0 );
	}
	
	@Override
	public boolean isCell(ItemStack stack) {
		return stack.getItem() == this;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public IMEInventoryHandler getCellInventory(ItemStack stack, ISaveProvider host, StorageChannel channel) {
		if((channel != StorageChannel.ITEMS) || !(stack.getItem() instanceof ItemEMCCell)) {
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
	public void openChestGui(EntityPlayer player, IChestOrDrive chest, ICellHandler cellHandler, IMEInventoryHandler inv, ItemStack is, StorageChannel chan) {}

	@SuppressWarnings("rawtypes")
	@Override
	public int getStatusForCell(ItemStack is, IMEInventory handler) {
		// TODO return cell status
		return 1;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public double cellIdleDrain(ItemStack is, IMEInventory handler) {
		//TODO Cell cost
		return 0;
	}

}
