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
		if(!EMCUtils.getInstance().hasEMC(craftingResult) || EMCUtils.getInstance().getEnergyValue(craftingResult) <= 0) {
			valid = false;
		} else {
			float outputEMC = EMCUtils.getInstance().getEnergyValue(craftingResult);
			float inputEMC = 0;
			int numItems = 1;
			int[] crystals = new int[] {0, 0, 0};
			if(outputEMC <= EMCUtils.getInstance().getCrystalEMC()) {
				crystals[0] = 1;
				numItems = (int)Math.min(EMCUtils.getInstance().getCrystalEMC()/outputEMC, 64);
				outputEMC *= numItems;
				inputEMC = EMCUtils.getInstance().getCrystalEMC();
			} else {
				int maxTier = calcStartingTier(outputEMC);
				float remainingEMC = outputEMC;
				for(int i = maxTier; i >= 0; i--) {
					crystals[i] = calcCrystals(remainingEMC, i);
					remainingEMC = getOverflow(outputEMC, crystals);
					if(remainingEMC <= 0) {
						break;
					}
					if(i != 0 ) {
						crystals[i]--;
						remainingEMC = Math.abs(remainingEMC - EMCUtils.getInstance().getCrystalEMC(i));
					}
				}
				int pass = 0;
				while(getTotalStacks(crystals) > 9 && pass < 2) {
					if(crystals[pass] > 0) {
						crystals[pass] = 0;
						crystals[pass + 1]++;
					}
					pass++;
				}
				if(getTotalStacks(crystals) > 9) {
					valid = false;
					return;
				}
				for(int i = 0; i <= 2; i++) {
					inputEMC += EMCUtils.getInstance().getCrystalEMC(i) * crystals[i];
				}
			}
			
			ItemStack outputStack = craftingResult.copy();
			outputStack.stackSize = numItems;
			result = AEApi.instance().storage().createItemStack(outputStack);
			
			int tier = 0;
			for (int i = 0; i <= 8 && tier <= 2 && (crystals[0] > 0 || crystals[1] > 0 || crystals[2] > 0); i++) {
				while (crystals[tier] <= 0) {
					tier++;
				}
				ingredients[i] = AEApi.instance().storage().createItemStack(new ItemStack(EquivalentEnergistics.itemEMCCrystal, Math.min(64, crystals[tier]), tier));
				crystals[tier] -= ingredients[i].getItemStack().stackSize;				
			}
		}
	}
	
	private int calcStartingTier(float emcValue) {
		if(emcValue > EMCUtils.getInstance().getCrystalEMC(2)) {
			return 2;
		}
		if(emcValue > EMCUtils.getInstance().getCrystalEMC(1)) {
			return 1;
		}
		return 0;
	}
	
	private int calcCrystals(float emcValue, int tier) {
		return (int)Math.ceil(emcValue / EMCUtils.getInstance().getCrystalEMC(tier));
	}
	
	private float getOverflow(float targetEMC, int[]crystals) {
		float crystalEMC = 0;
		for(int i = 0; i <= 2; i++) {
			crystalEMC += EMCUtils.getInstance().getCrystalEMC(i) * crystals[i];
		}
		return crystalEMC - targetEMC;
	}
	
	private int getTotalStacks(int[]crystals) {
		int totalStacks = 0;
		for(int i = 0; i <= 2; i++) {
			totalStacks += getStackCount(crystals[i]);
		}
		return totalStacks;
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
