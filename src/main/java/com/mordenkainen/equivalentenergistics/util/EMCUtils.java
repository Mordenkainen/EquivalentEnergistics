package com.mordenkainen.equivalentenergistics.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import com.mordenkainen.equivalentenergistics.EquivalentEnergistics;
import com.mordenkainen.equivalentenergistics.config.ConfigManager;
import com.mordenkainen.equivalentenergistics.tiles.TileEMCCrafter;
import com.pahimar.ee3.api.exchange.EnergyValueRegistryProxy;
import com.pahimar.ee3.api.knowledge.AbilityRegistryProxy;
import com.pahimar.ee3.api.knowledge.TransmutationKnowledgeRegistryProxy;
import com.pahimar.ee3.util.ItemHelper;

import cpw.mods.fml.common.registry.GameRegistry;
import moze_intel.projecte.api.ProjectEAPI;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class EMCUtils {
	private static EMCUtils instance;
	private static Item tomeItem;
	
	private EMCUtils() {}

	public static EMCUtils getInstance() {
		if(instance == null) {
			instance = new EMCUtils();
		}
		return instance;
	}
	
	public boolean hasEMC(ItemStack itemStack) {
		if(ConfigManager.useEE3) {
			return EnergyValueRegistryProxy.hasEnergyValue(itemStack);
		} else {
			return ProjectEAPI.getEMCProxy().hasValue(itemStack);
		}
	}

	public float getEnergyValue(ItemStack itemStack) {
		if(ConfigManager.useEE3) {
			return EnergyValueRegistryProxy.getEnergyValue(itemStack).getValue();
		} else {
			return ProjectEAPI.getEMCProxy().getValue(itemStack);
		}
	}

	public float getCrystalEMC() {
		return getCrystalEMC(0);
	}
	
	public float getCrystalEMC(int tier) {
		if(ConfigManager.useEE3) {
			return EnergyValueRegistryProxy.getEnergyValue(new ItemStack(EquivalentEnergistics.itemEMCCrystal, 1, tier)).getValue();
		} else {
			return ProjectEAPI.getEMCProxy().getValue(new ItemStack(EquivalentEnergistics.itemEMCCrystal, 1, tier));
		}
	}

	public ArrayList<ItemStack> getTransmutations(TileEMCCrafter tile) {
		ArrayList<ItemStack> transmutations = new ArrayList<ItemStack>();
		if(ConfigManager.useEE3) {
			transmutations.addAll(TransmutationKnowledgeRegistryProxy.getPlayerKnownTransmutations(ItemHelper.getOwnerUUID(tile.getCurrentTome())));
		} else {
			transmutations.addAll(ProjectEAPI.getTransmutationProxy().getKnowledge(UUID.fromString(tile.getCurrentTome().getTagCompound().getString("OwnerUUID"))));
		}
		return transmutations;
	}

	public boolean isValidItem(ItemStack itemStack) {
		if(ConfigManager.useEE3) {
			if(tomeItem == null) {
				tomeItem = GameRegistry.findItem("EE3", "alchemicalTome");
			}
			return itemStack != null && itemStack.getItem() == tomeItem && ItemHelper.hasOwnerUUID(itemStack);
		} else {
			return itemStack != null && itemStack.getItem() == EquivalentEnergistics.itemEMCBook && itemStack.hasTagCompound() && itemStack.getTagCompound().hasKey("OwnerUUID");
		}
	}
	
	public void setCrystalEMC(float emc) {
		if(ConfigManager.useEE3) {
			EnergyValueRegistryProxy.addPreAssignedEnergyValue(new ItemStack(EquivalentEnergistics.itemEMCCrystal, 1, 0), emc);
			EnergyValueRegistryProxy.addPreAssignedEnergyValue(new ItemStack(EquivalentEnergistics.itemEMCCrystal, 1, 1), emc * 576);
			EnergyValueRegistryProxy.addPreAssignedEnergyValue(new ItemStack(EquivalentEnergistics.itemEMCCrystal, 1, 2), (float)(emc * Math.pow(576, 2)));
	    	AbilityRegistryProxy.setAsNotLearnable(EquivalentEnergistics.itemEMCCrystal);
		} else {
			ProjectEAPI.getEMCProxy().registerCustomEMC(new ItemStack(EquivalentEnergistics.itemEMCCrystal, 1, 0), (int)emc);
			ProjectEAPI.getEMCProxy().registerCustomEMC(new ItemStack(EquivalentEnergistics.itemEMCCrystal, 1, 1), (int)emc * 576);
			ProjectEAPI.getEMCProxy().registerCustomEMC(new ItemStack(EquivalentEnergistics.itemEMCCrystal, 1, 2), (int)(emc * Math.pow(576, 2)));
		}
	}

	public UUID getTomeUUID(ItemStack currentTome) {
		if(ConfigManager.useEE3) {
			return ItemHelper.getOwnerUUID(currentTome);
		} else {
			return UUID.fromString(currentTome.getTagCompound().getString("OwnerUUID"));
		}
	}
}
