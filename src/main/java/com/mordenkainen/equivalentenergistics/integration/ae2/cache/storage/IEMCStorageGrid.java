package com.mordenkainen.equivalentenergistics.integration.ae2.cache.storage;

import com.mordenkainen.equivalentenergistics.integration.ae2.cache.ICacheBase;
import com.mordenkainen.equivalentenergistics.util.IEMCStorage;

import appeng.api.config.Actionable;
import appeng.api.networking.IGrid;

public interface IEMCStorageGrid extends ICacheBase, IEMCStorage {

    IGrid getGrid();

    double addEMC(double emc, Actionable mode);

    double extractEMC(double emc, Actionable mode);

}
