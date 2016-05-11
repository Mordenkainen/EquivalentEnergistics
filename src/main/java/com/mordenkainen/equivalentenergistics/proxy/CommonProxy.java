package com.mordenkainen.equivalentenergistics.proxy;

import com.mordenkainen.equivalentenergistics.lib.Ref;
import com.mordenkainen.equivalentenergistics.registries.BlockEnum;
import com.mordenkainen.equivalentenergistics.registries.ItemEnum;
import com.mordenkainen.equivalentenergistics.tiles.TileEMCCondenser;
import com.mordenkainen.equivalentenergistics.tiles.TileEMCCrafter;

import cpw.mods.fml.common.registry.GameRegistry;

public class CommonProxy {
	public int EMCCrafterRenderer;

	public boolean isClient() {
		return false;
	}

	public boolean isServer() {
		return true;
	}
	
	public void registerItems() {
		for (final ItemEnum current : ItemEnum.values()) {
			if(current.isEnabled()) {
				GameRegistry.registerItem(current.getItem(), current.getInternalName());
			}
		}
	}
	
	public void registerBlocks() {
		for (final BlockEnum current : BlockEnum.values()) {
			if(current.isEnabled()) {
				GameRegistry.registerBlock(current.getBlock(), current.getItemBlockClass(), current.getInternalName());
			}
		}
	}
	
	public void registerTileEntities() {
		GameRegistry.registerTileEntity(TileEMCCondenser.class, Ref.MOD_ID + "TileEMCCondenser");
		GameRegistry.registerTileEntity(TileEMCCrafter.class, Ref.MOD_ID + "TileEMCCrafter");
	}
	
	public void initRenderers() {}
	
	public void unmetDependency() {
		throw new RuntimeException("Equivalent Energistics requires either Equivalent Exchange 3 or ProjectE to be installed and enabled!");
	}
}
