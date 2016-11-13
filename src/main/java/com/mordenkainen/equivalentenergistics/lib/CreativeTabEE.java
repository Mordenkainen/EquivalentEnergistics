package com.mordenkainen.equivalentenergistics.lib;

import com.mordenkainen.equivalentenergistics.registries.ItemEnum;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class CreativeTabEE extends CreativeTabs {

    public CreativeTabEE(final int tabID, final String label) {
        super(tabID, label);
    }

    @Override
    public Item getTabIconItem() {
        return null;
    }

    @Override
    public ItemStack getIconItemStack() {
        return ItemEnum.EMCCRYSTAL.getDamagedStack(2);
    }

}
