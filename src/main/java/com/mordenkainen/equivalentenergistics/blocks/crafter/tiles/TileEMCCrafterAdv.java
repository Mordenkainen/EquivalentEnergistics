package com.mordenkainen.equivalentenergistics.blocks.crafter.tiles;

import com.mordenkainen.equivalentenergistics.core.config.EqEConfig;

public class TileEMCCrafterAdv extends TileEMCCrafter {

    public TileEMCCrafterAdv() {
        this(4, EqEConfig.emcAssembler.craftingTime, 1);
    }

    public TileEMCCrafterAdv(int jobs, double time, int meta) {
        super(jobs, time, meta);
    }

}
