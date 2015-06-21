package com.mordenkainen.equivalentenergistics.crafting;

import com.mordenkainen.equivalentenergistics.EquivalentEnergistics;

import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.registry.GameRegistry;

public class CraftingManager {
	private CraftingManager() {}
	
	public static void initRecipes() {
		GameRegistry.addShapedRecipe(new ItemStack(EquivalentEnergistics.blockEMCCondenser), new Object[]{
			"AMA",
			"MCM",
			"AMA",
			'A', GameRegistry.findBlock("EE3", "ashInfusedStone"), 'M',
			new ItemStack(GameRegistry.findItem("EE3", "alchemicalDust"), 1, 3), 'C',
			new ItemStack(GameRegistry.findItem("appliedenergistics2", "item.ItemMultiMaterial"), 1, 44)
		});
		
		GameRegistry.addShapedRecipe(new ItemStack(EquivalentEnergistics.blockEMCCrafter), new Object[]{
			"AMA",
			"MCM",
			"AMA",
			'A', GameRegistry.findBlock("EE3", "ashInfusedStoneSlab"), 'M',
			new ItemStack(GameRegistry.findItem("EE3", "alchemicalDust"), 1, 3), 'C',
			new ItemStack(GameRegistry.findItem("appliedenergistics2", "item.ItemMultiMaterial"), 1, 43)
		});
	}
}
