package com.mordenkainen.equivalentenergistics.integration.ae2.cache.storage;

import appeng.api.networking.IGrid;
import appeng.api.networking.events.MENetworkEventSubscribe;
import appeng.api.networking.events.MENetworkPostCacheConstruction;
import appeng.api.networking.storage.IStorageGrid;

public class EMCStorageGrid implements IEMCStorageGrid {

    private final IGrid grid;
    private final EMCCrystalHandler crystalHandler = new EMCCrystalHandler(this);

    public EMCStorageGrid(final IGrid grid) {
        this.grid = grid;
    }

    @MENetworkEventSubscribe
    public void afterCacheConstruction(final MENetworkPostCacheConstruction cacheConstruction) {
        ((IStorageGrid) grid.getCache(IStorageGrid.class)).registerCellProvider(crystalHandler);
    }
    
    @Override
    public void onUpdateTick() {
        crystalHandler.updateDisplay();
    }

    @Override
    public IGrid getGrid() {
        return grid;
    }
    
}
