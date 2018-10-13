package com.mordenkainen.equivalentenergistics.integration.ae2.cells;

import com.mordenkainen.equivalentenergistics.integration.ae2.storagechannel.IAEEMCStack;
import com.mordenkainen.equivalentenergistics.integration.ae2.storagechannel.IEMCStorageChannel;

import appeng.api.AEApi;
import appeng.api.config.AccessRestriction;
import appeng.api.config.IncludeExclude;
import appeng.api.storage.ICellInventory;
import appeng.api.storage.ICellInventoryHandler;
import appeng.api.storage.ISaveProvider;
import appeng.api.storage.IStorageChannel;

public abstract class HandlerEMCCellBase implements ICellInventoryHandler<IAEEMCStack> {

    protected final ISaveProvider saveProvider;

    public HandlerEMCCellBase(final ISaveProvider saveProvider) {
        this.saveProvider = saveProvider;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public IStorageChannel getChannel() {
        return AEApi.instance().storage().getStorageChannel(IEMCStorageChannel.class);
    }

    @Override
    public AccessRestriction getAccess() {
        return AccessRestriction.READ_WRITE;
    }

    @Override
    public boolean isPrioritized(final IAEEMCStack input) {
        return false;
    }

    @Override
    public boolean canAccept(final IAEEMCStack input) {
        return input != null;
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
        return true;
    }
    
    @Override
    public ICellInventory<IAEEMCStack> getCellInv() {
        return null;
    }

    @Override
    public IncludeExclude getIncludeExcludeMode() {
        return IncludeExclude.BLACKLIST;
    }

    @Override
    public boolean isFuzzy() {
        return false;
    }

    @Override
    public boolean isPreformatted() {
        return false;
    }

    public abstract int getCellStatus();

}
