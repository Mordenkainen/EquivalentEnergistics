package com.mordenkainen.equivalentenergistics.util.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public interface IInventoryInt extends IInventory {

    String INVSLOTS = "items";

    <T extends InternalInventory> T getInventory();

    @Override
    default int getSizeInventory() {
        return getInventory().getSizeInventory();
    }

    @Override
    default ItemStack getStackInSlot(final int slot) {
        return getInventory().getStackInSlot(slot);
    }

    @Override
    default ItemStack decrStackSize(final int slot, final int amount) {
        return getInventory().decrStackSize(slot, amount);
    }

    @Override
    default ItemStack getStackInSlotOnClosing(final int slot) {
        return getInventory().getStackInSlotOnClosing(slot);
    }

    @Override
    default void setInventorySlotContents(final int slot, final ItemStack stack) {
        getInventory().setInventorySlotContents(slot, stack);
    }

    @Override
    default String getInventoryName() {
        return getInventory().getInventoryName();
    }

    @Override
    default boolean hasCustomInventoryName() {
        return getInventory().hasCustomInventoryName();
    }

    @Override
    default int getInventoryStackLimit() {
        return getInventory().getInventoryStackLimit();
    }

    @Override
    default boolean isUseableByPlayer(final EntityPlayer player) {
        return getInventory().isUseableByPlayer(player);
    }

    @Override
    default void openInventory() {
        getInventory().openInventory();
    }

    @Override
    default void closeInventory() {
        getInventory().closeInventory();
    }

    @Override
    default boolean isItemValidForSlot(final int slot, final ItemStack stack) {
        return getInventory().isItemValidForSlot(slot, stack);
    }

    default void readFromNBT(final NBTTagCompound data) {
        getInventory().loadFromNBT(data, INVSLOTS);
    }

    default void writeToNBT(final NBTTagCompound data) {
        getInventory().saveToNBT(data, INVSLOTS);
    }
}
