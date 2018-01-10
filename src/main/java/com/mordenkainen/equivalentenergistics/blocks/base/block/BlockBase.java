package com.mordenkainen.equivalentenergistics.blocks.base.block;

import com.mordenkainen.equivalentenergistics.EquivalentEnergistics;
import com.mordenkainen.equivalentenergistics.core.Reference;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;

public abstract class BlockBase extends Block {

    protected String name;

    public BlockBase(Material material, String name) {
        super(material);
        this.name = name;
        setUnlocalizedName(Reference.MOD_ID + ":" + name);
        setRegistryName(name);
        setCreativeTab(EquivalentEnergistics.tabEE);
    }

    public void registerItemModel(Item itemBlock) {
        EquivalentEnergistics.proxy.registerItemRenderer(itemBlock, 0, name);
    }

    public Item createItemBlock() {
        return new ItemBlock(this).setRegistryName(getRegistryName());
    }

}
