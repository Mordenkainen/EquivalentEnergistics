package com.mordenkainen.equivalentenergistics.blocks.base.block;

import net.minecraft.block.Block;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

public interface ILayeredBlock {

    int numLayers(Block block, int meta);
    
    int numLayers(IBlockAccess world, Block block, int x, int y, int z, int meta);
    
    IIcon getLayer(Block block, int side, int meta, int layer);
    
    IIcon getLayer(IBlockAccess world, Block block, int x, int y, int z, int side, int meta, int layer);
    
}
