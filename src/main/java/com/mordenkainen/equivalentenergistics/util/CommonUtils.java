package com.mordenkainen.equivalentenergistics.util;

import java.text.DecimalFormat;
import java.util.List;

import com.mordenkainen.equivalentenergistics.EquivalentEnergistics;
import com.mordenkainen.equivalentenergistics.core.config.EqEConfig;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public final class CommonUtils {

    private CommonUtils() {}

    public static boolean destroyAndDrop(final World world, final BlockPos pos) {
        final IBlockState state = world.getBlockState(pos);
        if (!(state.getBlock().isAir(state, world, pos))) {
            if (!world.isRemote) {
                state.getBlock().breakBlock(world, pos, state);
                final List<ItemStack> drops = state.getBlock().getDrops(world, pos, state, 0);
                for (final ItemStack stack : drops) {
                    spawnEntItem(world, pos, stack);
                }
            }
            world.setBlockToAir(pos);

            return true;
        }
        return false;
    }

    public static void spawnEntItem(final World world, final BlockPos pos, final ItemStack item) {
        if (world.getGameRules().getBoolean("doTileDrops") && !world.restoringBlockSnapshots && item != null) {
            final float rx = world.rand.nextFloat() * 0.8F + 0.1F;
            final float ry = world.rand.nextFloat() * 0.8F + 0.1F;
            final float rz = world.rand.nextFloat() * 0.8F + 0.1F;

            final EntityItem entityItem = new EntityItem(world, pos.getX() + rx, pos.getY() + ry, pos.getZ() + rz, new ItemStack(item.getItem(), item.stackSize, item.getItemDamage()));

            if (item.hasTagCompound()) {
                entityItem.getEntityItem().setTagCompound(item.getTagCompound().copy());
            }

            entityItem.motionX = world.rand.nextGaussian() * 0.05F;
            entityItem.motionY = world.rand.nextGaussian() * 0.05F + 0.2000000029802322D;
            entityItem.motionZ = world.rand.nextGaussian() * 0.05F;
            world.spawnEntityInWorld(entityItem);
        }
    }

    @SuppressWarnings("unchecked")
    public static <R extends TileEntity> R getTE(final IBlockAccess world, final BlockPos pos) {
        final TileEntity tile = world instanceof ChunkCache ? ((ChunkCache) world).getTileEntity(pos, Chunk.EnumCreateEntityType.CHECK) : world.getTileEntity(pos);
        try {
            return (R) tile;
        } catch (ClassCastException e) {
            return null;
        }
    }

    public static String formatEMC(final double emc) {
        double displayValue = emc;

        final String[] preFixes = { "K", "M", "B", "T", "P", "T", "P", "E", "Z", "Y" };
        String level = "";
        int offset = 0;
        while (displayValue > 1000 && offset < preFixes.length) {
            displayValue /= 1000;
            level = preFixes[offset++];
        }

        final DecimalFormat formatter = new DecimalFormat("#.###");
        return formatter.format(displayValue) + ' ' + level;
    }

    public static ItemStack filterForEmpty(final ItemStack stack) {
        return stack.stackSize <= 0 ? null : stack;
    }

    public static boolean willItemsStack(final ItemStack dest, final ItemStack src) {
        if(dest == null || src == null) {
            return true;
        }
        return isSameItem(dest, src);
    }
    
    public static boolean isSameItem(final ItemStack stack1, final ItemStack stack2) {
        return stack1.getItem() == stack2.getItem() && stack1.getMetadata() == stack2.getMetadata() && ItemStack.areItemStackTagsEqual(stack1, stack2);
    }

    public static void debugLog(final String message) {
        if (EqEConfig.misc.debug) {
            EquivalentEnergistics.logger.debug(message);
        }
    }

    public static void debugLog(final String message, final Throwable t) {
        if (EqEConfig.misc.debug) {
            EquivalentEnergistics.logger.debug(message, t);
        }
    }

}
