package com.mordenkainen.equivalentenergistics.core.crafting;

import com.mordenkainen.equivalentenergistics.blocks.ModBlocks;
import com.mordenkainen.equivalentenergistics.items.ModItems;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;

public final class CraftingManager {

    private static final int NUM_CELLS = 8;
    public static Item aeMaterial;
    private static ItemStack base;
    private static Item aeGlass;
    private static ItemStack dust;
    private static String quartzCrystals = "EqECertusQuartz";

    private CraftingManager() {}

    public static void initRecipes() {
        aeMaterial = Item.REGISTRY.getObject(new ResourceLocation("appliedenergistics2", "material"));
        base = new ItemStack(Item.REGISTRY.getObject(new ResourceLocation("appliedenergistics2", "smooth_sky_stone_block")), 1);
        aeGlass = Item.REGISTRY.getObject(new ResourceLocation("appliedenergistics2", "quartz_glass"));
        dust = new ItemStack(Item.REGISTRY.getObject(new ResourceLocation("ProjectE", "item.pe_covalence_dust")), 1, 2);
        
        OreDictionary.registerOre(quartzCrystals, new ItemStack(aeMaterial, 1, 0));  //NOPMD
        OreDictionary.registerOre(quartzCrystals, new ItemStack(aeMaterial, 1, 1));
        OreDictionary.registerOre(quartzCrystals, new ItemStack(aeMaterial, 1, 10));

        initCondenserRecipies();
        initCrafterRecipies();
        GameRegistry.addShapedRecipe(new ItemStack(ModItems.EMC_BOOK, 1), new Object[] { " D ", "DBD", " D ", 'D', dust, 'B', Items.BOOK });

        initCellRecipies();
    }

    private static void initCellRecipies() {
        // Cell + Housing
        for (int i = 0; i < NUM_CELLS; i++) {
            GameRegistry.addShapelessRecipe(new ItemStack(ModItems.CRYSTAL, 1, i), new ItemStack(ModItems.MISC, 1, 0), new ItemStack(ModItems.COMPONENT, 1, i));
        }

        // Components
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.COMPONENT, 1, 0), "DCD", "CLC", "DCD", 'D', dust, 'C', quartzCrystals, 'L', new ItemStack(aeMaterial, 1, 22))); // NOPMD
        for (int i = 1; i < 4; i++) {
            GameRegistry.addShapedRecipe(new ItemStack(ModItems.COMPONENT, 1, i), "DCD", "SGS", "DSD", 'D', dust, 'C', new ItemStack(aeMaterial, 1, 23), 'S', new ItemStack(ModItems.COMPONENT, 1, i - 1), 'G', new ItemStack(aeGlass, 1));
        }
        for (int i = 4; i < 6; i++) {
            GameRegistry.addShapedRecipe(new ItemStack(ModItems.COMPONENT, 1, i), "DED", "SGS", "DSD", 'D', dust, 'E', new ItemStack(aeMaterial, 1, 24), 'S', new ItemStack(ModItems.COMPONENT, 1, i - 1), 'G', new ItemStack(aeGlass, 1));
        }
        for (int i = 6; i < NUM_CELLS; i++) {
            GameRegistry.addShapedRecipe(new ItemStack(ModItems.COMPONENT, 1, i), "NEN", "SGS", "NSN", 'N', new ItemStack(Items.NETHER_STAR), 'E', new ItemStack(aeMaterial, 1, 24), 'S', new ItemStack(ModItems.COMPONENT, 1, i - 1), 'G', new ItemStack(aeGlass, 1));
        }

        // Cells
        for (int i = 0; i < NUM_CELLS; i++) {
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.CELL, 1, i), "GRG", "RSR", "III", 'G', new ItemStack(aeGlass, 1), 'R', "dustRedstone", 'S', new ItemStack(ModItems.COMPONENT, 1, i), 'I', "ingotIron"));
        }
    }

    private static void initCrafterRecipies() {
        GameRegistry.addShapedRecipe(new ItemStack(ModBlocks.CRAFTER, 1, 0), new Object[] { "CDC", "DFD", "CDC", 'C', base, 'D', dust, 'F', new ItemStack(aeMaterial, 1, 43) });
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.CRAFTER, 1, 1), new Object[] { "GGG", "GRG", "GGG", 'R', new ItemStack(ModBlocks.CRAFTER, 1, 0), 'G', "gemDiamond" }));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.CRAFTER, 1, 2), new Object[] { "MMM", "MRM", "MMM", 'R', new ItemStack(ModBlocks.CRAFTER, 1, 1), 'M', "gemEmerald" }));
        GameRegistry.addShapedRecipe(new ItemStack(ModBlocks.CRAFTER, 1, 3), new Object[] { "SSS", "SRS", "SSS", 'R', new ItemStack(ModBlocks.CRAFTER, 1, 2), 'S', Items.NETHER_STAR });
    }

    private static void initCondenserRecipies() {
        GameRegistry.addShapedRecipe(new ItemStack(ModBlocks.CONDENSER, 1, 0), new Object[] { "BDB", "BAB", "BDB", 'B', base, 'D', dust, 'A', new ItemStack(aeMaterial, 1, 44) });
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.CONDENSER, 1, 1), new Object[] { "DDD", "DCD", "DDD", 'C', new ItemStack(ModBlocks.CONDENSER, 1, 0), 'D', "gemDiamond" }));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.CONDENSER, 1, 2), new Object[] { "EEE", "ECE", "EEE", 'C', new ItemStack(ModBlocks.CONDENSER, 1, 1), 'E', "gemEmerald" }));
        GameRegistry.addShapedRecipe(new ItemStack(ModBlocks.CONDENSER, 1, 3), new Object[] { "NNN", "NCN", "NNN", 'C', new ItemStack(ModBlocks.CONDENSER, 1, 2), 'N', Items.NETHER_STAR });
    }

}
