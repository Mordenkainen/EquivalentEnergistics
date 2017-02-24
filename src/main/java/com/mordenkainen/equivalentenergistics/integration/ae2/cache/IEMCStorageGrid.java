package com.mordenkainen.equivalentenergistics.integration.ae2.cache;

import appeng.api.config.Actionable;
import appeng.api.networking.IGridCache;
import appeng.api.networking.IGridStorage;

public interface IEMCStorageGrid extends IGridCache {

	@Override
    default void onSplit(final IGridStorage dstStorage) {}

    @Override
    default void onJoin(final IGridStorage sourceStorage) {}

    @Override
    default void populateGridStorage(final IGridStorage dstStorage) {}
    
	float injectEMC(float emc, Actionable mode);

	float extractEMC(float emc, Actionable mode);

	float getCurrentEMC();

	float getMaxEMC();

	float getAvail();

	boolean isFull();

	boolean isEmpty();
	
}
