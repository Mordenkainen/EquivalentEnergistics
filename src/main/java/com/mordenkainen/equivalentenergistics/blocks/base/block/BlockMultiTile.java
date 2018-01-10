package com.mordenkainen.equivalentenergistics.blocks.base.block;

import javax.annotation.Nullable;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldNameable;
import net.minecraft.world.World;

public abstract class BlockMultiTile extends BlockMulti implements ITileEntityProvider {

    public BlockMultiTile(final Material material, final String name, final int count) {
        super(material, name, count);
        hasTileEntity = true;
    }

    @Override
    public void harvestBlock(final World world, final EntityPlayer player, final BlockPos pos, final IBlockState state, final @Nullable TileEntity te, final ItemStack stack) {
        if (te instanceof IWorldNameable && ((IWorldNameable)te).hasCustomName()) {
            player.addStat(StatList.getBlockStats(this));
            player.addExhaustion(0.005F);

            if (world.isRemote) {
                return;
            }

            final int i = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, stack);
            final Item item = this.getItemDropped(state, world.rand, i);

            if (item == Items.AIR) {
                return;
            }

            final ItemStack itemstack = new ItemStack(item, this.quantityDropped(world.rand));
            itemstack.setStackDisplayName(((IWorldNameable)te).getName());
            spawnAsEntity(world, pos, itemstack);
        } else {
            super.harvestBlock(world, player, pos, state, (TileEntity)null, stack);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean eventReceived(final IBlockState state, final World world, final BlockPos pos, final int id, final int param) {
        super.eventReceived(state, world, pos, id, param);
        final TileEntity tileentity = world.getTileEntity(pos);
        return tileentity == null ? false : tileentity.receiveClientEvent(id, param);
    }

}
