package com.mordenkainen.equivalentenergistics.blocks.base.block;

import com.mordenkainen.equivalentenergistics.EquivalentEnergistics;
import com.mordenkainen.equivalentenergistics.blocks.base.tile.TileAEBase;
import com.mordenkainen.equivalentenergistics.integration.ae2.NetworkLights;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class BlockMultiAE extends BlockMultiTile {

    public static final PropertyEnum<NetworkLights> LIGHTS = PropertyEnum.create("lights", NetworkLights.class);

    public BlockMultiAE(final Material material, final String name, final int count) {
        super(material, name, count);
        setDefaultState(getDefaultState().withProperty(LIGHTS, NetworkLights.NONE));
    }

    @Override
    protected BlockStateContainer createRealBlockState() {
        return new BlockStateContainer(this, new IProperty[] {type, LIGHTS});
    }

    @Override
    public IBlockState getStateFromMeta(final int meta) {
        return super.getStateFromMeta(meta).withProperty(LIGHTS, NetworkLights.NONE);
    }

    @Override
    public void registerItemModel(final Item itemBlock) {
        for (int i = 0; i < count; i++) {
            EquivalentEnergistics.proxy.registerItemRenderer(itemBlock, i, name, "lights=none,type=" + i);
        }
    }

    @Override
    public EnumBlockRenderType getRenderType(final IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    @Deprecated
    public boolean isOpaqueCube(final IBlockState state) {
        return false;
    }

    @Override
    @Deprecated
    public boolean isFullCube(final IBlockState state) {
        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean canRenderInLayer(final IBlockState state, final BlockRenderLayer layer) {
        return layer == BlockRenderLayer.TRANSLUCENT;
    }

    @Override
    @Deprecated
    public IBlockState getActualState(final IBlockState state, final IBlockAccess world, final BlockPos pos) {
        TileAEBase tile = (TileAEBase) world.getTileEntity(pos);
        if (tile != null) {
            return state.withProperty(LIGHTS, tile.isPowered() ? NetworkLights.POWERED : NetworkLights.NONE);
        }
        return state;
    }

}
