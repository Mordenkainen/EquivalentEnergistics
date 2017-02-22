package com.mordenkainen.equivalentenergistics.blocks;

import java.util.Locale;
import java.util.Random;

import com.mordenkainen.equivalentenergistics.config.IConfigurable;
import com.mordenkainen.equivalentenergistics.registries.TextureEnum;
import com.mordenkainen.equivalentenergistics.tiles.condenser.TileEMCCondenser;
import com.mordenkainen.equivalentenergistics.tiles.condenser.TileEMCCondenserAdv;
import com.mordenkainen.equivalentenergistics.tiles.condenser.TileEMCCondenserAdv.RedstoneMode;
import com.mordenkainen.equivalentenergistics.tiles.condenser.TileEMCCondenserBase;
import com.mordenkainen.equivalentenergistics.tiles.condenser.TileEMCCondenserExt;
import com.mordenkainen.equivalentenergistics.tiles.condenser.TileEMCCondenserUlt;
import com.mordenkainen.equivalentenergistics.util.CommonUtils;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockEMCCondenser extends BlockMultiContainerBase implements IConfigurable {

    private static final String GROUP = "Condenser";
    public static int itemsPerTick;
    public static float emcPerTick;
    public static double idlePower;
    public static double activePower;

    public BlockEMCCondenser() {
        super(Material.rock, 4);
        setHardness(1.5f);
        setStepSound(Block.soundTypeStone);
    }

    @Override
    public TileEntity createNewTileEntity(final World world, final int meta) {
    	switch (meta) {
    		case 0:
    			return new TileEMCCondenser();
    		case 1:
    			return new TileEMCCondenserAdv();
    		case 2:
    			return new TileEMCCondenserExt();
    		case 3:
    			return new TileEMCCondenserUlt();
    		default:
    			return new TileEMCCondenser();
    	}
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(final IBlockAccess world, final int x, final int y, final int z, final int side) {
        final int meta = world.getBlockMetadata(x, y, z);
        if (meta > 1) {
	    	final TileEMCCondenserExt tileCondenser = CommonUtils.getTE(TileEMCCondenserExt.class, world, x, y, z);
	    	if (tileCondenser != null) {
	    		final int sideData = tileCondenser.getSide(side);
	    		if (sideData == 0) {
	    			return TextureEnum.EMCCONDENSERADV.getTexture(side == 0 || side == 1 ? (meta - 2) * 4 : (meta - 2) * 4 + 1);
	    		} else {
	    			return TextureEnum.EMCCONDENSERADV.getTexture((meta - 2) * 4 + sideData);
	    		}
	    	}
    	}
        return TextureEnum.EMCCONDENSER.getTexture(side == 0 || side == 1 ? meta * 2 : meta * 2 + 1);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(final int side, final int meta) {
    	if (meta < 2) {
    		return TextureEnum.EMCCONDENSER.getTexture(side == 0 || side == 1 ? meta * 2 : meta * 2 + 1);
    	}
    	return TextureEnum.EMCCONDENSERADV.getTexture(side == 0 || side == 1 ? (meta - 2) * 4 : (meta - 2) * 4 + 1);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(final World world, final int x, final int y, final int z, final Random random) {
        final TileEMCCondenserBase tileCondenser = CommonUtils.getTE(TileEMCCondenserBase.class, world, x, y, z);

        if (tileCondenser == null) {
            return;
        }
        
        String particle = null;
        switch (tileCondenser.getState()) {
	        case BLOCKED:
	        	particle = "reddust";
				break;
			case MISSING_CHANNEL:
				particle = "largesmoke";
				break;
			case UNPOWERED:
				particle = "angryVillager";
				break;
			default:
				break;
        }

        if (particle != null) {
	        for (final ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
	            if (world.getBlock(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ).isOpaqueCube()) {
	                continue;
	            }
	
	            CommonUtils.spawnParticle(world, x, y, z, dir, particle, random);
	        }
        }
    }
    
    @Override
	public boolean onBlockActivated(final World world, final int x, final int y, final int z, final EntityPlayer player, final int side, final float hitX, final float hitY, final float hitZ) {
		if (player == null) {
			return false;
		}
    	
    	if (player.getHeldItem() == null) {
    		final TileEMCCondenserExt tileCondenser = CommonUtils.getTE(TileEMCCondenserExt.class, world, x, y, z);
    		if (tileCondenser != null && !world.isRemote) {
    			tileCondenser.toggleSide(side);
    		}
    		return true;
    	} else if (player.getHeldItem().getItem() == Items.redstone) {
			final TileEMCCondenserAdv tileCondenser = CommonUtils.getTE(TileEMCCondenserAdv.class, world, x, y, z);
    		if (tileCondenser != null && !world.isRemote) {
        		tileCondenser.nextMode();
        		player.addChatComponentMessage(new ChatComponentText(tileCondenser.getMode().description()));
        	}
    		return true;
    	}
		
    	return false;
    }
    
    @Override
	public boolean canConnectRedstone(final IBlockAccess world, final int x, final int y, final int z, final int side) {
    	final TileEMCCondenserAdv tileCondenser = CommonUtils.getTE(TileEMCCondenserAdv.class, world, x, y, z);
    	return tileCondenser != null && tileCondenser.getMode() != RedstoneMode.NONE;
    }
    
    @Override
	public int isProvidingStrongPower(final IBlockAccess world, final int x, final int y, final int z, final int side) {
    	final TileEMCCondenserAdv tileCondenser = CommonUtils.getTE(TileEMCCondenserAdv.class, world, x, y, z);
    	return tileCondenser != null && tileCondenser.isProducingPower() ? 15 : 0;
    }

    @Override
	public int isProvidingWeakPower(final IBlockAccess world, final int x, final int y, final int z, final int side) {
    	return isProvidingStrongPower(world, x, y, z, side);
    }
    
    @Override
    public boolean shouldCheckWeakPower(final IBlockAccess world, final int x, final int y, final int z, final int side) {
        return false;
    }
    
    @Override
    public void loadConfig(final Configuration config) {
        idlePower = config.get(GROUP, "IdlePowerDrain", 0.0).getDouble(0.0);
        activePower = config.get(GROUP, "PowerDrainPerEMCCondensed", 0.01).getDouble(0.01);
        emcPerTick = (float) config.get(GROUP, "EMCProducedPerTick", 4096).getDouble(4096);
        
        final ConfigCategory condenserCat = config.getCategory(GROUP.toLowerCase(Locale.US));
        condenserCat.remove("CrystalsProducedPerTick");
        condenserCat.remove("ItemsCondensedPerTick");
    }

}
