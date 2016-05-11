package com.mordenkainen.equivalentenergistics.blocks;

import java.util.ArrayList;

import com.mordenkainen.equivalentenergistics.lib.Ref;
import com.mordenkainen.equivalentenergistics.tiles.TileEMCCondenser;
import com.mordenkainen.equivalentenergistics.util.CommonUtils;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;

public class BlockEMCCondenser extends BlockContainer {
	public static int itemsPerTick;
	public static int crystalsPerTick;
	public static double condenserIdlePower;
	public static double condenserActivePower;
	
	public static void loadfConfig(Configuration config) {
		itemsPerTick = config.get("Condenser", "ItemsCondensedPerTick", 8).getInt(8);
        crystalsPerTick = config.get("Condenser", "CrystalsProducedPerTick", 16).getInt(16);
        condenserIdlePower = config.get("Condenser", "IdlePowerDrain", 0.0).getDouble(0.0);
        condenserActivePower = config.get("Condenser", "PowerDrainPerEMCCondensed", 0.01).getDouble(0.01);
	}
	
	@SideOnly(Side.CLIENT)
	IIcon[] icons;
	
	public BlockEMCCondenser() {
		super(Material.rock);
		setHardness(1.5f);
		setStepSound(Block.soundTypeStone);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEMCCondenser();
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public final void registerBlockIcons(final IIconRegister register) {
		icons = new IIcon[2];
		icons[0] = register.registerIcon(Ref.getId("EMCCondenserTop"));
		icons[1] = register.registerIcon(Ref.getId("EMCCondenserSide"));
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(int side, int meta) {
		if(side == 0 || side == 1) {
			return icons[0];
		}
		return icons[1];
	}

	@Override
	public void breakBlock(final World world, final int x, final int y, final int z, final Block block, final int metaData) {
		if(!world.isRemote) {
			TileEMCCondenser tileCondenser = CommonUtils.getTE(TileEMCCondenser.class, world, x, y, z);

			if(tileCondenser != null) {
				ArrayList<ItemStack> drops = new ArrayList<ItemStack>();
				tileCondenser.getDrops(world, x, y, z, drops);

				for(ItemStack drop : drops)	{
					world.spawnEntityInWorld(new EntityItem(world, 0.5 + x, 0.5 + y, 0.2 + z, drop));
				}
			}
		}

		super.breakBlock(world, x, y, z, block, metaData);
	}
	
	@Override
	public void onBlockPlacedBy(final World world, final int x, final int y, final int z, final EntityLivingBase player, final ItemStack itemStack) {
		TileEMCCondenser tileCondenser = CommonUtils.getTE(TileEMCCondenser.class, world, x, y, z);

		if(tileCondenser != null && player instanceof EntityPlayer) {
			tileCondenser.setOwner((EntityPlayer)player);
		}
	}
}
