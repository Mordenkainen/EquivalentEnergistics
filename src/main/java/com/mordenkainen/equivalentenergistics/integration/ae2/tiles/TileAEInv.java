package com.mordenkainen.equivalentenergistics.integration.ae2.tiles;

import java.util.List;

import com.mordenkainen.equivalentenergistics.util.IDropItems;
import com.mordenkainen.equivalentenergistics.util.inventory.IInventoryInt;
import com.mordenkainen.equivalentenergistics.util.inventory.InternalInventory;
import com.mordenkainen.equivalentenergistics.util.inventory.InvUtils;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public abstract class TileAEInv extends TileAEBase implements IInventoryInt, IDropItems {

	protected InternalInventory internalInventory;

	public TileAEInv(final ItemStack repItem) {
		super(repItem);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends InternalInventory> T getInventory() {
	    return (T) internalInventory;
	}

	@Override
    public void readFromNBT(final NBTTagCompound data) {
        super.readFromNBT(data);
        IInventoryInt.super.readFromNBT(data);
    }

    @Override
    public void writeToNBT(final NBTTagCompound data) {
        super.writeToNBT(data);
        IInventoryInt.super.writeToNBT(data);
    }
	
    @Override
    public void getDrops(final World world, final int x, final int y, final int z, final List<ItemStack> drops) {
        drops.addAll(InvUtils.getInvAsList(getInventory()));
    }
    
}
