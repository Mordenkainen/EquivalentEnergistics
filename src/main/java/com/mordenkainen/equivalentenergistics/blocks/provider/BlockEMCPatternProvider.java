package com.mordenkainen.equivalentenergistics.blocks.provider;

import com.mordenkainen.equivalentenergistics.blocks.base.block.BlockContainerBase;
import com.mordenkainen.equivalentenergistics.blocks.provider.tile.TileEMCPatternProvider;

import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockEMCPatternProvider extends BlockContainerBase {

    public BlockEMCPatternProvider() {
        super(Material.rock);
    }

    @Override
    public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
        return new TileEMCPatternProvider();
    }

}
