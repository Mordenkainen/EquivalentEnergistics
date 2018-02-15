package com.mordenkainen.equivalentenergistics.items;

import java.util.List;

import com.mordenkainen.equivalentenergistics.core.Names;
import com.mordenkainen.equivalentenergistics.items.base.ItemMultiBase;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemMisc extends ItemMultiBase {

    public ItemMisc() {
        super(Names.MISC, 2);
    }

    @Override
    public void getSubItems(final Item item, final CreativeTabs tab, final List<ItemStack> items) {
        items.add(new ItemStack(item, 1, 0));
    }
}
