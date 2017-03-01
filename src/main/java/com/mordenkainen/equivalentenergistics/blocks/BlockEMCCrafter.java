package com.mordenkainen.equivalentenergistics.blocks;

import com.mordenkainen.equivalentenergistics.EquivalentEnergistics;
import com.mordenkainen.equivalentenergistics.config.IConfigurable;
import com.mordenkainen.equivalentenergistics.integration.Integration;
import com.mordenkainen.equivalentenergistics.registries.TextureEnum;
import com.mordenkainen.equivalentenergistics.tiles.crafter.TileEMCCrafter;
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

public class BlockEMCCrafter extends BlockContainerBase implements IConfigurable {

    private static final String GROUP = "Crafter";

    public static double idlePower;
    public static double activePower;
    public static double craftingTime;

    public BlockEMCCrafter() {
        super(Material.rock);
        setHardness(1.5f);
        setStepSound(Block.soundTypeStone);
        setLightOpacity(1);
    }

    @Override
    public TileEntity createNewTileEntity(final World world, final int meta) {
        return new TileEMCCrafter();
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
        final TileEMCCrafter tileCrafter = CommonUtils.getTE(TileEMCCrafter.class, world, x, y, z);

        if (tileCrafter == null || !tileCrafter.canPlayerInteract(player)) {
            return false;
        }

        final ItemStack existingTome = tileCrafter.getCurrentTome();
        if (Integration.emcHandler.isValidTome(player.getHeldItem()) && existingTome == null) {
            tileCrafter.setCurrentTome(player.getHeldItem().copy());
            if (!player.capabilities.isCreativeMode) {
                player.inventory.mainInventory[player.inventory.currentItem] = --player.inventory.mainInventory[player.inventory.currentItem].stackSize == 0 ? null
                        : player.inventory.mainInventory[player.inventory.currentItem];
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
        activePower = config.get(GROUP, "PowerDrainPerCraftingTick", 1.5).getDouble(1.5);
        craftingTime = config.get(GROUP, "TicksPerCrafting", 7).getInt(7);
    }

}
