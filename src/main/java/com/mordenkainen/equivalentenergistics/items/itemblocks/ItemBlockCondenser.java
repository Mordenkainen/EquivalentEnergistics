package com.mordenkainen.equivalentenergistics.items.itemblocks;

import com.mordenkainen.equivalentenergistics.blocks.BlockEnum;
import com.mordenkainen.equivalentenergistics.blocks.condenser.tiles.TileEMCCondenserBase;
import com.mordenkainen.equivalentenergistics.util.CommonUtils;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class ItemBlockCondenser extends ItemBlockMulti {

    private boolean canPlace = true;

    public ItemBlockCondenser(final Block block) {
        super(block);
    }

    @Override
    public boolean onItemUseFirst(final ItemStack stack, final EntityPlayer player, final World world, final int x, final int y, final int z, final int side, final float hitX, final float hitY, final float hitZ) {
        final int blockMeta = world.getBlockMetadata(x, y, z);
        if (player == null || blockMeta == stack.getItemDamage()) {
            return super.onItemUseFirst(stack, player, world, x, y, z, side, hitX, hitY, hitZ);
        }

        TileEMCCondenserBase tileCondenser = CommonUtils.getTE(TileEMCCondenserBase.class, world, x, y, z);
        final NBTTagCompound tag = new NBTTagCompound();
        if (tileCondenser != null) {
            tileCondenser.writeToNBT(tag);
            tag.removeTag("node0");
            world.setBlock(x, y, z, BlockEnum.EMCCONDENSER.getBlock(), player.getHeldItem().getItemDamage(), 3);
            tileCondenser = CommonUtils.getTE(TileEMCCondenserBase.class, world, x, y, z);
            if (tileCondenser != null) {
                tileCondenser.readFromNBT(tag);
            }
            if (!player.capabilities.isCreativeMode) {
                stack.stackSize--;
                player.inventory.addItemStackToInventory(new ItemStack(BlockEnum.EMCCONDENSER.getBlock(), 1, blockMeta));
            }
            canPlace = false;

            return !world.isRemote;
        }

        return super.onItemUseFirst(stack, player, world, x, y, z, side, hitX, hitY, hitZ);
    }

    @Override
    public boolean placeBlockAt(final ItemStack stack, final EntityPlayer player, final World world, final int x, final int y, final int z, final int side, final float hitX, final float hitY, final float hitZ, final int metadata) {
        if (world.isRemote && !canPlace) {
            canPlace = true;
            return false;
        }
        return super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, metadata);
    }

}
