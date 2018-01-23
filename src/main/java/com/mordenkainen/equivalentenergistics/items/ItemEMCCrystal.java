package com.mordenkainen.equivalentenergistics.items;

import com.mordenkainen.equivalentenergistics.core.Names;
import com.mordenkainen.equivalentenergistics.items.base.ItemMultiBase;

import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;

public class ItemEMCCrystal extends ItemMultiBase {

    final public static double[] CRYSTAL_VALUES = { 1, 64, 4096, 262144, 16777216 };

    public ItemEMCCrystal() {
        super(Names.CRYSTAL, 5);
    }

    @Override
    public EnumRarity getRarity(final ItemStack stack) {
        final int meta = stack.getMetadata();
        return meta <= 1 ? EnumRarity.COMMON : EnumRarity.values()[meta - 1];
    }
    
}
