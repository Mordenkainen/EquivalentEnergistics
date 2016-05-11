package com.mordenkainen.equivalentenergistics.integration.ae2.tiles;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public abstract class TileNetworkInv extends TileNetworkBase implements IInventory {

	public TileNetworkInv(ItemStack repItem) {
		super(repItem);
	}

	public abstract IInventory getInventory();
	
	@Override
	public int getSizeInventory() {
		return getInventory().getSizeInventory();
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return getInventory().getStackInSlot(slot);
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		return getInventory().decrStackSize(slot, amount);
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		return getInventory().getStackInSlotOnClosing(slot);
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		getInventory().setInventorySlotContents(slot, stack);
	}

	@Override
	public String getInventoryName() {
		return getInventory().getInventoryName();
	}

	@Override
	public boolean hasCustomInventoryName() {
		return getInventory().hasCustomInventoryName();
	}

	@Override
	public int getInventoryStackLimit() {
		return getInventory().getInventoryStackLimit();
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return getInventory().isUseableByPlayer(player);
	}

	@Override
	public void openInventory() {
		getInventory().openInventory();
	}

	@Override
	public void closeInventory() {
		getInventory().closeInventory();
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		return getInventory().isItemValidForSlot(slot, stack);
	}
}
