package com.mordenkainen.equivalentenergistics.util;

import com.mordenkainen.equivalentenergistics.config.ConfigManager;
import com.mordenkainen.equivalentenergistics.registries.ItemEnum;
import com.mordenkainen.equivalentenergistics.tiles.TileEMCCrafter;

import com.pahimar.ee3.api.exchange.EnergyValue;
import com.pahimar.ee3.api.exchange.EnergyValueRegistryProxy;
import com.pahimar.ee3.api.knowledge.TransmutationKnowledgeRegistryProxy;
import com.pahimar.ee3.util.ItemHelper;

import cpw.mods.fml.common.registry.GameRegistry;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import moze_intel.projecte.api.ProjectEAPI;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class EMCUtils {
	private static EMCUtils instance;
	private static Item tomeItem;
	private float[] crystalValues = {0.0F, 0.0F, 0.0F};

	public static EMCUtils getInstance() {
		if (instance == null) {
			instance = new EMCUtils();
		}
		return instance;
	}

	public void relearnCrystals() {
		crystalValues = new float[] {0.0F, 0.0F, 0.0F};
		for (int i = 0; i <= 2; i++) {
			crystalValues[i] = getCrystalEMC(i);
		}
	}

	public boolean hasEMC(ItemStack itemStack) {
		if (ConfigManager.useEE3) {
			return EnergyValueRegistryProxy.hasEnergyValue(itemStack);
		}
		// horrible hack for bug in PE API
		// return ProjectEAPI.getEMCProxy().hasValue(itemStack));
		return ProjectEAPI.getEMCProxy().getValue(itemStack) > 0;
	}

	public float getEnergyValue(ItemStack itemStack) {
		if (ConfigManager.useEE3) {
			EnergyValue val = EnergyValueRegistryProxy.getEnergyValue(itemStack);
			return val == null ? 0.0F : val.getValue();
		}
		return ProjectEAPI.getEMCProxy().getValue(itemStack);
	}

	public float getCrystalEMC() {
		return getCrystalEMC(0);
	}

	public float getCrystalEMC(int tier) {
		if (this.crystalValues[tier] == 0.0F) {
			if (ConfigManager.useEE3) {
				crystalValues[tier] = EnergyValueRegistryProxy.getEnergyValue(new ItemStack(ItemEnum.EMCCRYSTAL.getItem(), 1, tier)).getValue();
			} else {
				crystalValues[tier] = ProjectEAPI.getEMCProxy().getValue(new ItemStack(ItemEnum.EMCCRYSTAL.getItem(), 1, tier));
			}
		}
		return crystalValues[tier];
	}

	public List<ItemStack> getTransmutations(TileEMCCrafter tile) {
		List<ItemStack> transmutations;

		if (ConfigManager.useEE3) {
			transmutations = new ArrayList<ItemStack>(TransmutationKnowledgeRegistryProxy.getPlayerKnownTransmutations(ItemHelper.getOwnerUUID(tile.getCurrentTome())));
		} else {
			transmutations = ProjectEAPI.getTransmutationProxy().getKnowledge(UUID.fromString(tile.getCurrentTome().getTagCompound().getString("OwnerUUID")));
		}
		if (transmutations == null) {
			transmutations = new ArrayList<ItemStack>();
		}
		Iterator<ItemStack> iter = transmutations.iterator();
		while (iter.hasNext()) {
			ItemStack currentItem = (ItemStack)iter.next();
			if ((currentItem == null) || (currentItem.getItem() == ItemEnum.EMCCRYSTAL.getItem())) {
				iter.remove();
			}
		}
		return transmutations;
	}

	public boolean isValidTome(ItemStack itemStack)	{
		if (ConfigManager.useEE3) {
			if (tomeItem == null) { 
				tomeItem = GameRegistry.findItem("EE3", "alchemicalTome");
			}
			return (itemStack != null) && (itemStack.getItem() == tomeItem) && (ItemHelper.hasOwnerUUID(itemStack));
		}
		return (itemStack != null) && (itemStack.getItem() == ItemEnum.EMCBOOK.getItem()) && (itemStack.hasTagCompound()) && (itemStack.getTagCompound().hasKey("OwnerUUID"));
	}

	public void setCrystalEMC(float emc) {
		if (ConfigManager.useEE3) {
			EnergyValueRegistryProxy.addPreAssignedEnergyValue(new ItemStack(ItemEnum.EMCCRYSTAL.getItem(), 1, 0), emc);
			EnergyValueRegistryProxy.addPreAssignedEnergyValue(new ItemStack(ItemEnum.EMCCRYSTAL.getItem(), 1, 1), emc * 576.0F);
			EnergyValueRegistryProxy.addPreAssignedEnergyValue(new ItemStack(ItemEnum.EMCCRYSTAL.getItem(), 1, 2), (float)(emc * Math.pow(576.0D, 2.0D)));
		} else {
			ProjectEAPI.getEMCProxy().registerCustomEMC(new ItemStack(ItemEnum.EMCCRYSTAL.getItem(), 1, 0), (int)emc);
			ProjectEAPI.getEMCProxy().registerCustomEMC(new ItemStack(ItemEnum.EMCCRYSTAL.getItem(), 1, 1), (int)emc * 576);
			ProjectEAPI.getEMCProxy().registerCustomEMC(new ItemStack(ItemEnum.EMCCRYSTAL.getItem(), 1, 2), (int)(emc * Math.pow(576.0D, 2.0D)));
		}
	}

	public UUID getTomeUUID(ItemStack currentTome) {
		if (ConfigManager.useEE3) {
			return ItemHelper.getOwnerUUID(currentTome);
		}
		return UUID.fromString(currentTome.getTagCompound().getString("OwnerUUID"));
	}

	public String getTomeOwner(ItemStack currentTome) {
		if (ConfigManager.useEE3) {
			return ItemHelper.getOwnerName(currentTome);
		}
		return currentTome.getTagCompound().getString("Owner");
	}

	public float getSingleEnergyValue(ItemStack stack) {
		ItemStack singleStack = stack.copy();
		singleStack.stackSize = 1;
		return getEnergyValue(singleStack);
	}
}