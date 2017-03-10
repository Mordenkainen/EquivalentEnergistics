package com.mordenkainen.equivalentenergistics.blocks.crafter.tiles;

import net.minecraft.item.ItemStack;

public interface ICraftingMonitor {

    void craftingFinished(ItemStack outputStack);

}
