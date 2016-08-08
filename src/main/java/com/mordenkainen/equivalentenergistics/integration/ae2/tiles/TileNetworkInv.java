package com.mordenkainen.equivalentenergistics.integration.ae2.tiles;

import com.mordenkainen.equivalentenergistics.util.inventory.IInventoryInt;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public abstract class TileNetworkInv extends TileNetworkBase implements IInventoryInt {

	public TileNetworkInv(final ItemStack repItem) {
		super(repItem);
	}
	
	// TileEntity Overrides
	// ------------------------
	@Override
	public void readFromNBT(final NBTTagCompound data) {
		super.readFromNBT(data);
		getInventory().loadFromNBT(data, INVSLOTS);
		IInventoryInt.super.readFromNBT(data);
	}
	
	@Override
	public void writeToNBT(final NBTTagCompound data) {
		super.writeToNBT(data);
		IInventoryInt.super.writeToNBT(data);
	}
	// ------------------------
	
}
