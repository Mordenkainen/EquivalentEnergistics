package com.mordenkainen.equivalentenergistics.blocks.crafter.tiles;

import com.mordenkainen.equivalentenergistics.core.config.Config;

public class TileEMCCrafterExt extends TileEMCCrafterAdv {

	public TileEMCCrafterExt() {
		this(8, Config.crafterCraftingTime, 2);
	}

	public TileEMCCrafterExt(int jobs, double time, int meta) {
		super(jobs, time, meta);
	}

}
