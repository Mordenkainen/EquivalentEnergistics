package com.mordenkainen.equivalentenergistics.util.inventory;

import java.util.stream.IntStream;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;

public class InventoryAdapter implements ISidedInventory {

    private final IInventory inv;
    private final int[] slots;

    public InventoryAdapter(final IInventory inv) {
        this.inv = inv;
        slots = IntStream.rangeClosed(0, inv.getSizeInventory() - 1).toArray();
    }

    @Override
    public int getSizeInventory() {
        return inv.getSizeInventory();
    }

    @Override
    public ItemStack getStackInSlot(final int slot) {
        return inv.getStackInSlot(slot);
    }

    @Override
    public ItemStack decrStackSize(final int slot, final int amount) {
        return inv.decrStackSize(slot, amount);
    }

    @Override
    public ItemStack getStackInSlotOnClosing(final int slot) {
        return inv.getStackInSlotOnClosing(slot);
    }

    @Override
    public void setInventorySlotContents(final int slot, final ItemStack stack) {
        inv.setInventorySlotContents(slot, stack);
    }

    @Override
    public String getInventoryName() {
        return inv.getInventoryName();
    }

    @Override
    public boolean hasCustomInventoryName() {
        return inv.hasCustomInventoryName();
    }

    @Override
    public int getInventoryStackLimit() {
        return inv.getInventoryStackLimit();
    }

    @Override
    public void markDirty() {
        inv.markDirty();
    }

    @Override
    public boolean isUseableByPlayer(final EntityPlayer player) {
        return inv.isUseableByPlayer(player);
    }

    @Override
    public void openInventory() {
        inv.openInventory();
    }

    @Override
    public void closeInventory() {
        inv.closeInventory();
    }

    @Override
    public boolean isItemValidForSlot(final int slot, final ItemStack stack) {
        return inv.isItemValidForSlot(slot, stack);
    }

    @Override
    public int[] getAccessibleSlotsFromSide(final int side) {
        return slots.clone();
    }

    @Override
    public boolean canInsertItem(final int slot, final ItemStack stack, final int side) {
        return isItemValidForSlot(slot, stack);
    }

    @Override
    public boolean canExtractItem(final int slot, final ItemStack stack, final int side) {
        return true;
    }

    public static ISidedInventory getAdapter(final IInventory inv) {
        if (inv instanceof ISidedInventory) {
            return (ISidedInventory) inv;
        } else {
            return new InventoryAdapter(inv);
        }
    }
}
