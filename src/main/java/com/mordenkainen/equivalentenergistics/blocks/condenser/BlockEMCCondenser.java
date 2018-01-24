package com.mordenkainen.equivalentenergistics.blocks.condenser;

import java.util.Locale;
import java.util.Random;

import com.mordenkainen.equivalentenergistics.EquivalentEnergistics;
import com.mordenkainen.equivalentenergistics.blocks.base.block.BlockMultiContainerBase;
import com.mordenkainen.equivalentenergistics.blocks.base.block.ILayeredBlock;
import com.mordenkainen.equivalentenergistics.blocks.condenser.tiles.TileEMCCondenser;
import com.mordenkainen.equivalentenergistics.blocks.condenser.tiles.TileEMCCondenserAdv;
import com.mordenkainen.equivalentenergistics.blocks.condenser.tiles.TileEMCCondenserBase;
import com.mordenkainen.equivalentenergistics.blocks.condenser.tiles.TileEMCCondenserExt;
import com.mordenkainen.equivalentenergistics.blocks.condenser.tiles.TileEMCCondenserExt.SideSetting;
import com.mordenkainen.equivalentenergistics.blocks.condenser.tiles.TileEMCCondenserUlt;
import com.mordenkainen.equivalentenergistics.core.config.IConfigurable;
import com.mordenkainen.equivalentenergistics.core.textures.TextureEnum;
import com.mordenkainen.equivalentenergistics.util.CommonUtils;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockEMCCondenser extends BlockMultiContainerBase implements IConfigurable, ILayeredBlock {

    private static final String GROUP = "Condenser";
    public static double emcPerTick;
    public static double idlePower;
    public static double activePower;
    
    public BlockEMCCondenser() {
        super(Material.rock, 4);
        setHardness(1.5f);
        setStepSound(Block.soundTypeStone);
    }

    @Override
    public int getRenderBlockPass() {
        return 1;
    }

    @Override
    public boolean canRenderInPass(final int pass) {
        return pass == 1;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public int getRenderType() {
        return EquivalentEnergistics.proxy.layeredRenderer;
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
            default:
                return new TileEMCCondenserUlt();
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(final int side, final int meta) {
        return TextureEnum.EMCCONDENSER.getTexture(meta);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(final World world, final int x, final int y, final int z, final Random random) {
        final TileEMCCondenserBase tileCondenser = CommonUtils.getTE(world, x, y, z);

        if (tileCondenser == null) {
            return;
        }

        if (tileCondenser.getState().isError()) {
            for (final ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
                if (world.getBlock(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ).isOpaqueCube()) {
                    continue;
                }

                CommonUtils.spawnParticle(world, x, y, z, dir, "reddust", random);
            }
        }
    }

    @Override
    public boolean onBlockActivated(final World world, final int x, final int y, final int z, final EntityPlayer player, final int side, final float hitX, final float hitY, final float hitZ) {
        if (player == null) {
            return false;
        }

        if (player.getHeldItem() == null) {
            final TileEMCCondenserExt tileCondenser = CommonUtils.getTE(world, x, y, z);
            if (tileCondenser != null && !world.isRemote) {
                tileCondenser.toggleSide(side);
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean hasComparatorInputOverride() {
        return true;
    }
    
    @Override
    public int getComparatorInputOverride(final World world, final int x, final int y, final int z, final int meta) {
        if(meta == 0) {
            return 0;
        }
        
        final TileEMCCondenserAdv tile = CommonUtils.getTE(world, x, y, z);
        
        switch (tile.getState()) {
        case ACTIVE:
            return 2;
        case IDLE:
            return 1;
        case NOEMCSTORAGE:
            return 3;
        case NOITEMSTORAGE:
            return 4;
        case NOPOWER:
            return 5;
        default:
            return 0;
        }
    }
    
    @Override
    public boolean canConnectRedstone(final IBlockAccess world, final int x, final int y, final int z, final int side) {
        return world.getBlockMetadata(x, y, z) != 0;
    }

    @Override
    public int numLayers(final Block block, final int meta) {
        return 0;
    }

    @Override
    public int numLayers(final IBlockAccess world, final Block block, final int x, final int y, final int z, final int meta) {
        return 2;
    }

    @Override
    public IIcon getLayer(final Block block, final int side, final int meta, final int layer) {
        return null;
    }

    @Override
    public IIcon getLayer(final IBlockAccess world, final Block block, final int x, final int y, final int z, final int side, final int meta, final int layer) {
        final TileEMCCondenserBase tileCondenser = CommonUtils.getTE(world, x, y, z);
        if (tileCondenser != null) {
            if (layer == 1 && tileCondenser.isActive()) {
                return TextureEnum.EMCCONDENSEROVL.getTexture(2);
            } else if (tileCondenser instanceof TileEMCCondenserExt) {
                if (((TileEMCCondenserExt) tileCondenser).getSide(side) == SideSetting.INPUT) {
                    return TextureEnum.EMCCONDENSEROVL.getTexture();
                } else if (((TileEMCCondenserExt) tileCondenser).getSide(side) == SideSetting.OUTPUT) {
                    return TextureEnum.EMCCONDENSEROVL.getTexture(1);
                }
            }
        }
        return null;
    }
    
    @Override
    public void loadConfig(final Configuration config) {
        idlePower = config.get(GROUP, "IdlePowerDrain", 0.0).getDouble(0.0);
        activePower = config.get(GROUP, "PowerDrainPerEMCCondensed", 0.01).getDouble(0.01);
        emcPerTick = config.get(GROUP, "EMCProducedPerTick", 8192).getDouble(8192);

        final ConfigCategory condenserCat = config.getCategory(GROUP.toLowerCase(Locale.US));
        condenserCat.remove("CrystalsProducedPerTick");
        condenserCat.remove("ItemsCondensedPerTick");
    }

}
