package com.mordenkainen.equivalentenergistics.integration.ae2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.google.common.base.Equivalence;
import com.google.common.base.Equivalence.Wrapper;
import com.mordenkainen.equivalentenergistics.integration.Integration;
import com.mordenkainen.equivalentenergistics.items.ItemPattern;
import com.mordenkainen.equivalentenergistics.registries.ItemEnum;
import com.mordenkainen.equivalentenergistics.util.CompItemStack;

import appeng.api.AEApi;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.storage.data.IAEItemStack;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public final class EMCCraftingPattern implements ICraftingPatternDetails {
	private static Equivalence<ItemStack> eq = new CompItemStack();
	private static Map<Equivalence.Wrapper<ItemStack>, EMCCraftingPattern> patterns = new HashMap<Equivalence.Wrapper<ItemStack>, EMCCraftingPattern>();
	private IAEItemStack[] ingredients = new IAEItemStack[9];
	private IAEItemStack[] condInputs;
	private IAEItemStack result;
	private IAEItemStack[] condResult;
	public float outputEMC;
	public float inputEMC;
	public boolean valid = true;
	
	private EMCCraftingPattern(final ItemStack craftingResult) {
		buildPattern(craftingResult);
	}
	
	@Override
	public ItemStack getPattern() {
		final ItemStack pattern = new ItemStack(ItemEnum.EMCPATTERN.getItem());
		((ItemPattern)pattern.getItem()).setTargetItem(pattern, result.getItemStack());
		return pattern;
	}

	@Override
	public boolean isValidItemForSlot(final int slotIndex, final ItemStack itemStack, final World world) {
		if(itemStack == null || ingredients[slotIndex] == null) {
			return false;
		}
		
		return itemStack.isItemEqual(ingredients[slotIndex].getItemStack());
	}

	@Override
	public boolean isCraftable() {
		return false;
	}

	@Override
	public IAEItemStack[] getInputs() {
		return ingredients.clone();
	}

	@Override
	public IAEItemStack[] getCondensedInputs() {
		return condInputs.clone();
	}

	@Override
	public ItemStack getOutput(final InventoryCrafting craftingInv, final World world) {
		return result.getItemStack();
	}
	
	@Override
	public IAEItemStack[] getOutputs() {
		return getCondensedOutputs();
	}
	
	@Override
	public IAEItemStack[] getCondensedOutputs() {
		return condResult.clone();
	}

	@Override
	public boolean canSubstitute() {
		return false;
	}

	@Override
	public int getPriority() {
		return 0;
	}

	@Override
	public void setPriority(final int priority) {}

	private void buildPattern(final ItemStack craftingResult)	{
		valid = true;
		if (craftingResult.getItem() == ItemEnum.EMCCRYSTAL.getItem()) {
			createCrystalPattern(craftingResult.getItemDamage());
		} else {
			createItemPattern(craftingResult);
		}
		buildCondensedLists();
	}

	private void createCrystalPattern(final int tier) {
		outputEMC = inputEMC = Integration.emcHandler.getCrystalEMC(tier + 1);
		result = AEApi.instance().storage().createItemStack(new ItemStack(ItemEnum.EMCCRYSTAL.getItem(), 64, tier));
		ingredients[0] = AEApi.instance().storage().createItemStack(new ItemStack(ItemEnum.EMCCRYSTAL.getItem(), 1, tier + 1));
	}
	
	private void createItemPattern(final ItemStack craftingResult) {
		result = AEApi.instance().storage().createItemStack(craftingResult).setStackSize(1);
		float remainingEMC = outputEMC = Integration.emcHandler.getSingleEnergyValue(craftingResult);
		int slotNum = 0;
		inputEMC = 0;
		for(int x = 4; x >= 0 && remainingEMC > 0; x--) {
			final float crystalEMC = Integration.emcHandler.getCrystalEMC(x);
			final int numCrystals = (int) (remainingEMC / crystalEMC);
			if (numCrystals > 0) {
				ingredients[slotNum] = AEApi.instance().storage().createItemStack(new ItemStack(ItemEnum.EMCCRYSTAL.getItem(), numCrystals, x));
				final float totalEMC = crystalEMC * numCrystals;
				remainingEMC -= totalEMC;
				inputEMC += totalEMC;
				slotNum ++;
			}
		}
		if (remainingEMC > 0) {
			if (ingredients[slotNum - 1].getItemDamage() == 0 && ingredients[slotNum].getStackSize() < Integer.MAX_VALUE) {
				ingredients[slotNum - 1].incStackSize(1);
			} else {
				ingredients[slotNum] = AEApi.instance().storage().createItemStack(new ItemStack(ItemEnum.EMCCRYSTAL.getItem(), 1, 0));
			}
			inputEMC++;
		}
		if (slotNum > 8) {
			valid = false;
		}
	}

	private void buildCondensedLists() {
		condResult = new IAEItemStack[] {result};
		
		final ArrayList<IAEItemStack> cond = new ArrayList<IAEItemStack>();
		for (int index = 0; index < ingredients.length; index++) {
			if (ingredients[index] != null) {
				cond.add(ingredients[index]);
			}
		}
		condInputs = (IAEItemStack[])cond.toArray(new IAEItemStack[cond.size()]);
	}

	public static void relearnPatterns() {
		final Iterator<Wrapper<ItemStack>> iter =  patterns.keySet().iterator();
		
		while (iter.hasNext()) {
			final Wrapper<ItemStack> wrappedStack = iter.next();
			if(Integration.emcHandler.hasEMC(wrappedStack.get())) {
				((EMCCraftingPattern)patterns.get(wrappedStack)).buildPattern(wrappedStack.get());
			} else {
				iter.remove();
			}
		}
	}
	
	public static EMCCraftingPattern get(final ItemStack result) {
		if (patterns.containsKey(eq.wrap(result))) {
			return (EMCCraftingPattern)patterns.get(eq.wrap(result));
		}
		final EMCCraftingPattern newPattern = new EMCCraftingPattern(result);
		patterns.put(eq.wrap(result), newPattern);
		return newPattern;
	}
}
