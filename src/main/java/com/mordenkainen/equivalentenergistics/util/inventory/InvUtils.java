package com.mordenkainen.equivalentenergistics.util.inventory;

import java.util.ArrayList;
import java.util.List;

import com.mordenkainen.equivalentenergistics.util.CommonUtils;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

public final class InvUtils {

    private InvUtils() {}

    public static List<ItemStack> getInvAsList(final IInventory inv) {
        final List<ItemStack> output = new ArrayList<ItemStack>();
        for (int slot = 0; slot < inv.getSizeInventory(); slot++) {
            final ItemStack item = inv.getStackInSlot(slot);

            if (item != null) {
                output.add(item);
            }
        }
        return output;
    }

    public static int importFromAdjInv(final ForgeDirection side, final IInventory sourceInv, final IInventory destInv, final int destSlot, final int maxItems) {
        int remainingItems = maxItems;
        final ISidedInventory inv = InventoryAdapter.getAdapter(sourceInv);
        for (final int slot : inv.getAccessibleSlotsFromSide(side.ordinal())) {
            ItemStack destStack = destInv.getStackInSlot(destSlot);
            ItemStack sourceStack = inv.getStackInSlot(slot);
            if (sourceStack == null || !inv.canExtractItem(slot, sourceStack, side.getOpposite().ordinal())) {
                continue;
            }

            if (destStack == null) {
                final int toMove = Math.min(sourceStack.stackSize, remainingItems);
                sourceStack.stackSize -= toMove;
                destStack = sourceStack.copy();
                destStack.stackSize = toMove;
                destInv.setInventorySlotContents(destSlot, CommonUtils.filterForEmpty(destStack));
                inv.setInventorySlotContents(slot, CommonUtils.filterForEmpty(sourceStack));
                remainingItems -= toMove;
            } else if (CommonUtils.isSameItem(destStack, sourceStack)) {
                final int toMove = Math.min(Math.min(sourceStack.stackSize, destStack.getMaxStackSize() - destStack.stackSize), remainingItems);
                sourceStack.stackSize -= toMove;
                destStack.stackSize += toMove;
                destInv.setInventorySlotContents(destSlot, CommonUtils.filterForEmpty(destStack));
                inv.setInventorySlotContents(slot, CommonUtils.filterForEmpty(sourceStack));
                remainingItems -= toMove;
            }

            if (remainingItems <= 0) {
                break;
            }
        }
        return maxItems - remainingItems;
    }

    public static int ejectStack(final ItemStack sourceStack, final IInventory destInv, final ForgeDirection side, final int maxItems) {
        int remainingItems = maxItems;
        final ISidedInventory inv = InventoryAdapter.getAdapter(destInv);
        for (final int slot : inv.getAccessibleSlotsFromSide(side.ordinal())) {
            if (inv.canInsertItem(slot, sourceStack, side.ordinal())) {
                ItemStack destStack = inv.getStackInSlot(slot);
                if (destStack == null) {
                    final int toMove = Math.min(sourceStack.stackSize, remainingItems);
                    destStack = sourceStack.copy();
                    destStack.stackSize = toMove;
                    sourceStack.stackSize -= toMove;
                    remainingItems -= toMove;
                    inv.setInventorySlotContents(slot, destStack);
                } else if (CommonUtils.isSameItem(destStack, sourceStack)) {
                    final int toMove = Math.min(Math.min(sourceStack.stackSize, destStack.getMaxStackSize() - destStack.stackSize), remainingItems);
                    destStack.stackSize += toMove;
                    sourceStack.stackSize -= toMove;
                    remainingItems -= toMove;
                    inv.setInventorySlotContents(slot, destStack);
                }
            }
            if (remainingItems <= 0) {
                break;
            }
        }
        return maxItems - remainingItems;
    }
}
