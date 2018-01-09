package com.mordenkainen.equivalentenergistics.util;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

public final class InvUtils {

	private InvUtils() {}
	
	public static boolean isFull(final IItemHandler itemHandler) {
        for (int slot = 0; slot < itemHandler.getSlots(); slot++) {
        	final ItemStack stackInSlot = itemHandler.getStackInSlot(slot);
            if (stackInSlot.isEmpty() || stackInSlot.getCount() != stackInSlot.getMaxStackSize()) {
                return false;
            }
        }
        return true;
    }
	
	public static boolean isEmpty(final IItemHandler itemHandler) {
        for (int slot = 0; slot < itemHandler.getSlots(); slot++) {
        	final ItemStack stackInSlot = itemHandler.getStackInSlot(slot);
            if (stackInSlot.getCount() > 0) {
                return false;
            }
        }
        return true;
    }

	public static ItemStack filterForEmpty(final ItemStack stack) {
	    return stack.getCount() <= 0 ? ItemStack.EMPTY : stack;
	}

	public static int extractWithCount(final IItemHandler dest, final IItemHandler src, final int count) {
		int leftToMove = count;
		for (int i = 0; i < dest.getSlots() && leftToMove > 0; i++) {
			final ItemStack destStack = dest.getStackInSlot(i);
			if (!destStack.isEmpty() && (!destStack.isStackable() || destStack.getCount() >= destStack.getMaxStackSize())) {
				continue;
			}
			
			for (int j = 0; j < src.getSlots() && leftToMove > 0; j++) {
				final ItemStack srcStack = src.getStackInSlot(j);
				if (!srcStack.isEmpty() && InvUtils.willStack(destStack, srcStack)) {
					final int toMove = Math.min(leftToMove, Math.min(srcStack.getCount(), destStack.getMaxStackSize() - destStack.getCount()));
					dest.insertItem(i, ItemHandlerHelper.copyStackWithSize(srcStack, toMove), false);
					src.extractItem(j, toMove, false);
					leftToMove -= toMove;
				}
			}
		}
		
		return count - leftToMove;
	}

	public static boolean willStack(final ItemStack dest, final ItemStack src) {
		if(dest.isEmpty() && !src.isEmpty()) {
			return true;
		}else if (dest.getItem() != src.getItem()) {
	        return false;
	    } else if (dest.getMetadata() != src.getMetadata()) {
	        return false;
	    } else {
	        return ItemStack.areItemStackTagsEqual(dest, src);
	    }
	}
	
	public static List<ItemStack> getInvAsList(final IItemHandler inv) {
        final List<ItemStack> output = new ArrayList<ItemStack>();
        for (int slot = 0; slot < inv.getSlots(); slot++) {
            final ItemStack item = inv.getStackInSlot(slot);

            if (!item.isEmpty()) {
                output.add(item);
            }
        }
        return output;
    }
	
}
