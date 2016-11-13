package com.mordenkainen.equivalentenergistics.proxy;

import com.mordenkainen.equivalentenergistics.crafting.CraftingManager;
import com.mordenkainen.equivalentenergistics.exception.ServerUnmetDependencyException;
import com.mordenkainen.equivalentenergistics.integration.Integration;
import com.mordenkainen.equivalentenergistics.lib.Reference;
import com.mordenkainen.equivalentenergistics.registries.BlockEnum;
import com.mordenkainen.equivalentenergistics.registries.ItemEnum;
import com.mordenkainen.equivalentenergistics.tiles.TileEMCCondenser;
import com.mordenkainen.equivalentenergistics.tiles.TileEMCCrafter;

import cpw.mods.fml.common.registry.GameRegistry;

public class CommonProxy {

    public int crafterRenderer;

    public void preInit() {
        Integration.preInit();
        registerBlocks();
    }

    public void init() {
        if (!Integration.Mods.PROJECTE.isEnabled() && !Integration.Mods.EE3.isEnabled()) {
            unmetDependency();
        }
        Integration.init();
        registerTileEntities();
        registerItems();
        initRenderers();
        CraftingManager.initRecipes();
    }

    public void postInit() {
        Integration.postInit();
    }

    public boolean isClient() {
        return false;
    }

    public boolean isServer() {
        return true;
    }

    public void registerItems() {
        for (final ItemEnum current : ItemEnum.values()) {
            if (current.isEnabled()) {
                GameRegistry.registerItem(current.getItem(), current.getInternalName());
            }
        }
    }

    public void registerBlocks() {
        for (final BlockEnum current : BlockEnum.values()) {
            if (current.isEnabled()) {
                GameRegistry.registerBlock(current.getBlock(), current.getItemBlockClass(), current.getInternalName());
            }
        }
    }

    public void registerTileEntities() {
        if (BlockEnum.EMCCONDENSER.isEnabled()) {
            GameRegistry.registerTileEntity(TileEMCCondenser.class, Reference.MOD_ID + "TileEMCCondenser");
        }
        if (BlockEnum.EMCCRAFTER.isEnabled()) {
            GameRegistry.registerTileEntity(TileEMCCrafter.class, Reference.MOD_ID + "TileEMCCrafter");
        }
    }

    public void initRenderers() {}

    public void unmetDependency() {
        throw new ServerUnmetDependencyException("Equivalent Energistics requires either Equivalent Exchange 3 or ProjectE to be installed and enabled!");
    }

}
