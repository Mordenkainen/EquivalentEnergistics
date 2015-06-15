package com.mordenkainen.equivalentenergistics.util;

import java.util.ArrayList;

import com.mordenkainen.equivalentenergistics.EquivalentEnergistics;
import com.pahimar.ee3.api.exchange.EnergyValueRegistryProxy;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.AEApi;

public class EMCCraftingPattern implements ICraftingPatternDetails {
	private ItemStack book;
	private static final int GRID_SIZE = 9;
	public IAEItemStack[] ingredients = new IAEItemStack[EMCCraftingPattern.GRID_SIZE];
	public IAEItemStack result;
	public float outputEMC, inputEMC;
	
	public EMCCraftingPattern(final ItemStack book, final ItemStack craftingResult) {
		super();
		this.book = book;
		calculateContent(craftingResult);
	}

	private void calculateContent(ItemStack craftingResult) {
		float outputEMC = EnergyValueRegistryProxy.getEnergyValue(craftingResult).getValue();
		float crystalEMC = EnergyValueRegistryProxy.getEnergyValue(EquivalentEnergistics.EMCCrystal).getValue();
		int crystalCount = 0;
		int itemCount = 0;
		if(outputEMC <= crystalEMC) {
			crystalCount = 1;
			itemCount = (int)Math.min(crystalEMC/outputEMC, 64);
		} else {
			itemCount = 1;
			crystalCount = (int)(outputEMC/crystalEMC);
			if(outputEMC%crystalEMC > 0) {
				crystalCount++;
			}
		}
		inputEMC = crystalEMC * crystalCount;
		this.outputEMC = outputEMC * itemCount;
		result = AEApi.instance().storage().createItemStack(new ItemStack(craftingResult.getItem(), itemCount, craftingResult.getItemDamage()));
		int lastItem = 0;
		while(crystalCount > 64) {
			ingredients[lastItem] = AEApi.instance().storage().createItemStack(new ItemStack(EquivalentEnergistics.EMCCrystal, 64));
			lastItem++;
			crystalCount -= 64;
		}
		ingredients[lastItem] = AEApi.instance().storage().createItemStack(new ItemStack(EquivalentEnergistics.EMCCrystal, crystalCount));
	}

	@Override
	public ItemStack getPattern() {
		return book;
	}

	@Override
	public boolean isValidItemForSlot(final int slotIndex, final ItemStack repStack, final World world) {
		IAEItemStack ingStack = ingredients[slotIndex];

		if((ingStack == null) || (ingStack.getItem() == null) || (repStack == null) || (repStack.getItem() == null)) {
			return false;
		}
		if(repStack.getItem() == EquivalentEnergistics.EMCCrystal) {
			return true;
		}
		
		if(ItemStack.areItemStacksEqual(ingStack.getItemStack(), repStack)) {
			return true;
		}
		
		return false;
	}

	@Override
	public boolean isCraftable() {
		return false;
	}

	@Override
	public IAEItemStack[] getInputs() {
		return ingredients;
	}

	@Override
	public IAEItemStack[] getCondensedInputs() {
		ArrayList<IAEItemStack> cond = new ArrayList<IAEItemStack>();

		for(int index = 0; index < this.ingredients.length; index++) {
			if(ingredients[index] != null) {
				cond.add(ingredients[index]);
			}
		}

		return cond.toArray(new IAEItemStack[cond.size()]);
	}

	@Override
	public IAEItemStack[] getCondensedOutputs() {
		if(result == null) {
			return new IAEItemStack[0];
		}

		return new IAEItemStack[] {result};
	}

	@Override
	public IAEItemStack[] getOutputs() {
		return new IAEItemStack[] {result};
	}

	@Override
	public boolean canSubstitute() {
		return false;
	}

	@Override
	public ItemStack getOutput(InventoryCrafting paramInventoryCrafting, World paramWorld) {
		return result.getItemStack();
	}

	@Override
	public int getPriority() {
		return 0;
	}

	@Override
	public void setPriority(int paramInt) {}

}
