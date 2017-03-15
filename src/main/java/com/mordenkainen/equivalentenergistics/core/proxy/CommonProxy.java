package com.mordenkainen.equivalentenergistics.core.proxy;

import com.mordenkainen.equivalentenergistics.blocks.BlockEnum;
import com.mordenkainen.equivalentenergistics.blocks.condenser.tiles.TileEMCCondenser;
import com.mordenkainen.equivalentenergistics.blocks.condenser.tiles.TileEMCCondenserAdv;
import com.mordenkainen.equivalentenergistics.blocks.condenser.tiles.TileEMCCondenserExt;
import com.mordenkainen.equivalentenergistics.blocks.condenser.tiles.TileEMCCondenserUlt;
import com.mordenkainen.equivalentenergistics.blocks.crafter.tiles.TileEMCCrafter;
import com.mordenkainen.equivalentenergistics.core.Reference;
import com.mordenkainen.equivalentenergistics.core.crafting.CraftingManager;
import com.mordenkainen.equivalentenergistics.core.exceptions.ServerUnmetDependencyException;
import com.mordenkainen.equivalentenergistics.integration.Integration;
import com.mordenkainen.equivalentenergistics.items.ItemEnum;

import cpw.mods.fml.common.registry.GameRegistry;

public class CommonProxy {

    public int crafterRenderer;
    public int layeredRenderer;

    public void preInit() {
        Integration.preInit();
        BlockEnum.registerBlocks();
    }

    public void init() {
        if (!Integration.Mods.PROJECTE.isEnabled() && !Integration.Mods.EE3.isEnabled()) {
            unmetDependency();
        }
        Integration.init();
        registerTileEntities();
        ItemEnum.registerItems();
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

    public void registerTileEntities() {
        if (BlockEnum.EMCCONDENSER.isEnabled()) {
            GameRegistry.registerTileEntity(TileEMCCondenser.class, Reference.MOD_ID + "TileEMCCondenser");
            GameRegistry.registerTileEntity(TileEMCCondenserAdv.class, Reference.MOD_ID + "TileEMCCondenserAdv");
            GameRegistry.registerTileEntity(TileEMCCondenserExt.class, Reference.MOD_ID + "TileEMCCondenserExt");
            GameRegistry.registerTileEntity(TileEMCCondenserUlt.class, Reference.MOD_ID + "TileEMCCondenserUlt");
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
