package com.mordenkainen.equivalentenergistics.blocks.provider;

import com.mordenkainen.equivalentenergistics.EquivalentEnergistics;
import com.mordenkainen.equivalentenergistics.blocks.base.block.BlockContainerBase;
import com.mordenkainen.equivalentenergistics.blocks.provider.tile.TileEMCPatternProvider;
import com.mordenkainen.equivalentenergistics.core.textures.TextureEnum;
import com.mordenkainen.equivalentenergistics.integration.Integration;
import com.mordenkainen.equivalentenergistics.util.CommonUtils;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockEMCPatternProvider extends BlockContainerBase {

    public BlockEMCPatternProvider() {
        super(Material.rock);
        setHardness(1.5f);
        setStepSound(Block.soundTypeStone);
        setLightOpacity(1);
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
        return EquivalentEnergistics.proxy.providerRenderer;
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(final int side, final int meta) {
        return TextureEnum.EMCPROVIDER.getTexture();
    }

    @Override
    public TileEntity createNewTileEntity(final World world, final int meta) {
        return new TileEMCPatternProvider();
    }

    @Override
    public final boolean onBlockActivated(final World world, final int x, final int y, final int z, final EntityPlayer player, final int side, final float hitX, final float hitY, final float hitZ) {
        final TileEMCPatternProvider tileProvider = CommonUtils.getTE(TileEMCPatternProvider.class, world, x, y, z);

        if (tileProvider == null || !tileProvider.canPlayerInteract(player)) {
            return false;
        }

        if (Integration.emcHandler.isValidTome(player.getHeldItem()) && tileProvider.addTome(player.getHeldItem().copy())) {
            if (!player.capabilities.isCreativeMode) {
                player.inventory.mainInventory[player.inventory.currentItem] = null;
            }
            return true;
        } 

        return false;

    }
    
}
