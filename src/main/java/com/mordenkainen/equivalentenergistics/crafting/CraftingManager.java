package com.mordenkainen.equivalentenergistics.crafting;

import com.mordenkainen.equivalentenergistics.config.ConfigManager;
import com.mordenkainen.equivalentenergistics.registries.BlockEnum;
import com.mordenkainen.equivalentenergistics.registries.ItemEnum;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public final class CraftingManager {

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
            GameRegistry.addShapedRecipe(ItemEnum.EMCBOOK.getSizedStack(1), new Object[] { " D ", "DBD", " D ", 'D', dust, 'B', Items.book });
        }

        initCellRecipies();
    }

    private static void initCellRecipies() {
        // Cell + Housing
        for (int i = 0; i < NUM_CELLS; i++) {
            GameRegistry.addShapelessRecipe(ItemEnum.EMCCELL.getDamagedStack(i), ItemEnum.MISCITEM.getDamagedStack(0), ItemEnum.CELLCOMPONENT.getDamagedStack(i));
        }

        // Components
        GameRegistry.addShapedRecipe(ItemEnum.CELLCOMPONENT.getDamagedStack(0), "DCD", "CLC", "DCD", 'D', dust, 'C', new ItemStack(aeMaterial, 1), 'L', new ItemStack(aeMaterial, 1, 22)); // NOPMD
        for (int i = 1; i < 4; i++) {
            GameRegistry.addShapedRecipe(ItemEnum.CELLCOMPONENT.getDamagedStack(i), "DCD", "SGS", "DSD", 'D', dust, 'C', new ItemStack(aeMaterial, 1, 23), 'S',
                    ItemEnum.CELLCOMPONENT.getDamagedStack(i - 1), 'G', new ItemStack(aeGlass, 1));
        }
        for (int i = 4; i < 6; i++) {
            GameRegistry.addShapedRecipe(ItemEnum.CELLCOMPONENT.getDamagedStack(i), "DED", "SGS", "DSD", 'D', dust, 'E', new ItemStack(aeMaterial, 1, 24), 'S',
                    ItemEnum.CELLCOMPONENT.getDamagedStack(i - 1), 'G', new ItemStack(aeGlass, 1));
        }
        for (int i = 6; i < NUM_CELLS; i++) {
            GameRegistry.addShapedRecipe(ItemEnum.CELLCOMPONENT.getDamagedStack(i), "NEN", "SGS", "NSN", 'N', new ItemStack(Items.nether_star), 'E', new ItemStack(aeMaterial, 1, 24), 'S',
                    ItemEnum.CELLCOMPONENT.getDamagedStack(i - 1), 'G', new ItemStack(aeGlass, 1));
        }

        // Cells
        for (int i = 0; i < NUM_CELLS; i++) {
            GameRegistry.addShapedRecipe(ItemEnum.EMCCELL.getDamagedStack(i), "GRG", "RSR", "III", 'G', new ItemStack(aeGlass, 1), 'R', new ItemStack(Items.redstone), 'S',
                    ItemEnum.CELLCOMPONENT.getDamagedStack(i), 'I', new ItemStack(Items.iron_ingot));
        }
    }

    private static void initCrafterRecipies() {
        GameRegistry.addShapedRecipe(new ItemStack(BlockEnum.EMCCRAFTER.getBlock()), new Object[] { "CDC", "DFD", "CDC", 'C', base, 'D', dust, 'F', new ItemStack(aeMaterial, 1, 43) });
    }

    private static void initCondenserRecipies() {
        GameRegistry.addShapedRecipe(new ItemStack(BlockEnum.EMCCONDENSER.getBlock()), new Object[] { "BDB", "BAB", "BDB", 'B', base, 'D', dust, 'A', new ItemStack(aeMaterial, 1, 44) });
        GameRegistry.addShapedRecipe(new ItemStack(BlockEnum.EMCCONDENSER.getBlock(), 1, 1),
                new Object[] { "DDD", "DCD", "DDD", 'C', new ItemStack(BlockEnum.EMCCONDENSER.getBlock()), 'D', Items.diamond });
        GameRegistry.addShapedRecipe(new ItemStack(BlockEnum.EMCCONDENSER.getBlock(), 1, 2),
                new Object[] { "EEE", "ECE", "EEE", 'C', new ItemStack(BlockEnum.EMCCONDENSER.getBlock(), 1, 1), 'E', Items.emerald });
        GameRegistry.addShapedRecipe(new ItemStack(BlockEnum.EMCCONDENSER.getBlock(), 1, 3),
                new Object[] { "NNN", "NCN", "NNN", 'C', new ItemStack(BlockEnum.EMCCONDENSER.getBlock(), 1, 2), 'N', Items.nether_star });
    }

}
