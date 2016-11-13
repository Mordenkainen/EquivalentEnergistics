package com.mordenkainen.equivalentenergistics.util.inventory;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

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
}
