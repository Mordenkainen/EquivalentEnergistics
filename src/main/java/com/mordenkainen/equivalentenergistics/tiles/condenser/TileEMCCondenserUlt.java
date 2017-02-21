package com.mordenkainen.equivalentenergistics.tiles.condenser;

public class TileEMCCondenserUlt extends TileEMCCondenserExt {
	
	@Override
	protected float getEMCPerTick() {
		 return Float.MAX_VALUE;
	}
	
	@Override
	protected int itemsToTransfer() {
		return 256;
	}
}
