package com.mordenkainen.equivalentenergistics.tiles.crafter;

import net.minecraft.item.ItemStack;

public interface ICraftingMonitor {

    void craftingFinished(ItemStack outputStack);

}
