package com.mordenkainen.equivalentenergistics.integration.waila;

import cpw.mods.fml.common.event.FMLInterModComms;
import mcp.mobius.waila.api.IWailaRegistrar;

public final class Waila {
	
	private Waila() {}
	
	public static void init() {
		FMLInterModComms.sendMessage("Waila", "register", Waila.class.getName() + ".register");
	}
	
	public static void register(final IWailaRegistrar registrar) {
		final WailaHUDHandler wailaHUDHandler = new WailaHUDHandler();
		registrar.registerBodyProvider(wailaHUDHandler, IWailaNBTProvider.class);
		registrar.registerNBTProvider(wailaHUDHandler, IWailaNBTProvider.class);
	}
}
