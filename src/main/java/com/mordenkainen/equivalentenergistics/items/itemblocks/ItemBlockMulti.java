package com.mordenkainen.equivalentenergistics.items.itemblocks;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlockWithMetadata;
import net.minecraft.item.ItemStack;

public class ItemBlockMulti extends ItemBlockWithMetadata {

	public ItemBlockMulti(final Block block) {
		super(block, block);
	}

	@Override
	public String getUnlocalizedName(final ItemStack stack)
    {
        return this.getUnlocalizedName() + "." + stack.getItemDamage();
    }
	
}
