package com.mordenkainen.equivalentenergistics.util;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;

import com.mordenkainen.equivalentenergistics.EquivalentEnergistics;
import com.mordenkainen.equivalentenergistics.core.config.ConfigManager;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public final class CommonUtils {

    private CommonUtils() {}

    public static boolean destroyAndDrop(final World world, final int x, final int y, final int z) {
        final Block block = world.getBlock(x, y, z);
        if (block != null && block.getBlockHardness(world, x, y, z) >= 0) {
            if (!world.isRemote) {
                block.breakBlock(world, x, y, z, block, world.getBlockMetadata(x, y, z));
                final ArrayList<ItemStack> drops = block.getDrops(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
                for (final ItemStack stack : drops) {
                    spawnEntItem(world, x, y, z, stack);
                }
            }
            world.setBlockToAir(x, y, z);

            return true;
        }
        return false;
    }

    public static void spawnEntItem(final World world, final double x, final double y, final double z, final ItemStack item) {
        if (world.getGameRules().getGameRuleBooleanValue("doTileDrops") && !world.restoringBlockSnapshots && item != null && item.stackSize > 0) {
            final float rx = world.rand.nextFloat() * 0.8F + 0.1F;
            final float ry = world.rand.nextFloat() * 0.8F + 0.1F;
            final float rz = world.rand.nextFloat() * 0.8F + 0.1F;

            final EntityItem entityItem = new EntityItem(world, x + rx, y + ry, z + rz, new ItemStack(item.getItem(), item.stackSize, item.getItemDamage()));

            if (item.hasTagCompound()) {
                entityItem.getEntityItem().setTagCompound((NBTTagCompound) item.getTagCompound().copy());
            }

            entityItem.motionX = world.rand.nextGaussian() * 0.05F;
            entityItem.motionY = world.rand.nextGaussian() * 0.05F + 0.2000000029802322D;
            entityItem.motionZ = world.rand.nextGaussian() * 0.05F;
            world.spawnEntityInWorld(entityItem);
        }
    }

    @SuppressWarnings("unchecked")
    public static <R extends TileEntity> R getTE(final IBlockAccess world, final int x, final int y, final int z) {
        final TileEntity tile = world.getTileEntity(x, y, z);
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
        if(dest == null && src != null) {
            return true;
        }
        return isSameItem(dest, src);
    }

    public static void spawnParticle(final World world, final int x, final int y, final int z, final ForgeDirection dir, final String particle, final Random random) {
        double d1 = x + random.nextFloat();
        double d2 = y + random.nextFloat();
        double d3 = z + random.nextFloat();

        switch (dir) {
            case EAST:
                d1 = x + 1.0625D;
                break;
            case WEST:
                d1 = x - 0.0625D;
                break;
            case UP:
                d2 = y + 1.0625D;
                break;
            case DOWN:
                d2 = y - 0.0625D;
                break;
            case SOUTH:
                d3 = z + 1.0625D;
                break;
            case NORTH:
                d3 = z - 0.0625D;
                break;
            default:
                break;
        }

        world.spawnParticle(particle, d1, d2, d3, 0.0D, 0.0D, 0.0D);
    }

    public static boolean isSameItem(final ItemStack stack1, final ItemStack stack2) {
        return stack1.getItem() == stack2.getItem() && stack1.getItemDamage() == stack2.getItemDamage() && ItemStack.areItemStackTagsEqual(stack1, stack2);
    }

    public static void debugLog(final String message) {
        if (ConfigManager.debug) {
            EquivalentEnergistics.logger.debug(message);
        }
    }

    public static void debugLog(final String message, final Throwable t) {
        if (ConfigManager.debug) {
            EquivalentEnergistics.logger.debug(message, t);
        }
    }

}
