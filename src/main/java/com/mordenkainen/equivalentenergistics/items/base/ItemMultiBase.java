package com.mordenkainen.equivalentenergistics.items.base;

import java.util.List;

import com.mordenkainen.equivalentenergistics.EquivalentEnergistics;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public abstract class ItemMultiBase extends ItemBase {

    public int itemCount;

    public ItemMultiBase(final String name, final int count) {
        super(name);
        itemCount = count;
        setHasSubtypes(true);
    }

    @Override
    public void getSubItems(Item item, final CreativeTabs tab, final List<ItemStack> items) {
        for (int i = 0; i < itemCount; i++) {
                items.add(new ItemStack(item, 1, i));
        }
    }

    @Override
    public String getUnlocalizedName(final ItemStack stack) {
        return super.getUnlocalizedName(stack) + "_" + stack.getMetadata();
    }

    @Override
    public void registerItemModel() {
        for (int i = 0; i < itemCount; i++) {
            EquivalentEnergistics.proxy.registerItemRenderer(this, i, name + "_" + i);
        }
    }

}
