package com.mordenkainen.equivalentenergistics.crafting;

import com.mordenkainen.equivalentenergistics.config.ConfigManager;
import com.mordenkainen.equivalentenergistics.registries.BlockEnum;
import com.mordenkainen.equivalentenergistics.registries.ItemEnum;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.registry.GameRegistry;

public class CraftingManager {
	private CraftingManager() {}
	
	public static void initRecipes() {
		if(ConfigManager.useEE3) {
			GameRegistry.addShapedRecipe(new ItemStack(BlockEnum.EMCCONDENSER.getBlock()), new Object[]{
				"AMA",
				"MCM",
				"AMA",
				'A', GameRegistry.findBlock("EE3", "ashInfusedStone"),
				'M', new ItemStack(GameRegistry.findItem("EE3", "alchemicalDust"), 1, 3),
				'C', new ItemStack(GameRegistry.findItem("appliedenergistics2", "item.ItemMultiMaterial"), 1, 44)
			});
			
			GameRegistry.addShapedRecipe(new ItemStack(BlockEnum.EMCCRAFTER.getBlock()), new Object[]{
				"AMA",
				"MCM",
				"AMA",
				'A', GameRegistry.findBlock("EE3", "ashInfusedStoneSlab"),
				'M', new ItemStack(GameRegistry.findItem("EE3", "alchemicalDust"), 1, 3),
				'C', new ItemStack(GameRegistry.findItem("appliedenergistics2", "item.ItemMultiMaterial"), 1, 43)
			});
		} else {
			GameRegistry.addShapedRecipe(new ItemStack(BlockEnum.EMCCONDENSER.getBlock()), new Object[]{
				"AMA",
				"MCM",
				"AMA",
				'A', Blocks.stone,
				'M', new ItemStack(GameRegistry.findItem("ProjectE", "item.pe_covalence_dust"), 1, 2),
				'C', new ItemStack(GameRegistry.findItem("appliedenergistics2", "item.ItemMultiMaterial"), 1, 44)
			});
			
			GameRegistry.addShapedRecipe(new ItemStack(BlockEnum.EMCCRAFTER.getBlock()), new Object[]{
				"AMA",
				"MCM",
				"AMA",
				'A', Item.getItemFromBlock(Blocks.stone_slab),
				'M', new ItemStack(GameRegistry.findItem("ProjectE", "item.pe_covalence_dust"), 1, 2),
				'C', new ItemStack(GameRegistry.findItem("appliedenergistics2", "item.ItemMultiMaterial"), 1, 43)
			});
			
			GameRegistry.addShapedRecipe(ItemEnum.EMCBOOK.getSizedStack(1), new Object[]{
				" M ",
				"MBM",
				" M ",
				'M', new ItemStack(GameRegistry.findItem("ProjectE", "item.pe_covalence_dust"), 1, 2),
				'B', Items.book
			});
		}
	}
}
