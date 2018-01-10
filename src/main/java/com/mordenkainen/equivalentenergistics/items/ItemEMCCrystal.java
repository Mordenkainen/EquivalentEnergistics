package com.mordenkainen.equivalentenergistics.items;

import com.mordenkainen.equivalentenergistics.core.Names;
import com.mordenkainen.equivalentenergistics.items.base.ItemMultiBase;

public class ItemEMCCrystal extends ItemMultiBase {

    final public static float[] CRYSTAL_VALUES = { 1, 64, 4096, 262144, 16777216 };

    public ItemEMCCrystal() {
        super(Names.CRYSTAL, 5);
    }

}
