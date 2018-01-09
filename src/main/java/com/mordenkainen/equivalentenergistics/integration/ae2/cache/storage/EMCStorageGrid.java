package com.mordenkainen.equivalentenergistics.integration.ae2.cache.storage;

import com.mordenkainen.equivalentenergistics.util.EMCPool;

import appeng.api.config.Actionable;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
import appeng.api.networking.events.MENetworkCellArrayUpdate;
import appeng.api.networking.events.MENetworkEventSubscribe;
import appeng.api.networking.events.MENetworkPostCacheConstruction;
import appeng.api.networking.storage.IStorageGrid;

public class EMCStorageGrid implements IEMCStorageGrid {

	private final IGrid grid;
	private final EMCPool pool = new EMCPool();
	private final EMCGridCellHandler cellHandler = new EMCGridCellHandler(this);
	private final EMCCrystalHandler crystalHandler = new EMCCrystalHandler(this);
	
	public EMCStorageGrid(final IGrid grid) {
        this.grid = grid;
    }
	
	@MENetworkEventSubscribe
    public void afterCacheConstruction(final MENetworkPostCacheConstruction cacheConstruction) {
		((IStorageGrid) grid.getCache(IStorageGrid.class)).registerCellProvider(crystalHandler);
    }
	
	@MENetworkEventSubscribe
    public void cellUpdate(final MENetworkCellArrayUpdate cellUpdate) {
		cellHandler.cellUpdate(cellUpdate);
    }
	
	@Override
	public void addNode(IGridNode gridNode, IGridHost machine) {
		cellHandler.addNode(gridNode, machine);
	}

	@Override
	public void onUpdateTick() {
		crystalHandler.updateDisplay();
	}

	@Override
	public void removeNode(IGridNode gridNode, IGridHost machine) {
		cellHandler.removeNode(gridNode, machine);
	}
	
	public IGrid getGrid() {
        return grid;
    }

	@Override
    public float getCurrentEMC() {
        return pool.getCurrentEMC();
    }

    @Override
    public float getMaxEMC() {
        return pool.getMaxEMC();
    }

    @Override
    public float getAvail() {
        return pool.getAvail();
    }

    @Override
    public boolean isFull() {
        return pool.isFull();
    }

    @Override
    public boolean isEmpty() {
        return pool.isEmpty();
    }

	@Override
	public void setCurrentEMC(float currentEMC) {
		pool.setCurrentEMC(currentEMC);
	}

	@Override
	public void setMaxEMC(float maxEMC) {
		pool.setMaxEMC(maxEMC);
	}

	@Override
	public float addEMC(float emc) {
		return pool.addEMC(emc);
	}

	@Override
	public float extractEMC(float emc) {
		return pool.extractEMC(emc);
	}
	
	@Override
	public float addEMC(float emc, Actionable mode) {
		return cellHandler.injectEMC(emc, mode);
	}

	@Override
	public float extractEMC(float emc, Actionable mode) {
		return cellHandler.extractEMC(emc, mode);
	}

	public void markDirty() {
        crystalHandler.markDirty();
    }
	
}
