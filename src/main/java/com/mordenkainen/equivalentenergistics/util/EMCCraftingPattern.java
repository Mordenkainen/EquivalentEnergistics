package com.mordenkainen.equivalentenergistics.util;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import appeng.api.AEApi;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.storage.data.IAEItemStack;

import com.mordenkainen.equivalentenergistics.EquivalentEnergistics;

public class EMCCraftingPattern extends EECraftingPattern {
	public boolean valid = true;
	
	public EMCCraftingPattern(final ItemStack craftingResult) {
		calculateContent(craftingResult);
	}

	private void calculateContent(ItemStack craftingResult) {
		if(!EMCUtils.getInstance().hasEMC(craftingResult)) {
			valid = false;
		} else {
			float outputEMC = EMCUtils.getInstance().getEnergyValue(craftingResult);
			float crystalEMC = EMCUtils.getInstance().getCrystalEMC();
			int tier0CrystalCount, tier1CrystalCount, tier2CrystalCount, itemCount;
			tier1CrystalCount = tier2CrystalCount = 0;
			if(outputEMC <= crystalEMC) {
				tier0CrystalCount = 1;
				itemCount = (int)Math.min(crystalEMC/outputEMC, 64);
			} else {
				itemCount = 1;
				tier0CrystalCount = (int)Math.ceil(outputEMC/crystalEMC);
			}
			this.outputEMC = outputEMC * itemCount;
			ItemStack outputStack = craftingResult.copy();
			outputStack.stackSize = itemCount;
			result = AEApi.instance().storage().createItemStack(outputStack);
			
			if(tier0CrystalCount >= Math.pow(576, 2)) {
				int numCrystals = (int)Math.floor(tier0CrystalCount/Math.pow(576, 2));
				tier2CrystalCount = numCrystals;
				tier0CrystalCount -= numCrystals * Math.pow(576, 2);
			}
			if(tier0CrystalCount >= 576) {
				int numCrystals = (int)Math.floor(tier0CrystalCount/576);
				tier1CrystalCount = numCrystals;
				tier0CrystalCount -= numCrystals * 576;
			}
			if(getStackCount(tier0CrystalCount) + getStackCount(tier1CrystalCount) + getStackCount(tier2CrystalCount) > 9) {
				tier1CrystalCount++;
				tier0CrystalCount=0;
			}
			inputEMC = crystalEMC * tier0CrystalCount + EMCUtils.getInstance().getCrystalEMC(1) * tier1CrystalCount + EMCUtils.getInstance().getCrystalEMC(2) * tier2CrystalCount;
				
			int lastItem = 0;
			while(tier0CrystalCount > 0) {
				ingredients[lastItem++] = AEApi.instance().storage().createItemStack(new ItemStack(EquivalentEnergistics.itemEMCCrystal, Math.min(64, tier0CrystalCount), 0));
				tier0CrystalCount -= Math.min(64, tier0CrystalCount);
			}
			while(tier1CrystalCount > 0) {
				ingredients[lastItem++] = AEApi.instance().storage().createItemStack(new ItemStack(EquivalentEnergistics.itemEMCCrystal, Math.min(64, tier1CrystalCount), 1));
				tier1CrystalCount -= Math.min(64, tier1CrystalCount);
			}
			while(tier2CrystalCount > 0) {
				ingredients[lastItem++] = AEApi.instance().storage().createItemStack(new ItemStack(EquivalentEnergistics.itemEMCCrystal, Math.min(64, tier2CrystalCount), 2));
				tier2CrystalCount -= Math.min(64, tier2CrystalCount);
			}
		}
	}

	private int getStackCount(int numCrystals) {
		if(numCrystals > 0 && numCrystals < 64) {
			return 1;
		} else if (numCrystals > 0) {
			return (int)Math.ceil(numCrystals/64D);
		}
		return 0;
	}
}
