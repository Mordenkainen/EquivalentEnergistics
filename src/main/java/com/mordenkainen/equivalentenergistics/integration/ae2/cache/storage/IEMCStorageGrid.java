package com.mordenkainen.equivalentenergistics.integration.ae2.cache.storage;

import com.mordenkainen.equivalentenergistics.integration.ae2.cache.ICacheBase;

import appeng.api.networking.IGrid;

public interface IEMCStorageGrid extends ICacheBase {

    IGrid getGrid();

}
