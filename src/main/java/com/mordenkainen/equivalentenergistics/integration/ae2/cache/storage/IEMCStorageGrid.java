package com.mordenkainen.equivalentenergistics.integration.ae2.cache.storage;

import com.mordenkainen.equivalentenergistics.integration.ae2.cache.ICacheBase;

import appeng.api.config.Actionable;

public interface IEMCStorageGrid extends ICacheBase {
    
	float injectEMC(float emc, Actionable mode);

	float extractEMC(float emc, Actionable mode);

	float getCurrentEMC();

	float getMaxEMC();

	float getAvail();

	boolean isFull();

	boolean isEmpty();
	
}
