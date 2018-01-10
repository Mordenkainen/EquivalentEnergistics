package com.mordenkainen.equivalentenergistics.items;

import com.mordenkainen.equivalentenergistics.core.Names;
import com.mordenkainen.equivalentenergistics.integration.ae2.cells.HandlerEMCCellCreative;

import appeng.api.AEApi;
import appeng.api.storage.IMEInventory;
import appeng.api.storage.IMEInventoryHandler;
import appeng.api.storage.ISaveProvider;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEStack;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;

public class ItemCellCreative extends ItemCellBase {

    public ItemCellCreative() {
        super(Names.CELL_CREATIVE, 1);
    }

    @Override
    public EnumRarity getRarity(final ItemStack stack) {
        return EnumRarity.EPIC;
    }

    @Override
    public <T extends IAEStack<T>> double cellIdleDrain(ItemStack stack, IMEInventory<T> inv) {
        return 0;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends IAEStack<T>> IMEInventoryHandler<T> getCellInventory(ItemStack stack, ISaveProvider host, IStorageChannel<T> channel) {
        if (channel == AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class) && isCell(stack)) {
            return (IMEInventoryHandler<T>) new HandlerEMCCellCreative(host);
        }
        return null;
    }

    @Override
    public <T extends IAEStack<T>> int getStatusForCell(ItemStack stack, IMEInventory<T> inv) {
        return 1;
    }

}
