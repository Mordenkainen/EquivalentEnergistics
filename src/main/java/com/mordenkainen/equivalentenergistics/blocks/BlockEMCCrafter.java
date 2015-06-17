package com.mordenkainen.equivalentenergistics.blocks;

import java.util.ArrayList;

import com.mordenkainen.equivalentenergistics.EquivalentEnergistics;
import com.mordenkainen.equivalentenergistics.lib.Ref;
import com.mordenkainen.equivalentenergistics.tiles.TileEMCCondenser;
import com.mordenkainen.equivalentenergistics.tiles.TileEMCCrafter;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockEMCCrafter extends BlockContainer {

	public BlockEMCCrafter() {
		super(Material.rock);
		setHardness(1.5f);
		setStepSound(Block.soundTypeStone);
		setCreativeTab(EquivalentEnergistics.tabEE);
		setBlockName(Ref.getId("EMCCrafter"));
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEMCCrafter();
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public final void registerBlockIcons(final IIconRegister register) {
		blockIcon = Blocks.stone.getIcon(0, 0);
	}
	
	@Override
	public void breakBlock(final World world, final int x, final int y, final int z, final Block block, final int metaData) {
		if(!world.isRemote) {
			TileEntity tileCrafter = world.getTileEntity(x, y, z);

			if(tileCrafter instanceof TileEMCCrafter) {
				ItemStack existingTome = ((TileEMCCrafter)tileCrafter).getCurrentTome();
				if(existingTome != null) {
					world.spawnEntityInWorld(new EntityItem(world, x, y, z, existingTome));
				}
				((TileEMCCrafter)tileCrafter).onBreak();
			}
		}

		super.breakBlock(world, x, y, z, block, metaData);
	}

	@Override
	public void onBlockPlacedBy(final World world, final int x, final int y, final int z, final EntityLivingBase player, final ItemStack itemStack) {
		TileEntity tileCrafter = world.getTileEntity( x, y, z );

		if(tileCrafter instanceof TileEMCCrafter) {
			if(player instanceof EntityPlayer) {
				((TileEMCCrafter)tileCrafter).setOwner((EntityPlayer)player);
			}
		}
	}
	
	@Override
	public final boolean onBlockActivated(final World world, final int x, final int y, final int z, final EntityPlayer player, final int side,
											final float hitX, final float hitY, final float hitZ) {
		TileEntity tileCrafter = world.getTileEntity(x, y, z);

		if(tileCrafter instanceof TileEMCCrafter) {
			if(player.getHeldItem() == null) {
				ItemStack existingTome = ((TileEMCCrafter)tileCrafter).getCurrentTome();
				if(existingTome != null) {
					if(!world.isRemote) {
						world.spawnEntityInWorld(new EntityItem(world, player.posX, player.posY, player.posZ, existingTome));
					}
					((TileEMCCrafter)tileCrafter).setCurrentTome(null);
					return true;
				}
			} else if (player.getHeldItem().getItem() == GameRegistry.findItem("EE3", "alchemicalTome") && ((TileEMCCrafter)tileCrafter).getCurrentTome() == null) {
				((TileEMCCrafter)tileCrafter).setCurrentTome(player.getHeldItem().copy());
				player.inventory.mainInventory[player.inventory.currentItem] = --player.inventory.mainInventory[player.inventory.currentItem].stackSize==0 ? null:
					player.inventory.mainInventory[player.inventory.currentItem];
				return true;
			}
		}
		return false;
	}
}
