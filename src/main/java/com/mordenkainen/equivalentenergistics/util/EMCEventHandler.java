package com.mordenkainen.equivalentenergistics.util;

import com.mordenkainen.equivalentenergistics.config.ConfigManager;
import com.mordenkainen.equivalentenergistics.tiles.TileEMCCrafter;

import com.pahimar.ee3.api.event.EnergyValueEvent;
import com.pahimar.ee3.api.event.PlayerKnowledgeEvent;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

import java.util.Iterator;

import moze_intel.projecte.api.event.EMCRemapEvent;
import moze_intel.projecte.api.event.PlayerKnowledgeChangeEvent;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;

public class EMCEventHandler {
	public EMCEventHandler() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Optional.Method(modid="ProjectE")
	@SubscribeEvent
	public void onPlayerKnowledgeChange(PlayerKnowledgeChangeEvent event) {
		if (!ConfigManager.useEE3) {
			if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
				Iterator<DimensionalLocation> iter = TileEMCCrafter.crafterTiles.iterator();
				while (iter.hasNext()) {
					DimensionalLocation currentLoc = (DimensionalLocation)iter.next();
					TileEntity crafter = currentLoc.getTE();
					if ((crafter == null) || (!(crafter instanceof TileEMCCrafter))) {
						iter.remove();
					} else {
						((TileEMCCrafter)crafter).playerKnowledgeChange(event.playerUUID);
					}
				}
			}
		}
	}

	@Optional.Method(modid="ProjectE")
	@SubscribeEvent
	public void onEnergyValueChange(EMCRemapEvent event) {
		if (!ConfigManager.useEE3) {
			if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
				EMCUtils.getInstance().relearnCrystals();
				EMCCraftingPattern.relearnPatterns();
				Iterator<DimensionalLocation> iter = TileEMCCrafter.crafterTiles.iterator();
				while (iter.hasNext()) {
					DimensionalLocation currentLoc = (DimensionalLocation)iter.next();
					TileEntity crafter = currentLoc.getTE();
					if ((crafter == null) || (!(crafter instanceof TileEMCCrafter))) {
						iter.remove();
					} else {
						((TileEMCCrafter)crafter).energyValueEvent();
					}
				}
			}
		}
	}

	@Optional.Method(modid="EE3")
	@SubscribeEvent
	public void onPlayerKnowledgeChange(PlayerKnowledgeEvent event)	{
		if (ConfigManager.useEE3) {
			if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
				Iterator<DimensionalLocation> iter = TileEMCCrafter.crafterTiles.iterator();
				while (iter.hasNext()) {
					DimensionalLocation currentLoc = (DimensionalLocation)iter.next();
					TileEntity crafter = currentLoc.getTE();
					if ((crafter == null) || (!(crafter instanceof TileEMCCrafter))) {
						iter.remove();
					} else {
						((TileEMCCrafter)crafter).playerKnowledgeChange(event.playerUUID);
					}
				}
			}
		}
	}

	@Optional.Method(modid="EE3")
	@SubscribeEvent
	public void onEnergyValueChange(EnergyValueEvent event)	{
		if (!ConfigManager.useEE3) {
			if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
				EMCUtils.getInstance().relearnCrystals();
				EMCCraftingPattern.relearnPatterns();
				Iterator<DimensionalLocation> iter = TileEMCCrafter.crafterTiles.iterator();
				while (iter.hasNext()) {
					DimensionalLocation currentLoc = (DimensionalLocation)iter.next();
					TileEntity crafter = currentLoc.getTE();
					if ((crafter == null) || (!(crafter instanceof TileEMCCrafter))) {
						iter.remove();
					} else {
						((TileEMCCrafter)crafter).energyValueEvent();
					}
				}
			}
		}
	}
}
