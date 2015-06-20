package com.mordenkainen.equivalentenergistics.proxy;

import com.mordenkainen.equivalentenergistics.render.BlockEMCCrafterRenderer;
import com.mordenkainen.equivalentenergistics.render.TileEMCCrafterRenderer;
import com.mordenkainen.equivalentenergistics.tiles.TileEMCCrafter;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy {
	public void initRenderers() {
		ClientRegistry.bindTileEntitySpecialRenderer(TileEMCCrafter.class, new TileEMCCrafterRenderer());
		EMCCrafterRenderer = RenderingRegistry.getNextAvailableRenderId();
		RenderingRegistry.registerBlockHandler(new BlockEMCCrafterRenderer());
    }
}
