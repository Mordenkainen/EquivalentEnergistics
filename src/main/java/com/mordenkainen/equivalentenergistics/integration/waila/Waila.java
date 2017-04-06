package com.mordenkainen.equivalentenergistics.integration.waila;

import com.mordenkainen.equivalentenergistics.blocks.condenser.tiles.TileEMCCondenserBase;
import com.mordenkainen.equivalentenergistics.blocks.crafter.tiles.TileEMCCrafterBase;
import com.mordenkainen.equivalentenergistics.integration.ae2.tiles.TileAEBase;

import cpw.mods.fml.common.event.FMLInterModComms;
import mcp.mobius.waila.api.IWailaRegistrar;

public final class Waila {

    private Waila() {}

    public static void init() {
        FMLInterModComms.sendMessage("Waila", "register", Waila.class.getName() + ".register");
    }

    public static void register(final IWailaRegistrar registrar) {
        registrar.registerBodyProvider(new WailaAEBaseHUDHandler(), TileAEBase.class);
        final WailaCrafterHUDHandler crafterHandler = new WailaCrafterHUDHandler();
        registrar.registerBodyProvider(crafterHandler, TileEMCCrafterBase.class);
        registrar.registerNBTProvider(crafterHandler, TileEMCCrafterBase.class);
        final WailaCondenserHUDHandler condenserHandler = new WailaCondenserHUDHandler();
        registrar.registerBodyProvider(condenserHandler, TileEMCCondenserBase.class);
        registrar.registerNBTProvider(condenserHandler, TileEMCCondenserBase.class);
    }

}
