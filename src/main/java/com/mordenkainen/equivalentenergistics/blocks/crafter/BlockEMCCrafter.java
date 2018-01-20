package com.mordenkainen.equivalentenergistics.blocks.crafter;

import com.mordenkainen.equivalentenergistics.EquivalentEnergistics;
import com.mordenkainen.equivalentenergistics.blocks.base.block.BlockMultiContainerBase;
import com.mordenkainen.equivalentenergistics.blocks.crafter.tiles.TileEMCCrafter;
import com.mordenkainen.equivalentenergistics.blocks.crafter.tiles.TileEMCCrafterAdv;
import com.mordenkainen.equivalentenergistics.blocks.crafter.tiles.TileEMCCrafterBase;
import com.mordenkainen.equivalentenergistics.blocks.crafter.tiles.TileEMCCrafterExt;
import com.mordenkainen.equivalentenergistics.blocks.crafter.tiles.TileEMCCrafterUlt;
import com.mordenkainen.equivalentenergistics.core.config.IConfigurable;
import com.mordenkainen.equivalentenergistics.core.textures.TextureEnum;
import com.mordenkainen.equivalentenergistics.integration.Integration;
import com.mordenkainen.equivalentenergistics.util.CommonUtils;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import net.minecraftforge.common.config.Configuration;

public class BlockEMCCrafter extends BlockMultiContainerBase implements IConfigurable {

    private static final String GROUP = "Crafter";

    public static double idlePower;
    public static double powerPerEMC;
    public static double craftingTime;

    public BlockEMCCrafter() {
        super(Material.rock, 4);
        setHardness(1.5f);
        setStepSound(Block.soundTypeStone);
        setLightOpacity(1);
    }

    @Override
    public TileEntity createNewTileEntity(final World world, final int meta) {
        switch (meta) {
            case 0:
                return new TileEMCCrafter();
            case 1:
                return new TileEMCCrafterAdv();
            case 2:
                return new TileEMCCrafterExt();
            default:
                return new TileEMCCrafterUlt();
        }
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
        return EquivalentEnergistics.proxy.crafterRenderer;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(final int side, final int meta) {
        return TextureEnum.EMCCONDENSER.getTexture();
    }

    @Override
    public final boolean onBlockActivated(final World world, final int x, final int y, final int z, final EntityPlayer player, final int side, final float hitX, final float hitY, final float hitZ) {
        final TileEMCCrafterBase tileCrafter = CommonUtils.getTE(world, x, y, z);

        if (tileCrafter == null || !tileCrafter.canPlayerInteract(player)) {
            return false;
        }

        final ItemStack existingTome = tileCrafter.getCurrentTome();
        if (Integration.emcHandler.isValidTome(player.getHeldItem()) && existingTome == null) {
            tileCrafter.setCurrentTome(player.getHeldItem().copy());
            if (!player.capabilities.isCreativeMode) {
                player.inventory.mainInventory[player.inventory.currentItem] = null;
            }
            return true;
        } else if (existingTome != null) {
            tileCrafter.setCurrentTome(null);
            if (!world.isRemote) {
                CommonUtils.spawnEntItem(world, x, y, z, existingTome);
            }
            return true;
        }

        return false;

    }

    @Override
    public void loadConfig(final Configuration config) {
        idlePower = config.get(GROUP, "IdlePowerDrain", 0.0).getDouble(0.0);
        powerPerEMC = config.get(GROUP, "PowerDrainPerEMC", 0.01).getDouble(0.01);
        craftingTime = config.get(GROUP, "TicksPerCrafting", 20).getInt(20);
    }

}
