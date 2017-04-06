package com.mordenkainen.equivalentenergistics.blocks.common;

import java.util.ArrayList;
import java.util.List;

import com.mordenkainen.equivalentenergistics.integration.ae2.grid.IAEProxyHost;
import com.mordenkainen.equivalentenergistics.util.CommonUtils;
import com.mordenkainen.equivalentenergistics.util.IDropItems;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public abstract class BlockContainerBase extends BlockContainer {

    public BlockContainerBase(final Material material) {
        super(material);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(final IIconRegister register) {}

    @Override
    public void breakBlock(final World world, final int x, final int y, final int z, final Block block, final int metaData) {
        if (!world.isRemote) {
            final IDropItems tile = CommonUtils.getTE(IDropItems.class, world, x, y, z);

            if (tile != null) {
                final List<ItemStack> drops = new ArrayList<ItemStack>();
                tile.getDrops(world, x, y, z, drops);

                for (final ItemStack drop : drops) {
                    CommonUtils.spawnEntItem(world, x, y, z, drop);
                }
            }
        }

        super.breakBlock(world, x, y, z, block, metaData);
    }

    @Override
    public void onBlockPlacedBy(final World world, final int x, final int y, final int z, final EntityLivingBase player, final ItemStack itemStack) {
        final IAEProxyHost tile = CommonUtils.getTE(IAEProxyHost.class, world, x, y, z);

        if (tile != null && player instanceof EntityPlayer) {
            tile.setOwner((EntityPlayer) player);
        }
    }

}
