package com.mordenkainen.equivalentenergistics.items.base;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public abstract class ItemMultiBase extends ItemBase {

    public int itemCount;

    public ItemMultiBase(final int count) {
        super();
        itemCount = count;
        setHasSubtypes(true);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(final Item item, final CreativeTabs tab, final List list) {
        for (int i = 0; i < itemCount; i++) {
            list.add(new ItemStack(item, 1, i));
        }
    }

    @Override
    public String getUnlocalizedName(final ItemStack stack) {
        return super.getUnlocalizedName() + "." + stack.getItemDamage();
    }

}
