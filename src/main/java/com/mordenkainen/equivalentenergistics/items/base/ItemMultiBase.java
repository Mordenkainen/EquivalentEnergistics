package com.mordenkainen.equivalentenergistics.items.base;

import com.mordenkainen.equivalentenergistics.EquivalentEnergistics;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public abstract class ItemMultiBase extends ItemBase {

    public int itemCount;

    public ItemMultiBase(final String name, final int count) {
        super(name);
        itemCount = count;
        setHasSubtypes(true);
    }

    @Override
    public void getSubItems(final CreativeTabs tab, final NonNullList<ItemStack> items) {
        if (this.isInCreativeTab(tab)) {
            for (int i = 0; i < itemCount; i++) {
                items.add(new ItemStack(this, 1, i));
            }
        }
    }

    @Override
    public String getTranslationKey(final ItemStack stack) {
        return super.getTranslationKey(stack) + "_" + stack.getMetadata();
    }

    @Override
    public void registerItemModel() {
        for (int i = 0; i < itemCount; i++) {
            EquivalentEnergistics.proxy.registerItemRenderer(this, i, name + "_" + i);
        }
    }

}
