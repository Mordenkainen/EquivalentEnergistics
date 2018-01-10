package com.mordenkainen.equivalentenergistics.items;

import com.mordenkainen.equivalentenergistics.core.Names;
import com.mordenkainen.equivalentenergistics.items.base.ItemMultiBase;

import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;

public class ItemStorageComponent extends ItemMultiBase {

    public ItemStorageComponent() {
        super(Names.COMPONENT, 8);
    }

    @Override
    public EnumRarity getRarity(final ItemStack stack) {
        return EnumRarity.values()[stack.getItemDamage() / 2];
    }

}
