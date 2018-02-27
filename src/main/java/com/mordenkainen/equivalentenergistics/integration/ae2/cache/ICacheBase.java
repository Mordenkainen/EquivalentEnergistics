package com.mordenkainen.equivalentenergistics.integration.ae2.cache;

import appeng.api.networking.IGridCache;
import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
import appeng.api.networking.IGridStorage;

public interface ICacheBase extends IGridCache {

    @Override
    default void onSplit(final IGridStorage dstStorage) {}

    @Override
    default void onJoin(final IGridStorage sourceStorage) {}

    @Override
    default void populateGridStorage(final IGridStorage dstStorage) {}
    
    @Override
    default void addNode(IGridNode arg0, IGridHost arg1) {}

    @Override
    default void removeNode(IGridNode arg0, IGridHost arg1) {}
    
}