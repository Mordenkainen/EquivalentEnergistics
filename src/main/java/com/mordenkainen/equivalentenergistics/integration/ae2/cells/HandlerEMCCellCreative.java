package com.mordenkainen.equivalentenergistics.integration.ae2.cells;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import com.mordenkainen.equivalentenergistics.core.config.EqEConfig;
import com.mordenkainen.equivalentenergistics.integration.ae2.storagechannel.EMCStackType;
import com.mordenkainen.equivalentenergistics.integration.ae2.storagechannel.IAEEMCStack;
import com.mordenkainen.equivalentenergistics.integration.ae2.storagechannel.IEMCStorageChannel;

import appeng.api.AEApi;
import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.storage.ISaveProvider;
import appeng.api.storage.data.IItemList;

public class HandlerEMCCellCreative extends HandlerEMCCellBase {

    public HandlerEMCCellCreative(final ISaveProvider saveProvider) {
        super(saveProvider);
    }

    @Override
    public int getCellStatus() {
        return 1;
    }

    @Override
    public IAEEMCStack extractItems(final IAEEMCStack request, final Actionable mode, final IActionSource src) {
        if (request == null) {
            return null;
        }
        return request;
    }

    @Override
    public IItemList<IAEEMCStack> getAvailableItems(final IItemList<IAEEMCStack> stacks) {
        IAEEMCStack current = AEApi.instance().storage().getStorageChannel(IEMCStorageChannel.class).createStack(EqEConfig.cellCapacities.creativeCell / 2);
        stacks.add(current);
        
        Pair<Double, EMCStackType> cellInfo = new ImmutablePair<Double, EMCStackType>(EqEConfig.cellCapacities.creativeCell, EMCStackType.CAPACITY);
        IAEEMCStack max = AEApi.instance().storage().getStorageChannel(IEMCStorageChannel.class).createStack(cellInfo);
        stacks.add(max);
        
        return stacks;
    }

    @Override
    public IAEEMCStack injectItems(final IAEEMCStack input, final Actionable mode, final IActionSource src) {
        return null;
    }

}
