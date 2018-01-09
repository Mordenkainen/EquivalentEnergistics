package com.mordenkainen.equivalentenergistics.integration.ae2.cells;

import com.mordenkainen.equivalentenergistics.core.config.Config;

import appeng.api.storage.ISaveProvider;

public class HandlerEMCCellCreative extends HandlerEMCCellBase {

	public HandlerEMCCellCreative(final ISaveProvider saveProvider) {
        super(saveProvider);
    }
	
	@Override
	public int getCellStatus() {
		return 1;
	}

	@Override
	public float getCurrentEMC() {
		return Config.creative_Capacity / 2;
	}

	@Override
	public void setCurrentEMC(float currentEMC) {}

	@Override
	public float getMaxEMC() {
		return Config.creative_Capacity;
	}

	@Override
	public void setMaxEMC(float maxEMC) {}

	@Override
	public float getAvail() {
		return Config.creative_Capacity / 2;
	}

	@Override
	public boolean isFull() {
		return false;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public float addEMC(float emc) {
		return emc;
	}

	@Override
	public float extractEMC(float emc) {
		return emc;
	}

}
