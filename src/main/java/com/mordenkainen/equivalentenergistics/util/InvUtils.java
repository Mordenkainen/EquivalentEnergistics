package com.mordenkainen.equivalentenergistics.util;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

public final class InvUtils {

    private InvUtils() {}

    public static List<ItemStack> getInvAsList(final IItemHandler inv) {
        final List<ItemStack> output = new ArrayList<ItemStack>();
        for (int slot = 0; slot < inv.getSlots(); slot++) {
            final ItemStack item = inv.getStackInSlot(slot);

            if (item != null) {
                output.add(item);
            }
        }
        return output;
    }
    
    public static boolean isFull(final IItemHandler itemHandler) {
        for (int slot = 0; slot < itemHandler.getSlots(); slot++) {
            final ItemStack stackInSlot = itemHandler.getStackInSlot(slot);
            if (stackInSlot == null || stackInSlot.stackSize != stackInSlot.getMaxStackSize()) {
                return false;
            }
        }
        return true;
    }

    public static boolean isEmpty(final IItemHandler itemHandler) {
        for (int slot = 0; slot < itemHandler.getSlots(); slot++) {
            final ItemStack stackInSlot = itemHandler.getStackInSlot(slot);
            if (stackInSlot.stackSize > 0) {
                return false;
            }
        }
        return true;
    }
    
    public static int extractWithCount(final IItemHandler dest, final IItemHandler src, final int count) {
        int leftToMove = count;
        for (int i = 0; i < dest.getSlots() && leftToMove > 0; i++) {
            final ItemStack destStack = dest.getStackInSlot(i);
            if (destStack!= null && (!destStack.isStackable() || destStack.stackSize >= destStack.getMaxStackSize())) {
                continue;
            }

            for (int j = 0; j < src.getSlots() && leftToMove > 0; j++) {
                final ItemStack srcStack = src.getStackInSlot(j);
                if (srcStack != null && CommonUtils.willItemsStack(destStack, srcStack)) {
                    final int toMove = Math.min(leftToMove, Math.min(srcStack.stackSize, destStack.getMaxStackSize() - destStack.stackSize));
                    dest.insertItem(i, ItemHandlerHelper.copyStackWithSize(srcStack, toMove), false);
                    src.extractItem(j, toMove, false);
                    leftToMove -= toMove;
                }
            }
        }

        return count - leftToMove;
    }

}
