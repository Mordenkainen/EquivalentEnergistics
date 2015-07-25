package com.mordenkainen.equivalentenergistics.util;

import java.util.ArrayList;

import com.mordenkainen.equivalentenergistics.EquivalentEnergistics;
import com.mordenkainen.equivalentenergistics.items.ItemPattern;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.storage.data.IAEItemStack;

public abstract class EECraftingPattern implements ICraftingPatternDetails {

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(inputEMC);
		result = prime * result + Float.floatToIntBits(outputEMC);
		result = getHashForStack(prime, result, this.result);
		for(IAEItemStack stack : ingredients) {
			result = getHashForStack(prime, result, stack);
		}
		return result;
	}

	private int getHashForStack(int prime, int curResult, IAEItemStack stack) {
		int result;
		if(stack == null) {
			result = prime * curResult;
		} else {
			ItemStack theStack = stack.getItemStack();
			result = prime * curResult + Item.getIdFromItem(theStack.getItem());
			result = prime * result + theStack.getItemDamage();
			result = prime * result + (theStack.hasTagCompound() ? theStack.stackTagCompound.hashCode() : 0);
		}
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		EECraftingPattern other = (EECraftingPattern) obj;
		if (!ingredientsEqual(ingredients, other.ingredients)) {
			return false;
		}
		if (inputEMC != other.inputEMC) {
			return false;
		}
		if (outputEMC != other.outputEMC) {
			return false;
		}
		if ((result == null && other.result != null) || (other.result == null && result != null)) {
			return false;
		} else if (!compareStacks(result, other.result)) {
			return false;
		}
		return true;
	}

	private boolean ingredientsEqual(IAEItemStack[] ingredients1, IAEItemStack[] ingredients2) {
		if((ingredients1 == null && ingredients2 != null) || (ingredients2 == null && ingredients1 != null)) {
			return false;
		}
		if(ingredients1 == null && ingredients2 == null) {
			return true;
		}
		if(ingredients1.length != ingredients2.length) {
			return false;
		}
		for(int i = 0; i < ingredients1.length; i++) {
			if(!compareStacks(ingredients1[i], ingredients2[i])) {
				return false;
			}
		}
		return true;
	}

	private boolean compareStacks(IAEItemStack iaeItemStack, IAEItemStack iaeItemStack2) {
		if((iaeItemStack == null && iaeItemStack2 != null) || (iaeItemStack2 == null && iaeItemStack != null)) {
			return false;
		}
		if(iaeItemStack == null && iaeItemStack2 == null) {
			return true;
		}
		ItemStack stack1 = iaeItemStack.getItemStack();
		ItemStack stack2 = iaeItemStack2.getItemStack();
		if(!ItemStack.areItemStacksEqual(stack1, stack2)) {
			return false;
		}
		return true;
	}

	protected IAEItemStack[] ingredients = new IAEItemStack[9];
	protected IAEItemStack result;
	public float outputEMC;
	public float inputEMC;

	public EECraftingPattern() {
		super();
	}

	@Override
	public ItemStack getPattern() {
		ItemStack pattern = new ItemStack(EquivalentEnergistics.itemPattern);
		((ItemPattern)pattern.getItem()).setTargetItem(pattern, result.getItemStack());
		return pattern;
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