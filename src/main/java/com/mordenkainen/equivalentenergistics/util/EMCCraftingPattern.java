package com.mordenkainen.equivalentenergistics.util;

import java.util.ArrayList;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import appeng.api.AEApi;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.storage.data.IAEItemStack;
import com.mordenkainen.equivalentenergistics.EquivalentEnergistics;
import com.pahimar.ee3.api.exchange.EnergyValueRegistryProxy;

public class EMCCraftingPattern implements ICraftingPatternDetails {
	private ItemStack book;
	private IAEItemStack[] ingredients;
	private IAEItemStack result;
	public float outputEMC, inputEMC;
	
	public EMCCraftingPattern(final ItemStack book, final ItemStack craftingResult) {
		this.book = book;
		calculateContent(craftingResult);
	}

	private void calculateContent(ItemStack craftingResult) {
		float outputEMC = EnergyValueRegistryProxy.getEnergyValue(craftingResult).getValue();
		float crystalEMC = EnergyValueRegistryProxy.getEnergyValue(EquivalentEnergistics.EMCCrystal).getValue();
		int crystalCount, itemCount;
		if(outputEMC <= crystalEMC) {
			crystalCount = 1;
			itemCount = (int)Math.min(crystalEMC/outputEMC, 64);
		} else {
			itemCount = 1;
			crystalCount = (int)Math.ceil(outputEMC/crystalEMC);
		}
		inputEMC = crystalEMC * crystalCount;
		this.outputEMC = outputEMC * itemCount;
		result = AEApi.instance().storage().createItemStack(new ItemStack(craftingResult.getItem(), itemCount, craftingResult.getItemDamage()));
		ingredients = new IAEItemStack[(int)Math.ceil(crystalCount/64.0D)];
		int lastItem = 0;
		while(crystalCount > 64) {
			ingredients[lastItem++] = AEApi.instance().storage().createItemStack(new ItemStack(EquivalentEnergistics.EMCCrystal, 64));
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

		if((ingStack == null) || (ingStack.getItem() == null) || (repStack == null) || (repStack.getItem() == null) || 
				repStack.getItem() != EquivalentEnergistics.EMCCrystal || repStack.stackSize < ingStack.getItemStack().stackSize) {
			return false;
		}
		
		return true;
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
