package com.mordenkainen.equivalentenergistics.integration.ae2.cache;

import java.util.ArrayList;
import java.util.List;

import com.mordenkainen.equivalentenergistics.registries.ItemEnum;

import appeng.api.config.AccessRestriction;
import appeng.api.storage.ICellProvider;
import appeng.api.storage.IMEInventoryHandler;
import appeng.api.storage.StorageChannel;
import appeng.api.storage.data.IAEItemStack;

public interface IGridCellHandler extends ICellProvider, IMEInventoryHandler<IAEItemStack> {
	
	@SuppressWarnings("rawtypes")
    @Override
    default List<IMEInventoryHandler> getCellArray(final StorageChannel channel) {
        final List<IMEInventoryHandler> list = new ArrayList<IMEInventoryHandler>();

        if (channel == StorageChannel.ITEMS) {
            list.add(this);
        }

        return list;
    }

    @Override
    default int getPriority() {
        return Integer.MAX_VALUE - 1;
    }

    @Override
    default AccessRestriction getAccess() {
        return AccessRestriction.READ_WRITE;
    }

    @Override
    default boolean isPrioritized(final IAEItemStack stack) {
        return ItemEnum.isCrystal(stack.getItemStack());
    }

    @Override
    default boolean canAccept(final IAEItemStack stack) {
    	return ItemEnum.isCrystal(stack.getItemStack());
    }
    
    @Override
    default int getSlot() {
        return 0;
    }

    @Override
    default boolean validForPass(final int pass) {
        return pass == 1;
    }
    
    @Override
    default StorageChannel getChannel() {
        return StorageChannel.ITEMS;
    }

}
