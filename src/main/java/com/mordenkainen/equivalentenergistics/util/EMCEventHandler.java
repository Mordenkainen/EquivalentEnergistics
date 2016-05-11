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
	public void onPlayerKnowledgeChange(final PlayerKnowledgeChangeEvent event) {
		if (!ConfigManager.useEE3 && FMLCommonHandler.instance().getEffectiveSide().isServer()) {
			final Iterator<DimensionalLocation> iter = TileEMCCrafter.crafterTiles.iterator();
			while (iter.hasNext()) {
				final DimensionalLocation currentLoc = (DimensionalLocation)iter.next();
				final TileEntity crafter = currentLoc.getTE();
				if (crafter instanceof TileEMCCrafter) {
					((TileEMCCrafter)crafter).playerKnowledgeChange(event.playerUUID);
				} else {
					iter.remove();
				}
			}
		}
	}

	@Optional.Method(modid="ProjectE")
	@SubscribeEvent
	public void onEnergyValueChange(final EMCRemapEvent event) {
		if (!ConfigManager.useEE3 && FMLCommonHandler.instance().getEffectiveSide().isServer()) {
			EMCUtils.getInstance().relearnCrystals();
			EMCCraftingPattern.relearnPatterns();
			final Iterator<DimensionalLocation> iter = TileEMCCrafter.crafterTiles.iterator();
			while (iter.hasNext()) {
				final DimensionalLocation currentLoc = (DimensionalLocation)iter.next();
				final TileEntity crafter = currentLoc.getTE();
				if (crafter instanceof TileEMCCrafter) {
					((TileEMCCrafter)crafter).energyValueEvent();
				} else {
					iter.remove();
				}
			}
		}
	}

	@Optional.Method(modid="EE3")
	@SubscribeEvent
	public void onPlayerKnowledgeChange(final PlayerKnowledgeEvent event)	{
		if (ConfigManager.useEE3 && FMLCommonHandler.instance().getEffectiveSide().isServer()) {
			final Iterator<DimensionalLocation> iter = TileEMCCrafter.crafterTiles.iterator();
			while (iter.hasNext()) {
				final DimensionalLocation currentLoc = (DimensionalLocation)iter.next();
				final TileEntity crafter = currentLoc.getTE();
				if (crafter instanceof TileEMCCrafter) {
					((TileEMCCrafter)crafter).playerKnowledgeChange(event.playerUUID);
				} else {
					iter.remove();
				}
			}
		}
	}

	@Optional.Method(modid="EE3")
	@SubscribeEvent
	public void onEnergyValueChange(final EnergyValueEvent event)	{
		if (!ConfigManager.useEE3 && FMLCommonHandler.instance().getEffectiveSide().isServer()) {
			EMCUtils.getInstance().relearnCrystals();
			EMCCraftingPattern.relearnPatterns();
			final Iterator<DimensionalLocation> iter = TileEMCCrafter.crafterTiles.iterator();
			while (iter.hasNext()) {
				final DimensionalLocation currentLoc = (DimensionalLocation)iter.next();
				final TileEntity crafter = currentLoc.getTE();
				if (crafter instanceof TileEMCCrafter) {
					((TileEMCCrafter)crafter).energyValueEvent();
				} else {
					iter.remove();
				}
			}
		}
	}
}
