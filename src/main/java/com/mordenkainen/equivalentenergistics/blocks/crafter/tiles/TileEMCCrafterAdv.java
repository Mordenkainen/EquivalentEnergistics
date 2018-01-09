package com.mordenkainen.equivalentenergistics.blocks.crafter.tiles;

import com.mordenkainen.equivalentenergistics.core.config.Config;

public class TileEMCCrafterAdv extends TileEMCCrafter {

	public TileEMCCrafterAdv() {
		this(4, Config.crafter_Crafting_Time, 1);
	}

	public TileEMCCrafterAdv(int jobs, double time, int meta) {
		super(jobs, time, meta);
	}

}
