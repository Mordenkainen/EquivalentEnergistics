package com.mordenkainen.equivalentenergistics.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.mordenkainen.equivalentenergistics.EquivalentEnergistics;
import com.mordenkainen.equivalentenergistics.config.ConfigManager;
import com.mordenkainen.equivalentenergistics.tiles.TileEMCCrafter;
import com.pahimar.ee3.api.exchange.EnergyValueRegistryProxy;
import com.pahimar.ee3.api.knowledge.AbilityRegistryProxy;
import com.pahimar.ee3.api.knowledge.TransmutationKnowledgeRegistryProxy;
import com.pahimar.ee3.util.ItemHelper;

import cpw.mods.fml.common.registry.GameRegistry;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.emc.EMCMapper;
import moze_intel.projecte.emc.SimpleStack;
import moze_intel.projecte.utils.EMCHelper;
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
			return EMCHelper.doesItemHaveEmc(itemStack);
		}
	}

	public float getEnergyValue(ItemStack itemStack) {
		if(ConfigManager.useEE3) {
			return EnergyValueRegistryProxy.getEnergyValue(itemStack).getValue();
		} else {
			return EMCHelper.getEmcValue(itemStack);
		}
	}

	public float getCrystalEMC() {
		return getCrystalEMC(0);
	}
	
	public float getCrystalEMC(int tier) {
		if(ConfigManager.useEE3) {
			return EnergyValueRegistryProxy.getEnergyValue(new ItemStack(EquivalentEnergistics.itemEMCCrystal, 1, tier)).getValue();
		} else {
			return EMCHelper.getEmcValue(new ItemStack(EquivalentEnergistics.itemEMCCrystal, 1, tier));
		}
	}

	public ArrayList<ItemStack> getTransmutations(TileEMCCrafter tile) {
		List<ItemStack> tmpTransmutations = new ArrayList<ItemStack>();
		ArrayList<ItemStack> transmutations = new ArrayList<ItemStack>();
		if(ConfigManager.useEE3) {
			tmpTransmutations.addAll(TransmutationKnowledgeRegistryProxy.getPlayerKnownTransmutations(ItemHelper.getOwnerUUID(tile.getCurrentTome())));
		} else {
			tmpTransmutations.addAll(TransmutationNbt.getPlayerKnowledge(tile.getCurrentTome().getTagCompound().getString("OwnerUUID")));
		}
		
		if(tmpTransmutations != null) {
			for(ItemStack currentItem : tmpTransmutations) {
				if(currentItem.getItem() != EquivalentEnergistics.itemEMCCrystal) {
					transmutations.add(currentItem);
				}
			}
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
		} else {
			ProjectEAPI.registerCustomEMC(new ItemStack(EquivalentEnergistics.itemEMCCrystal, 1, 0), (int)emc);
			ProjectEAPI.registerCustomEMC(new ItemStack(EquivalentEnergistics.itemEMCCrystal, 1, 1), (int)emc * 576);
			ProjectEAPI.registerCustomEMC(new ItemStack(EquivalentEnergistics.itemEMCCrystal, 1, 2), (int)(emc * Math.pow(576, 2)));
		}
	}

	public String getTomeOwner(ItemStack currentTome) {
		if(ConfigManager.useEE3) {
			return ItemHelper.getOwnerName(currentTome);
		} else {
			return currentTome.getTagCompound().getString("Owner");
		}
	}
}
