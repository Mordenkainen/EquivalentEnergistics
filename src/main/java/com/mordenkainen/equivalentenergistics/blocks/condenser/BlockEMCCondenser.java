package com.mordenkainen.equivalentenergistics.blocks.condenser;

import com.mordenkainen.equivalentenergistics.blocks.base.block.BlockMultiAE;
import com.mordenkainen.equivalentenergistics.blocks.base.tile.TE;
import com.mordenkainen.equivalentenergistics.blocks.base.tile.TEList;
import com.mordenkainen.equivalentenergistics.blocks.condenser.tiles.TileEMCCondenser;
import com.mordenkainen.equivalentenergistics.blocks.condenser.tiles.TileEMCCondenserAdv;
import com.mordenkainen.equivalentenergistics.blocks.condenser.tiles.TileEMCCondenserExt;
import com.mordenkainen.equivalentenergistics.blocks.condenser.tiles.TileEMCCondenserUlt;
import com.mordenkainen.equivalentenergistics.core.Names;
import com.mordenkainen.equivalentenergistics.core.Reference;
import com.mordenkainen.equivalentenergistics.integration.ae2.NetworkLights;
import com.mordenkainen.equivalentenergistics.util.CommonUtils;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

@TEList({
    @TE(tileEntityClass = TileEMCCondenser.class, registryName = Reference.MOD_ID + ".emc_condenser"),
    @TE(tileEntityClass = TileEMCCondenserAdv.class, registryName = Reference.MOD_ID + ".emc_condenser_adv"),
    @TE(tileEntityClass = TileEMCCondenserExt.class, registryName = Reference.MOD_ID + ".emc_condenser_ext"),
    @TE(tileEntityClass = TileEMCCondenserUlt.class, registryName = Reference.MOD_ID + ".emc_condenser_ult")
})
public class BlockEMCCondenser extends BlockMultiAE {

    public BlockEMCCondenser() {
        super(Material.ROCK, Names.CONDENSER, 4);
        setHardness(1.5f);
        blockSoundType = SoundType.STONE;
    }

    @Deprecated
    @Override
    public IBlockState getActualState(final IBlockState state, final IBlockAccess world, final BlockPos pos) {
        final IBlockState tmpState = super.getActualState(state, world, pos);
        final TileEMCCondenser tile = CommonUtils.getTE(world, pos);
        if (tile != null && tile.getState().isError()) {
            return tmpState.withProperty(LIGHTS, NetworkLights.ERROR);
        }
        return tmpState;
    }

    @Override
    public TileEntity createNewTileEntity(final World world, final int meta) {
        switch (meta) {
        case 0:
            return new TileEMCCondenser();
        case 1:
            return new TileEMCCondenserAdv();
        case 2:
            return new TileEMCCondenserExt();
        default:
            return new TileEMCCondenserUlt();
        }
    }

    @Override
    public boolean hasComparatorInputOverride(final IBlockState state) {
        return state.getValue(type) > 0;
    }

    @Override
    public int getComparatorInputOverride(final IBlockState blockState, final World world, final BlockPos pos) {
        if (blockState.getActualState(world, pos).getValue(LIGHTS) == NetworkLights.NONE) {
            return 0;
        }

        final TileEMCCondenserAdv tile = CommonUtils.getTE(world, pos);
        
        switch (tile.getState()) {
        case ACTIVE:
            return 2;
        case IDLE:
            return 1;
        case NOEMCSTORAGE:
            return 3;
        case NOITEMSTORAGE:
            return 4;
        case NOPOWER:
            return 5;
        default:
            return 0;
        }
    }

    @Override
    public boolean canConnectRedstone(final IBlockState state, final IBlockAccess world, final BlockPos pos, final EnumFacing side) {
        return world.getBlockState(pos).getValue(type) > 0;
    }

    @Override
    public boolean onBlockActivated(final World world, final BlockPos pos, final IBlockState state, final EntityPlayer player, final EnumHand hand, final EnumFacing facing, final float hitX, final float hitY, final float hitZ) {
        if (player == null) {
            return false;
        }

        if (player.getHeldItem(hand) == ItemStack.EMPTY) {
            final TileEMCCondenserExt tileCondenser = CommonUtils.getTE(world, pos);
            if (tileCondenser != null && !world.isRemote) {
                tileCondenser.toggleSide(facing);
            }
            return true;
        }

        return false;
    }

}
