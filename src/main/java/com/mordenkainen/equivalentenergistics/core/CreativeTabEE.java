package com.mordenkainen.equivalentenergistics.core;

import com.mordenkainen.equivalentenergistics.items.ModItems;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class CreativeTabEE extends CreativeTabs {

    public CreativeTabEE(final int tabID, final String label) {
        super(tabID, label);
    }

    @Override
    public Item getTabIconItem() {
        return ModItems.CRYSTAL;
    }

    @Override
    public int getIconItemDamage() {
        return 4;
    }
    
    

}
