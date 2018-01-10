package com.mordenkainen.equivalentenergistics.blocks.crafter;

import java.util.ArrayList;
import java.util.List;

import com.mordenkainen.equivalentenergistics.blocks.base.block.BlockMultiAE;
import com.mordenkainen.equivalentenergistics.blocks.base.tile.TE;
import com.mordenkainen.equivalentenergistics.blocks.base.tile.TEList;
import com.mordenkainen.equivalentenergistics.blocks.crafter.tiles.TileEMCCrafter;
import com.mordenkainen.equivalentenergistics.blocks.crafter.tiles.TileEMCCrafterAdv;
import com.mordenkainen.equivalentenergistics.blocks.crafter.tiles.TileEMCCrafterExt;
import com.mordenkainen.equivalentenergistics.blocks.crafter.tiles.TileEMCCrafterUlt;
import com.mordenkainen.equivalentenergistics.core.Names;
import com.mordenkainen.equivalentenergistics.core.Reference;
import com.mordenkainen.equivalentenergistics.integration.ae2.NetworkLights;
import com.mordenkainen.equivalentenergistics.integration.ae2.grid.IAEProxyHost;
import com.mordenkainen.equivalentenergistics.items.ModItems;
import com.mordenkainen.equivalentenergistics.util.CommonUtils;
import com.mordenkainen.equivalentenergistics.util.IDropItems;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

@TEList({
    @TE(tileEntityClass = TileEMCCrafter.class, registryName = Reference.MOD_ID + ".emc_crafter"),
    @TE(tileEntityClass = TileEMCCrafterAdv.class, registryName = Reference.MOD_ID + ".emc_crafter_adv"),
    @TE(tileEntityClass = TileEMCCrafterExt.class, registryName = Reference.MOD_ID + ".emc_crafter_ext"),
    @TE(tileEntityClass = TileEMCCrafterUlt.class, registryName = Reference.MOD_ID + ".emc_crafter_ult")
})
public class BlockEMCCrafter extends BlockMultiAE {

    public BlockEMCCrafter() {
        super(Material.ROCK, Names.CRAFTER, 4);
        setHardness(1.5f);
        blockSoundType = SoundType.STONE;
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

    @Deprecated
    @Override
    public IBlockState getActualState(final IBlockState state, final IBlockAccess world, final BlockPos pos) {
        final IBlockState tmpState = super.getActualState(state, world, pos);
        final TileEMCCrafter tile = CommonUtils.getTE(world, pos);
        if (tile != null && tile.isErrored()) {
            return tmpState.withProperty(LIGHTS, NetworkLights.ERROR);
        }
        return tmpState;
    }

    @Override
    public void onBlockPlacedBy(final World world, final BlockPos pos, final IBlockState state, final EntityLivingBase placer, final ItemStack stack) {
        final IAEProxyHost tile = CommonUtils.getTE(world, pos);

        if (tile != null && placer instanceof EntityPlayer) {
            tile.setOwner((EntityPlayer) placer);
        }
    }

    @Override
    public void breakBlock(final World world, final BlockPos pos, final IBlockState state) {
        if (!world.isRemote) {
            final IDropItems tile = CommonUtils.getTE(world, pos);

            if (tile != null) {
                final List<ItemStack> drops = new ArrayList<ItemStack>();
                tile.getDrops(world, pos, drops);

                for (final ItemStack drop : drops) {
                    CommonUtils.spawnEntItem(world, pos, drop);
                }
            }
        }

        super.breakBlock(world, pos, state);
    }

    @Override
    public boolean onBlockActivated(final World world, final BlockPos pos, final IBlockState state, final EntityPlayer player, final EnumHand hand, final EnumFacing facing, final float hitX, final float hitY, final float hitZ) {
        final TileEMCCrafter tileCrafter = CommonUtils.getTE(world, pos);

        if (tileCrafter == null || !tileCrafter.canPlayerInteract(player)) {
            return false;
        }

        final ItemStack existingTome = tileCrafter.getCurrentTome();
        if (isValidTome(player.getHeldItem(hand)) && existingTome.isEmpty()) {
            tileCrafter.setCurrentTome(player.getHeldItem(hand).copy());
            if (!player.capabilities.isCreativeMode) {
                player.setHeldItem(hand, ItemStack.EMPTY);
            }
            return true;
        } else if (!existingTome.isEmpty()) {
            tileCrafter.setCurrentTome(ItemStack.EMPTY);
            if (!world.isRemote) {
                CommonUtils.spawnEntItem(world, pos, existingTome);
            }
            return true;
        }

        return false;
    }

    private boolean isValidTome(final ItemStack itemStack) {
        return itemStack != null && itemStack.getItem() == ModItems.EMC_BOOK && itemStack.hasTagCompound() && itemStack.getTagCompound().hasKey("OwnerUUID");
    }

}
