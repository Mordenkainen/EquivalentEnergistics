package com.mordenkainen.equivalentenergistics.integration.ae2.cache;

import appeng.api.networking.IGridCache;
import appeng.api.networking.IGridStorage;

public interface ICacheBase extends IGridCache {

	@Override
    default void onSplit(final IGridStorage dstStorage) {}

    @Override
    default void onJoin(final IGridStorage sourceStorage) {}

    @Override
    default void populateGridStorage(final IGridStorage dstStorage) {}
    
}
