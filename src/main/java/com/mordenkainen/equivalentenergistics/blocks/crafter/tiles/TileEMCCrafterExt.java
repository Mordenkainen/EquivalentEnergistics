package com.mordenkainen.equivalentenergistics.blocks.crafter.tiles;

import com.mordenkainen.equivalentenergistics.core.config.EqEConfig;

public class TileEMCCrafterExt extends TileEMCCrafterAdv {

    public TileEMCCrafterExt() {
        this(8, EqEConfig.emcAssembler.craftingTime, 2);
    }

    public TileEMCCrafterExt(final int jobs, final double time, final int meta) {
        super(jobs, time, meta);
    }

}
