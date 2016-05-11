package com.mordenkainen.equivalentenergistics.blocks;

import com.mordenkainen.equivalentenergistics.EquivalentEnergistics;
import com.mordenkainen.equivalentenergistics.tiles.TileEMCCrafter;
import com.mordenkainen.equivalentenergistics.util.CommonUtils;
import com.mordenkainen.equivalentenergistics.util.EMCUtils;

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
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;

public class BlockEMCCrafter extends BlockContainer {
	public static double crafterIdlePower;
	public static double crafterActivePower;
	public static double craftingTime;
	
	public static void loadfConfig(Configuration config) {
		crafterIdlePower = config.get("Crafter", "IdlePowerDrain", 0.0).getDouble(0.0);
        crafterActivePower = config.get("Crafter", "PowerDrainPerCraftingTick", 1.5).getDouble(1.5);
        craftingTime = config.get("Crafter", "TicksPerCrafting", 7).getInt(7);
	}
	
	public BlockEMCCrafter() {
		super(Material.rock);
		setHardness(1.5f);
		setStepSound(Block.soundTypeStone);
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}
	
	@Override
	public int getRenderType() {
		return EquivalentEnergistics.proxy.EMCCrafterRenderer;
	}
	
	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEMCCrafter();
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public final void registerBlockIcons(final IIconRegister register) {}
	
	@Override
	public void breakBlock(final World world, final int x, final int y, final int z, final Block block, final int metaData) {
		if(!world.isRemote) {
			TileEMCCrafter tileCrafter = CommonUtils.getTE(TileEMCCrafter.class, world, x, y, z);

			if(tileCrafter != null) {
				ItemStack existingTome = ((TileEMCCrafter)tileCrafter).getCurrentTome();
				if(existingTome != null) {
					world.spawnEntityInWorld(new EntityItem(world, x, y, z, existingTome));
				}
			}
		}

		super.breakBlock(world, x, y, z, block, metaData);
	}

	@Override
	public void onBlockPlacedBy(final World world, final int x, final int y, final int z, final EntityLivingBase player, final ItemStack itemStack) {
		TileEMCCrafter tileCrafter = CommonUtils.getTE(TileEMCCrafter.class, world, x, y, z);

		if(tileCrafter != null && player instanceof EntityPlayer) {
			tileCrafter.setOwner((EntityPlayer)player);
		}
	}
	
	@Override
	public final boolean onBlockActivated(final World world, final int x, final int y, final int z, final EntityPlayer player, final int side, final float hitX, final float hitY, final float hitZ) {
		TileEMCCrafter tileCrafter = CommonUtils.getTE(TileEMCCrafter.class, world, x, y, z);
		
		if(tileCrafter != null && tileCrafter.checkPermissions(player) && !tileCrafter.isCrafting()) {
			if(player.getHeldItem() == null) {
				ItemStack existingTome = tileCrafter.getCurrentTome();
				if(existingTome != null) {
					if(!world.isRemote) {
						world.spawnEntityInWorld(new EntityItem(world, player.posX, player.posY, player.posZ, existingTome));
					}
					tileCrafter.setCurrentTome(null);
					return true;
				}
			} else if (EMCUtils.getInstance().isValidTome(player.getHeldItem())) {
				if(tileCrafter.getCurrentTome() == null) {
					tileCrafter.setCurrentTome(player.getHeldItem().copy());
					player.inventory.mainInventory[player.inventory.currentItem] = --player.inventory.mainInventory[player.inventory.currentItem].stackSize==0 ? null:
						player.inventory.mainInventory[player.inventory.currentItem];
					return true;
				}
			}
		}
		return false;
	}
}
