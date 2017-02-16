package com.mordenkainen.equivalentenergistics.blocks;

import java.util.Locale;
import java.util.Random;

import com.mordenkainen.equivalentenergistics.config.IConfigurable;
import com.mordenkainen.equivalentenergistics.registries.TextureEnum;
import com.mordenkainen.equivalentenergistics.tiles.TileEMCCondenser;
import com.mordenkainen.equivalentenergistics.tiles.TileEMCCondenserBase;
import com.mordenkainen.equivalentenergistics.util.CommonUtils;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
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
        super(Material.rock);
        setHardness(1.5f);
        setStepSound(Block.soundTypeStone);
    }

    @Override
    public TileEntity createNewTileEntity(final World world, final int meta) {
        return new TileEMCCondenser();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(final int side, final int meta) {
        return TextureEnum.EMCCONDENSER.getTexture(side == 0 || side == 1 ? 0 : 1);
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
    public void loadConfig(final Configuration config) {
        itemsPerTick = config.get(GROUP, "ItemsCondensedPerTick", 8).getInt(8);
        idlePower = config.get(GROUP, "IdlePowerDrain", 0.0).getDouble(0.0);
        activePower = config.get(GROUP, "PowerDrainPerEMCCondensed", 0.01).getDouble(0.01);

        emcPerTick = (float) config.get(GROUP, "EMCProducedPerTick", 4096).getDouble(4096);
        if (config.hasKey(GROUP.toLowerCase(Locale.US), "CrystalsProducedPerTick")) {
            final ConfigCategory condenserCat = config.getCategory(GROUP.toLowerCase(Locale.US));
            condenserCat.remove("CrystalsProducedPerTick");
        }
    }

}
