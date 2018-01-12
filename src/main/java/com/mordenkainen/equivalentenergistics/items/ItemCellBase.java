package com.mordenkainen.equivalentenergistics.items;

import com.mordenkainen.equivalentenergistics.items.base.ItemMultiBase;

import appeng.api.implementations.tiles.IChestOrDrive;
import appeng.api.storage.ICellHandler;
import appeng.api.storage.IMEInventoryHandler;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.data.IAEStack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentTranslation;

public abstract class ItemCellBase extends ItemMultiBase implements ICellHandler {

    public ItemCellBase(final String name, final int count) {
        super(name, count);
        setMaxStackSize(1);
    }

    @Override
    public boolean isCell(final ItemStack stack) {
        return stack != null && stack.getItem() == this;
    }

    @Override
    public <T extends IAEStack<T>> void openChestGui(final EntityPlayer player, final IChestOrDrive drive, final ICellHandler handler, final IMEInventoryHandler<T> inv, final ItemStack stack, final IStorageChannel<T> channel) {
        player.sendStatusMessage(new TextComponentTranslation("message.cell.chestwarning", new Object[0]), true);
    }

}
