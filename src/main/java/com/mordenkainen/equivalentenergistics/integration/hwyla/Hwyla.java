package com.mordenkainen.equivalentenergistics.integration.hwyla;

import com.mordenkainen.equivalentenergistics.blocks.condenser.tiles.TileEMCCondenser;
import com.mordenkainen.equivalentenergistics.blocks.crafter.tiles.TileEMCCrafter;
import com.mordenkainen.equivalentenergistics.integration.ae2.tiles.TileAEBase;

import mcp.mobius.waila.api.IWailaRegistrar;

public final class Hwyla {

    private Hwyla() {}

    public static void register(final IWailaRegistrar registrar) {
        registrar.registerBodyProvider(new WailaAEBaseHUDHandler(), TileAEBase.class);
        final WailaCrafterHUDHandler crafterHandler = new WailaCrafterHUDHandler();
        registrar.registerBodyProvider(crafterHandler, TileEMCCrafter.class);
        registrar.registerNBTProvider(crafterHandler, TileEMCCrafter.class);
        final WailaCondenserHUDHandler condenserHandler = new WailaCondenserHUDHandler();
        registrar.registerBodyProvider(condenserHandler, TileEMCCondenser.class);
        registrar.registerNBTProvider(condenserHandler, TileEMCCondenser.class);
    }

}
