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

	public ItemCellBase(String name, int count) {
		super(name, count);
		setMaxStackSize(1);
	}

	@Override
	public boolean isCell(ItemStack stack) {
		return stack != null && stack.getItem() == this;
	}

	@Override
	public <T extends IAEStack<T>> void openChestGui(EntityPlayer player, IChestOrDrive drive, ICellHandler handler, IMEInventoryHandler<T> inv, ItemStack stack, IStorageChannel<T> channel) {
		player.sendStatusMessage(new TextComponentTranslation("message.cell.chestwarning", new Object[0]), true);
	}

}
