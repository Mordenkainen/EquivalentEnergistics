package com.mordenkainen.equivalentenergistics.items;

import com.mordenkainen.equivalentenergistics.core.Names;
import com.mordenkainen.equivalentenergistics.integration.ae2.cells.HandlerEMCCellCreative;
import com.mordenkainen.equivalentenergistics.integration.ae2.storagechannel.IEMCStorageChannel;

import appeng.api.AEApi;
import appeng.api.storage.IMEInventory;
import appeng.api.storage.IMEInventoryHandler;
import appeng.api.storage.ISaveProvider;
import appeng.api.storage.IStorageChannel;
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

    @SuppressWarnings("unchecked")
    @Override
    public <T extends IAEStack<T>> IMEInventoryHandler<T> getCellInventory(final ItemStack stack, final ISaveProvider host, final IStorageChannel<T> channel) {
        if (channel == AEApi.instance().storage().getStorageChannel(IEMCStorageChannel.class) && isCell(stack)) {
            return (IMEInventoryHandler<T>) new HandlerEMCCellCreative(host);
        }
        return null;
    }

    @Override
    public <T extends IAEStack<T>> int getStatusForCell(final ItemStack stack, final IMEInventory<T> inv) {
        return 1;
    }
    
    @Override
    public <T extends IAEStack<T>> double cellIdleDrain(final ItemStack stack, final IMEInventory<T> inv) {
        return 0;
    }


}
