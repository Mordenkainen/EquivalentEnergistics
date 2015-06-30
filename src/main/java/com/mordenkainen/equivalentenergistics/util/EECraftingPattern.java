package com.mordenkainen.equivalentenergistics.util;

import java.util.ArrayList;

import com.mordenkainen.equivalentenergistics.EquivalentEnergistics;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.storage.data.IAEItemStack;

public abstract class EECraftingPattern implements ICraftingPatternDetails {

	protected IAEItemStack[] ingredients = new IAEItemStack[9];
	protected IAEItemStack result;
	public float outputEMC;
	public float inputEMC;

	public EECraftingPattern() {
		super();
	}

	@Override
	public ItemStack getPattern() {
		return null;
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

	@Override
	public boolean isValidItemForSlot(final int slotIndex, final ItemStack repStack, final World world) {
		IAEItemStack ingStack = ingredients[slotIndex];

		if(ingStack == null || ingStack.getItem() == null || repStack == null || repStack.getItem() == null || 
				repStack.getItem() != EquivalentEnergistics.itemEMCCrystal || repStack.getItemDamage() != ingStack.getItemDamage() ||
				repStack.stackSize < ingStack.getItemStack().stackSize) {
			return false;
		}
		
		return true;
	}
}