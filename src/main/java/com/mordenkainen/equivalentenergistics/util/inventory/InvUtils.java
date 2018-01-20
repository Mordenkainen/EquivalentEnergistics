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
    
    public static boolean isFull(final IInventory inv) {
        for (int slot = 0; slot < inv.getSizeInventory(); slot++) {
            final ItemStack stackInSlot = inv.getStackInSlot(slot);
            if (stackInSlot == null || stackInSlot.stackSize != stackInSlot.getMaxStackSize()) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean isEmpty(final IInventory inv) {
        for (int slot = 0; slot < inv.getSizeInventory(); slot++) {
            final ItemStack stackInSlot = inv.getStackInSlot(slot);
            if (stackInSlot != null) {
                return false;
            }
        }
        return true;
    }
    
    public static int extractWithCount(final ForgeDirection side, final IInventory dest, final IInventory src, final int count) {
        int leftToMove = count;
        final ISidedInventory srcSidedIv = InventoryAdapter.getAdapter(src);
        for (int i = 0; i < dest.getSizeInventory() && leftToMove > 0; i++) {
            final ItemStack destStack = dest.getStackInSlot(i);
            if (destStack != null && (!destStack.isStackable() || destStack.stackSize >= destStack.getMaxStackSize())) {
                continue;
            }
            
            for (final int slot : srcSidedIv.getAccessibleSlotsFromSide(side.ordinal())) {
                final ItemStack srcStack = srcSidedIv.getStackInSlot(slot);
                if (srcStack != null && CommonUtils.willItemsStack(destStack, srcStack) && srcSidedIv.canExtractItem(slot, srcStack, side.getOpposite().ordinal())) {
                    int toMove = 0;
                    if (destStack == null ) {
                        toMove = Math.min(leftToMove, srcStack.stackSize);
                        dest.setInventorySlotContents(i, new ItemStack(srcStack.getItem(), toMove, srcStack.getItemDamage()));
                    } else {
                        toMove = Math.min(leftToMove, Math.min(srcStack.stackSize, destStack.getMaxStackSize() - destStack.stackSize));
                        destStack.stackSize += toMove;
                    }
                    srcStack.stackSize -= toMove;
                    if (srcStack.stackSize <= 0) {
                        srcSidedIv.setInventorySlotContents(slot, null);
                    }
                    leftToMove -= toMove;
                    
                    if (leftToMove <= 0) {
                        break;
                    }
                }
            }
        }

        return count - leftToMove;
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
            if (remainingItems <= 0 || sourceStack.stackSize <= 0) {
                break;
            }
        }
        return maxItems - remainingItems;
    }

    
}
