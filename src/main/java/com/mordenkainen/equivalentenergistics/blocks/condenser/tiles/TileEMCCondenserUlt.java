package com.mordenkainen.equivalentenergistics.blocks.condenser.tiles;

import com.mordenkainen.equivalentenergistics.blocks.ModBlocks;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class TileEMCCondenserUlt extends TileEMCCondenserExt {

    public TileEMCCondenserUlt() {
        super(new ItemStack(Item.getItemFromBlock(ModBlocks.CONDENSER), 1, 3));
    }

    @Override
    protected float getEMCPerTick() {
        return Float.MAX_VALUE;
    }
    
    @Override
    protected int itemsToTransfer() {
        return 256;
    }

}
