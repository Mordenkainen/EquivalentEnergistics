package com.mordenkainen.equivalentenergistics.items.itemblocks;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockMulti extends ItemBlock {

    public ItemBlockMulti(final Block block) {
        super(block);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }

    @Override
    public int getMetadata(final int damage) {
        return damage;
    }

    @Override
    public String getUnlocalizedName(final ItemStack stack) {
        return super.getUnlocalizedName(stack) + "_" + stack.getMetadata();
    }

}
