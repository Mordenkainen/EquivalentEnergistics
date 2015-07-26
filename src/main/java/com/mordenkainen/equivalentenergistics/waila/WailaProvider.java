package com.mordenkainen.equivalentenergistics.waila;

import com.mordenkainen.equivalentenergistics.tiles.TileEMCCondenser;
import com.mordenkainen.equivalentenergistics.tiles.TileEMCCrafter;

import mcp.mobius.waila.api.IWailaRegistrar;

public class WailaProvider {
	public static void callbackRegister(IWailaRegistrar registrar) {
		registrar.registerBodyProvider(new CondenserHUDHandler(), TileEMCCondenser.class);
		
		registrar.registerBodyProvider(new CrafterHUDHandler(), TileEMCCrafter.class);
	}
}
