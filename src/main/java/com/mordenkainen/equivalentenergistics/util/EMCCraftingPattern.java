package com.mordenkainen.equivalentenergistics.util;

import appeng.api.AEApi;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.storage.data.IAEItemStack;

import com.google.common.base.Equivalence;
import com.mordenkainen.equivalentenergistics.integration.Integration;
import com.mordenkainen.equivalentenergistics.items.ItemPattern;
import com.mordenkainen.equivalentenergistics.registries.ItemEnum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public final class EMCCraftingPattern implements ICraftingPatternDetails {
	private static Equivalence<ItemStack> eq = new CompItemStack();
	private static Map<Equivalence.Wrapper<ItemStack>, EMCCraftingPattern> patterns = new HashMap<Equivalence.Wrapper<ItemStack>, EMCCraftingPattern>();
	private IAEItemStack[] ingredients = new IAEItemStack[9];
	private IAEItemStack result;
	public float outputEMC;
	public float inputEMC;
	public boolean valid = true;

	private EMCCraftingPattern(final ItemStack craftingResult) {
		buildPattern(craftingResult);
	}

	private void buildPattern(final ItemStack craftingResult)	{
		if (craftingResult.getItem() == ItemEnum.EMCCRYSTAL.getItem()) {
			outputEMC = inputEMC = Integration.emcHandler.getEnergyValue(craftingResult);
			result = AEApi.instance().storage().createItemStack(craftingResult);
			final ItemStack ingredient = new ItemStack(ItemEnum.EMCCRYSTAL.getItem(), 64, craftingResult.getItemDamage() - 1);
			for (int i = 0; i < 9; i++) {
				ingredients[i] = AEApi.instance().storage().createItemStack(ingredient);
			}
		} else {
			calculateContent(craftingResult);
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

	public ItemStack getPattern() {
		final ItemStack pattern = new ItemStack(ItemEnum.EMCPATTERN.getItem());
		((ItemPattern)pattern.getItem()).setTargetItem(pattern, result.getItemStack());
		return pattern;
	}

	public boolean isCraftable() {
		return false;
	}

	public IAEItemStack[] getInputs() {
		return ingredients.clone();
	}

	public IAEItemStack[] getCondensedInputs() {
		final ArrayList<IAEItemStack> cond = new ArrayList<IAEItemStack>();
		for (int index = 0; index < ingredients.length; index++) {
			if (ingredients[index] != null) {
				cond.add(ingredients[index]);
			}
		}
		return (IAEItemStack[])cond.toArray(new IAEItemStack[cond.size()]);
	}

	public IAEItemStack[] getCondensedOutputs() {
		if (result == null) {
			return new IAEItemStack[0];
		}
		return new IAEItemStack[] {result};
	}

	public IAEItemStack[] getOutputs() {
		return getCondensedOutputs();
	}

	public boolean canSubstitute() {
		return false;
	}

	public ItemStack getOutput(final InventoryCrafting inventoryCrafting, final World world) {
		return result.getItemStack();
	}

	public int getPriority() {
		return 0;
	}

	public void setPriority(final int priority) {}

	public boolean isValidItemForSlot(final int slotIndex, final ItemStack repStack, final World world) {
		final IAEItemStack ingStack = ingredients[slotIndex];
		return !(ingStack == null || ingStack.getItem() == null || repStack == null || repStack.getItem() == null || repStack.getItem() != ItemEnum.EMCCRYSTAL.getItem() || repStack.getItemDamage() != ingStack.getItemDamage() || repStack.stackSize < ingStack.getItemStack().stackSize);
	}

	private void calculateContent(final ItemStack craftingResult) {
		if (!Integration.emcHandler.hasEMC(craftingResult) || Integration.emcHandler.getEnergyValue(craftingResult) <= 0.0F) {
			valid = false;
		} else {
			outputEMC = Integration.emcHandler.getEnergyValue(craftingResult);
			inputEMC = 0.0F;
			int numItems = 1;
			int[] crystals = {0, 0, 0};
			if (outputEMC <= Integration.emcHandler.getCrystalEMC()) {
				crystals[0] = 1;
				numItems = (int)Math.min(Integration.emcHandler.getCrystalEMC() / outputEMC, 64.0F);
				outputEMC *= numItems;
				inputEMC = Integration.emcHandler.getCrystalEMC();
			} else {
				final int maxTier = calcStartingTier(outputEMC);
				float remainingEMC = outputEMC;
				for (int i = maxTier; i >= 0; i--) {
					crystals[i] = calcCrystals(remainingEMC, i);
					remainingEMC = getOverflow(outputEMC, crystals);
					if (remainingEMC <= 0.0F) {
						break;
					}
					if (i != 0) {
						crystals[i] -= 1;
						remainingEMC = Math.abs(remainingEMC - Integration.emcHandler.getCrystalEMC(i));
					}
				}
				int pass = 0;
				while (getTotalStacks(crystals) > 9 && pass < 2) {
					if (crystals[pass] > 0) {
						crystals[pass] = 0;
						crystals[pass + 1] += 1;
					}
					pass++;
				}
				if (getTotalStacks(crystals) > 9) {
					valid = false;
					return;
				}
				for (int i = 0; i <= 2; i++) {
					inputEMC += Integration.emcHandler.getCrystalEMC(i) * crystals[i];
				}
			}
			final ItemStack outputStack = craftingResult.copy();
			outputStack.stackSize = numItems;
			result = AEApi.instance().storage().createItemStack(outputStack);

			int tier = 0;
			for (int i = 0; i <= 8 && tier <= 2 && crystals[0] > 0 || crystals[1] > 0 || crystals[2] > 0; i++) {
				while (crystals[tier] <= 0) {
					tier++;
				}
				ingredients[i] = AEApi.instance().storage().createItemStack(new ItemStack(ItemEnum.EMCCRYSTAL.getItem(), Math.min(64, crystals[tier]), tier));
				crystals[tier] -= ingredients[i].getItemStack().stackSize;
			}
		}
	}

	private int calcStartingTier(final float emcValue) {
		if (emcValue > Integration.emcHandler.getCrystalEMC(2)) {
			return 2;
		}
		if (emcValue > Integration.emcHandler.getCrystalEMC(1)) {
			return 1;
		}
		return 0;
	}

	private int calcCrystals(final float emcValue, final int tier) {
		return (int)Math.ceil(emcValue / Integration.emcHandler.getCrystalEMC(tier));
	}

	private float getOverflow(final float targetEMC, final int[] crystals) {
		float crystalEMC = 0.0F;
		for (int i = 0; i <= 2; i++) {
			crystalEMC += Integration.emcHandler.getCrystalEMC(i) * crystals[i];
		}
		return crystalEMC - targetEMC;
	}

	private int getTotalStacks(final int[] crystals) {
		int totalStacks = 0;
		for (int i = 0; i <= 2; i++) {
			totalStacks += getStackCount(crystals[i]);
		}
		return totalStacks;
	}

	private int getStackCount(final int numCrystals) {
		if (numCrystals > 0 && numCrystals < 64) {
			return 1;
		}
		if (numCrystals > 0) {
			return (int)Math.ceil(numCrystals / 64.0D);
		}
		return 0;
	}

	public static void relearnPatterns() {
		for (final Equivalence.Wrapper<ItemStack> wrappedStack : patterns.keySet()) {
			((EMCCraftingPattern)patterns.get(wrappedStack)).buildPattern((ItemStack)wrappedStack.get());
		}
	}
}
