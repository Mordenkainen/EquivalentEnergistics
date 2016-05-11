package com.mordenkainen.equivalentenergistics.proxy;

import com.mordenkainen.equivalentenergistics.render.BlockEMCCrafterRenderer;
import com.mordenkainen.equivalentenergistics.render.TileEMCCrafterRenderer;
import com.mordenkainen.equivalentenergistics.tiles.TileEMCCrafter;
import com.mordenkainen.equivalentenergistics.util.UnmetDependencyException;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy {
	
	@Override
	public boolean isClient() {
		return true;
	}

	@Override
	public boolean isServer() {
		return false;
	}
	
	@Override
	public void initRenderers() {
		ClientRegistry.bindTileEntitySpecialRenderer(TileEMCCrafter.class, new TileEMCCrafterRenderer());
		crafterRenderer = RenderingRegistry.getNextAvailableRenderId();
		RenderingRegistry.registerBlockHandler(new BlockEMCCrafterRenderer());
    }
	
	@Override
	public void unmetDependency() {
		throw new UnmetDependencyException();
	}
}
