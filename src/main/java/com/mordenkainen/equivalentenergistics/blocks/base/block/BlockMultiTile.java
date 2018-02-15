package com.mordenkainen.equivalentenergistics.blocks.base.block;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class BlockMultiTile extends BlockMulti implements ITileEntityProvider {

    public BlockMultiTile(final Material material, final String name, final int count) {
        super(material, name, count);
        isBlockContainer = true;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean eventReceived(final IBlockState state, final World world, final BlockPos pos, final int id, final int param) {
        super.eventReceived(state, world, pos, id, param);
        final TileEntity tileentity = world.getTileEntity(pos);
        return tileentity != null && tileentity.receiveClientEvent(id, param);
    }

}
