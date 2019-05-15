package com.mordenkainen.equivalentenergistics.blocks.base.block;

import java.util.ArrayList;
import java.util.List;

import com.mordenkainen.equivalentenergistics.EquivalentEnergistics;
import com.mordenkainen.equivalentenergistics.integration.ae2.NetworkLights;
import com.mordenkainen.equivalentenergistics.integration.ae2.grid.IAEProxyHost;
import com.mordenkainen.equivalentenergistics.integration.ae2.tiles.TileAEBase;
import com.mordenkainen.equivalentenergistics.util.CommonUtils;
import com.mordenkainen.equivalentenergistics.util.IDropItems;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class BlockMultiAE extends BlockMultiTile {

    public static final PropertyEnum<NetworkLights> LIGHTS = PropertyEnum.create("lights", NetworkLights.class);

    public BlockMultiAE(final Material material, final String name, final int count) {
        super(material, name, count);
        multiBlockState = new BlockStateContainer(this, new IProperty[] {type, LIGHTS});
        setDefaultState(multiBlockState.getBaseState().withProperty(LIGHTS, NetworkLights.NONE));
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
    public BlockRenderLayer getRenderLayer() {
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
        final TileAEBase tile = (TileAEBase) world.getTileEntity(pos);
        if (tile != null) {
            return state.withProperty(LIGHTS, tile.isPowered() ? NetworkLights.POWERED : NetworkLights.NONE);
        }
        return state;
    }
    
    @Override
    public void onBlockPlacedBy(final World world, final BlockPos pos, final IBlockState state, final EntityLivingBase placer, final ItemStack stack) {
        final IAEProxyHost tile = CommonUtils.getTE(IAEProxyHost.class, world, pos);

        if (tile != null && placer instanceof EntityPlayer) {
            tile.setOwner((EntityPlayer) placer);
        }
    }

    @Override
    public void breakBlock(final World world, final BlockPos pos, final IBlockState state) {
        if (!world.isRemote) {
            final IDropItems tile = CommonUtils.getTE(IDropItems.class, world, pos);

            if (tile != null) {
                final List<ItemStack> drops = new ArrayList<ItemStack>();
                tile.getDrops(world, pos, drops);

                for (final ItemStack drop : drops) {
                    CommonUtils.spawnEntItem(world, pos, drop);
                }
            }
        }

        super.breakBlock(world, pos, state);
    }

}
