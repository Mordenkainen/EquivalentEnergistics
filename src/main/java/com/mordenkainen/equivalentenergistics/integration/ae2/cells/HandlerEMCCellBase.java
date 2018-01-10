package com.mordenkainen.equivalentenergistics.integration.ae2.cells;

import com.mordenkainen.equivalentenergistics.util.IEMCStorage;

import appeng.api.AEApi;
import appeng.api.config.AccessRestriction;
import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.storage.IMEInventoryHandler;
import appeng.api.storage.ISaveProvider;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IItemList;

public abstract class HandlerEMCCellBase implements IMEInventoryHandler<IAEItemStack>, IEMCStorage {

    protected final ISaveProvider saveProvider;

    public HandlerEMCCellBase(final ISaveProvider saveProvider) {
        this.saveProvider = saveProvider;
    }

    @Override
    public IAEItemStack injectItems(final IAEItemStack input, final Actionable type, final IActionSource src) {
        return input;
    }

    @Override
    public IAEItemStack extractItems(final IAEItemStack request, final Actionable mode, final IActionSource src) {
        return null;
    }

    @Override
    public IItemList<IAEItemStack> getAvailableItems(final IItemList<IAEItemStack> stacks) {
        return stacks;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public IStorageChannel getChannel() {
        return AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class);
    }

    @Override
    public AccessRestriction getAccess() {
        return AccessRestriction.READ_WRITE;
    }

    @Override
    public boolean isPrioritized(final IAEItemStack input) {
        return false;
    }

    @Override
    public boolean canAccept(final IAEItemStack input) {
        return false;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public int getSlot() {
        return 0;
    }

    @Override
    public boolean validForPass(final int pass) {
        return false;
    }

    public abstract int getCellStatus();

}
