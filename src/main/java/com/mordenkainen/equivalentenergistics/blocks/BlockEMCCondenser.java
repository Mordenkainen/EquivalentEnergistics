package com.mordenkainen.equivalentenergistics.blocks;

import java.util.ArrayList;

import com.mordenkainen.equivalentenergistics.EquivalentEnergistics;
import com.mordenkainen.equivalentenergistics.lib.Ref;
import com.mordenkainen.equivalentenergistics.tiles.TileEMCCondenser;

import cpw.mods.fml.common.FMLCommonHandler;
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

public class BlockEMCCondenser extends BlockContainer {

	public BlockEMCCondenser() {
		super(Material.rock);
		setHardness(1.5f);
		setStepSound(Block.soundTypeStone);
		setCreativeTab(EquivalentEnergistics.tabEE);
		setBlockName(Ref.getId("EMCCondenser"));
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEMCCondenser();
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public final void registerBlockIcons(final IIconRegister register) {
		blockIcon = Blocks.stone.getIcon(0, 0);
	}

	@Override
	public void breakBlock(final World world, final int x, final int y, final int z, final Block block, final int metaData) {
		if(!world.isRemote) {
			TileEntity tileCondenser = world.getTileEntity(x, y, z);

			if(tileCondenser instanceof TileEMCCondenser) {
				ArrayList<ItemStack> drops = new ArrayList<ItemStack>();
				((TileEMCCondenser)tileCondenser).getDrops(world, x, y, z, drops);

				for(ItemStack drop : drops)	{
					world.spawnEntityInWorld(new EntityItem(world, 0.5 + x, 0.5 + y, 0.2 + z, drop));
				}

				((TileEMCCondenser)tileCondenser).onBreak();
			}
		}

		super.breakBlock(world, x, y, z, block, metaData);
	}
	
	@Override
	public void onBlockPlacedBy(final World world, final int x, final int y, final int z, final EntityLivingBase player, final ItemStack itemStack) {
		TileEntity tileCondenser = world.getTileEntity( x, y, z );

		if(tileCondenser instanceof TileEMCCondenser) {
			if(player instanceof EntityPlayer) {
				((TileEMCCondenser)tileCondenser).setOwner((EntityPlayer)player);
			}
		}
	}
}
