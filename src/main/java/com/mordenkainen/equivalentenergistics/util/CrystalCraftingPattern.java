package com.mordenkainen.equivalentenergistics.util;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import appeng.api.AEApi;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.storage.data.IAEItemStack;

import com.mordenkainen.equivalentenergistics.EquivalentEnergistics;

public class CrystalCraftingPattern extends EECraftingPattern {
	public CrystalCraftingPattern(final int tier) {
		outputEMC = inputEMC = EMCUtils.getInstance().getEnergyValue(new ItemStack(EquivalentEnergistics.itemEMCCrystal, 1, tier));
		result = AEApi.instance().storage().createItemStack(new ItemStack(EquivalentEnergistics.itemEMCCrystal, 1, tier));
		for(int i = 0; i < 9; i++) {
			ingredients[i] = AEApi.instance().storage().createItemStack(new ItemStack(EquivalentEnergistics.itemEMCCrystal, 64, tier - 1));
		}
	}
}
