package com.mordenkainen.equivalentenergistics.crafting;

import com.mordenkainen.equivalentenergistics.config.ConfigManager;
import com.mordenkainen.equivalentenergistics.registries.BlockEnum;
import com.mordenkainen.equivalentenergistics.registries.ItemEnum;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.registry.GameRegistry;

public final class CraftingManager {
	private CraftingManager() {}

	public static void initRecipes() {
		final Item aeMaterial = GameRegistry.findItem("appliedenergistics2", "item.ItemMultiMaterial");
		final ItemStack base = new ItemStack(GameRegistry.findItem("appliedenergistics2", "tile.BlockSkyStone"), 1, 1);
		final String frame = "AMA";
		
		ItemStack dust;
		if(ConfigManager.useEE3) {
			dust = new ItemStack(GameRegistry.findItem("EE3", "alchemicalDust"), 1, 3);
		} else {
			dust = new ItemStack(GameRegistry.findItem("ProjectE", "item.pe_covalence_dust"), 1, 2);
		}
		
		GameRegistry.addShapedRecipe(new ItemStack(BlockEnum.EMCCONDENSER.getBlock()), new Object[]{
			frame,
			"MCM",
			frame,
			'A', base,
			'M', dust,
			'C', new ItemStack(aeMaterial, 1, 44)
		});
		
		GameRegistry.addShapedRecipe(new ItemStack(BlockEnum.EMCCRAFTER.getBlock()), new Object[]{
			frame,
			"MFM",
			frame,
			'A', base,
			'M', dust,
			'F', new ItemStack(aeMaterial, 1, 43)
		});
		
		if(!ConfigManager.useEE3) {
			GameRegistry.addShapedRecipe(ItemEnum.EMCBOOK.getSizedStack(1), new Object[]{
				" M ",
				"MBM",
				" M ",
				'M', dust,
				'B', Items.book
			});
		}
	}
	
}
