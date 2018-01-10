package com.mordenkainen.equivalentenergistics.blocks.crafter.tiles;

import com.mordenkainen.equivalentenergistics.core.config.EqEConfig;

public class TileEMCCrafterExt extends TileEMCCrafterAdv {

	public TileEMCCrafterExt() {
		this(8, EqEConfig.emcAssembler.craftingTime, 2);
	}

	public TileEMCCrafterExt(int jobs, double time, int meta) {
		super(jobs, time, meta);
	}

}
