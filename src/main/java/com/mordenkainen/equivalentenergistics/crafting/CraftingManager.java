package com.mordenkainen.equivalentenergistics.crafting;

import com.mordenkainen.equivalentenergistics.config.ConfigManager;
import com.mordenkainen.equivalentenergistics.registries.BlockEnum;
import com.mordenkainen.equivalentenergistics.registries.ItemEnum;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public final class CraftingManager {

    private static final String FRAME = "AMA";
    private static final int NUM_CELLS = 8;
    private static Item aeMaterial;
    private static ItemStack base;
    private static Item aeGlass;
    private static ItemStack dust;

    private CraftingManager() {}

    public static void initRecipes() {
        aeMaterial = GameRegistry.findItem("appliedenergistics2", "item.ItemMultiMaterial");
        base = new ItemStack(GameRegistry.findItem("appliedenergistics2", "tile.BlockSkyStone"), 1, 1);
        aeGlass = GameRegistry.findItem("appliedenergistics2", "tile.BlockQuartzGlass");
        if (ConfigManager.useEE3) {
            dust = new ItemStack(GameRegistry.findItem("EE3", "alchemicalDust"), 1, 3);
        } else {
            dust = new ItemStack(GameRegistry.findItem("ProjectE", "item.pe_covalence_dust"), 1, 2);
        }
        
        if (BlockEnum.EMCCONDENSER.isEnabled()) {
            initCondenserRecipies();
        }
        
        if (BlockEnum.EMCCRAFTER.isEnabled()) {
        	initCrafterRecipies();
        }
        
        if (!ConfigManager.useEE3) {
            GameRegistry.addShapedRecipe(ItemEnum.EMCBOOK.getSizedStack(1), new Object[] { " M ", "MBM", " M ", 'M', dust, 'B', Items.book });
        }

        initCellRecipies();
    }

	private static void initCellRecipies() {
		// Cell + Housing
        for (int i = 0; i < NUM_CELLS; i++) {
            GameRegistry.addShapelessRecipe(ItemEnum.EMCCELL.getDamagedStack(i), ItemEnum.MISCITEM.getDamagedStack(0), ItemEnum.CELLCOMPONENT.getDamagedStack(i));
        }

        // Components
        GameRegistry.addShapedRecipe(ItemEnum.CELLCOMPONENT.getDamagedStack(0), "ABA", "BCB", "ABA", 'A', dust, 'B', new ItemStack(aeMaterial, 1), 'C', new ItemStack(aeMaterial, 1, 22)); // NOPMD
        for (int i = 1; i < 4; i++) {
            GameRegistry.addShapedRecipe(ItemEnum.CELLCOMPONENT.getDamagedStack(i), "ABA", "CDC", "ACA", 'A', dust, 'B', new ItemStack(aeMaterial, 1, 23), 'C', ItemEnum.CELLCOMPONENT.getDamagedStack(i - 1), 'D', new ItemStack(aeGlass, 1));
        }
        for (int i = 4; i < 6; i++) {
            GameRegistry.addShapedRecipe(ItemEnum.CELLCOMPONENT.getDamagedStack(i), "ABA", "CDC", "ACA", 'A', dust, 'B', new ItemStack(aeMaterial, 1, 24), 'C', ItemEnum.CELLCOMPONENT.getDamagedStack(i - 1), 'D', new ItemStack(aeGlass, 1));
        }
        for (int i = 6; i < NUM_CELLS; i++) {
            GameRegistry.addShapedRecipe(ItemEnum.CELLCOMPONENT.getDamagedStack(i), "ABA", "CDC", "ACA", 'A', new ItemStack(Items.nether_star), 'B', new ItemStack(aeMaterial, 1, 24), 'C', ItemEnum.CELLCOMPONENT.getDamagedStack(i - 1), 'D', new ItemStack(aeGlass, 1));
        }

        // Cells
        for (int i = 0; i < NUM_CELLS; i++) {
            GameRegistry.addShapedRecipe(ItemEnum.EMCCELL.getDamagedStack(i), "ABA", "BCB", "DDD", 'A', new ItemStack(aeGlass, 1), 'B', new ItemStack(Items.redstone), 'C', ItemEnum.CELLCOMPONENT.getDamagedStack(i), 'D', new ItemStack(Items.iron_ingot));
        }
	}

	private static void initCrafterRecipies() {
		GameRegistry.addShapedRecipe(new ItemStack(BlockEnum.EMCCRAFTER.getBlock()), new Object[] { FRAME, "MFM", FRAME, 'A', base, 'M', dust, 'F', new ItemStack(aeMaterial, 1, 43) });
	}

	private static void initCondenserRecipies() {
		GameRegistry.addShapedRecipe(new ItemStack(BlockEnum.EMCCONDENSER.getBlock()), new Object[] { FRAME, "MCM", FRAME, 'A', base, 'M', dust, 'C', new ItemStack(aeMaterial, 1, 44) });
    	GameRegistry.addShapedRecipe(new ItemStack(BlockEnum.EMCCONDENSER.getBlock(), 1, 1 ), new Object[] { "AAA", "ADA", "AAA", 'D', new ItemStack(BlockEnum.EMCCONDENSER.getBlock()), 'A', Items.diamond});
    	GameRegistry.addShapedRecipe(new ItemStack(BlockEnum.EMCCONDENSER.getBlock(), 1, 2 ), new Object[] { "BBB", "BDB", "BBB", 'D', new ItemStack(BlockEnum.EMCCONDENSER.getBlock(), 1, 1), 'B', Items.emerald});
    	GameRegistry.addShapedRecipe(new ItemStack(BlockEnum.EMCCONDENSER.getBlock(), 1, 3 ), new Object[] { "CCC", "CEC", "CCC", 'E', new ItemStack(BlockEnum.EMCCONDENSER.getBlock(), 1, 2), 'C', Items.nether_star});
	}

}
