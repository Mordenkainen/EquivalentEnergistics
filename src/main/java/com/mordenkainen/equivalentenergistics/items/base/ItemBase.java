package com.mordenkainen.equivalentenergistics.items.base;

import com.mordenkainen.equivalentenergistics.EquivalentEnergistics;
import com.mordenkainen.equivalentenergistics.core.Reference;

import net.minecraft.item.Item;

public abstract class ItemBase extends Item {

    protected String name;

    public ItemBase(String name) {
        this.name = name;
        setUnlocalizedName(Reference.MOD_ID + ":" + name);
        setCreativeTab(EquivalentEnergistics.tabEE);
        setRegistryName(name);
    }

    public void registerItemModel() {
        EquivalentEnergistics.proxy.registerItemRenderer(this, 0, name);
    }

}
