package com.mordenkainen.equivalentenergistics.util;

import java.util.ArrayList;
import java.util.List;

import com.mordenkainen.equivalentenergistics.config.ConfigManager;
import com.mordenkainen.equivalentenergistics.tiles.TileEMCCrafter;
import com.pahimar.ee3.api.event.EnergyValueEvent;
import com.pahimar.ee3.api.event.PlayerKnowledgeEvent;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import moze_intel.projecte.api.event.EMCRemapEvent;
import moze_intel.projecte.api.event.PlayerKnowledgeChangeEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;

public class EventHandlerPE {
	public EventHandlerPE() {
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@SubscribeEvent
	public void onPlayerKnowledgeChange(PlayerKnowledgeChangeEvent event) {
		if(FMLCommonHandler.instance().getEffectiveSide().isServer()) {
			for(TileEMCCrafter crafter : TileEMCCrafter.crafterTiles) {
				crafter.playerKnowledgeChange(event.playerUUID);
			}
		}
	}
	
	@SubscribeEvent
	public void onEnergyValueChange(EMCRemapEvent event) {
		if(FMLCommonHandler.instance().getEffectiveSide().isServer()) {
			for(TileEMCCrafter crafter : TileEMCCrafter.crafterTiles) {
				crafter.energyValueEvent();
			}
		}
	}
}
