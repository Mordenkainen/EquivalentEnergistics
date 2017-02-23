package com.mordenkainen.equivalentenergistics.integration.ae2.cache;

import appeng.api.config.Actionable;
import appeng.api.networking.IGridCache;

public interface IEMCStorageGrid extends IGridCache {

	float injectEMC(float emc, Actionable mode);

	float extractEMC(float emc, Actionable mode);

	float getCurrentEMC();

	float getMaxEMC();

	float getAvail();

	boolean isFull();

	boolean isEmpty();

}