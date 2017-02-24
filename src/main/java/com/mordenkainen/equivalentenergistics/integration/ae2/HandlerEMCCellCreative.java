package com.mordenkainen.equivalentenergistics.integration.ae2;

import com.mordenkainen.equivalentenergistics.items.ItemEMCCellCreative;

import appeng.api.storage.ISaveProvider;

public class HandlerEMCCellCreative extends HandlerEMCCellBase {

	public HandlerEMCCellCreative(final ISaveProvider saveProvider) {
		super(saveProvider);
	}

	@Override
	public float addEMC(final float amount) {
		return amount;
	}

	@Override
	public float extractEMC(final float amount) {
		return amount;
	}

	@Override
	public int getCellStatus() {
		return 1;
	}

	@Override
	public float getCapacity() {
		return ItemEMCCellCreative.capacity;
	}

	@Override
	public float getEMC() {
		return ItemEMCCellCreative.capacity / 2;
	}

	@Override
	public float getAvail() {
		return ItemEMCCellCreative.capacity / 2;
	}

}
