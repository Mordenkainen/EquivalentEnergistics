package com.mordenkainen.equivalentenergistics.core;

import com.mordenkainen.equivalentenergistics.items.ModItems;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class CreativeTabEE extends CreativeTabs {

    public CreativeTabEE(final int tabID, final String label) {
        super(tabID, label);
    }

    @Override
    public ItemStack getTabIconItem() {
        return new ItemStack(ModItems.CRYSTAL, 1, 4);
    }

}
