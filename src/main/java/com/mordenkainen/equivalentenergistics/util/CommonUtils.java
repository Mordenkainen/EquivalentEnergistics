package com.mordenkainen.equivalentenergistics.util;

import java.text.DecimalFormat;
import java.util.ArrayList;

import com.mordenkainen.equivalentenergistics.EquivalentEnergistics;
import com.mordenkainen.equivalentenergistics.config.ConfigManager;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public final class CommonUtils {
	
	private CommonUtils() {}

	public static String formatEMC(final float emc) {
		float displayValue = emc;

		final String[] preFixes = {"k", "M", "B", "T", "P",	"T", "P", "E", "Z", "Y"};
		String level = "";
		int offset = 0;
		while (displayValue > 1000 && offset < preFixes.length) {
			displayValue /= 1000;
			level = preFixes[offset++];
		}

		final DecimalFormat formatter = new DecimalFormat("#.####");
		return formatter.format(displayValue) + ' ' + level;
	}
	
	public static boolean destroyAndDrop(final World world, final int x, final int y, final int z) {
		final Block block = world.getBlock(x, y, z);
		if (block != null && block.getBlockHardness(world, x, y, z) >= 0) {
			if (!world.isRemote){
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
				entityItem.getEntityItem().setTagCompound((NBTTagCompound)item.getTagCompound().copy());
			}

			entityItem.motionX = world.rand.nextGaussian() * 0.05F;
			entityItem.motionY = world.rand.nextGaussian() * 0.05F + 0.2000000029802322D;
			entityItem.motionZ = world.rand.nextGaussian() * 0.05F;
			world.spawnEntityInWorld(entityItem);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getTE(final Class<T> type, final IBlockAccess world, final int x, final int y, final int z) {
		final TileEntity tile = world.getTileEntity(x, y, z);
		return type.isInstance(tile) ? (T)tile : null;
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
