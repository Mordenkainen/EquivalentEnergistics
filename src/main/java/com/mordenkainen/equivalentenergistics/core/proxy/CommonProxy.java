package com.mordenkainen.equivalentenergistics.core.proxy;

import com.mordenkainen.equivalentenergistics.blocks.BlockEnum;
import com.mordenkainen.equivalentenergistics.blocks.condenser.tiles.TileEMCCondenser;
import com.mordenkainen.equivalentenergistics.blocks.condenser.tiles.TileEMCCondenserAdv;
import com.mordenkainen.equivalentenergistics.blocks.condenser.tiles.TileEMCCondenserExt;
import com.mordenkainen.equivalentenergistics.blocks.condenser.tiles.TileEMCCondenserUlt;
import com.mordenkainen.equivalentenergistics.blocks.crafter.tiles.TileEMCCrafter;
import com.mordenkainen.equivalentenergistics.blocks.crafter.tiles.TileEMCCrafterAdv;
import com.mordenkainen.equivalentenergistics.blocks.crafter.tiles.TileEMCCrafterExt;
import com.mordenkainen.equivalentenergistics.blocks.crafter.tiles.TileEMCCrafterUlt;
import com.mordenkainen.equivalentenergistics.blocks.provider.tile.TileEMCPatternProvider;
import com.mordenkainen.equivalentenergistics.core.Reference;
import com.mordenkainen.equivalentenergistics.core.TickHandler;
import com.mordenkainen.equivalentenergistics.core.crafting.CraftingManager;
import com.mordenkainen.equivalentenergistics.core.exceptions.ServerUnmetDependencyException;
import com.mordenkainen.equivalentenergistics.integration.Integration;
import com.mordenkainen.equivalentenergistics.items.ItemEnum;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraftforge.common.MinecraftForge;

public class CommonProxy {

    public int crafterRenderer;
    public int condenserRenderer;
    public int providerRenderer;

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
        FMLCommonHandler.instance().bus().register(TickHandler.INSTANCE);
        MinecraftForge.EVENT_BUS.register(TickHandler.INSTANCE);
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
            GameRegistry.registerTileEntity(TileEMCCrafterAdv.class, Reference.MOD_ID + "TileEMCCrafterAdv");
            GameRegistry.registerTileEntity(TileEMCCrafterExt.class, Reference.MOD_ID + "TileEMCCrafterExt");
            GameRegistry.registerTileEntity(TileEMCCrafterUlt.class, Reference.MOD_ID + "TileEMCCrafterUlt");
        }
        if (BlockEnum.EMCPROVIDER.isEnabled()) {
            GameRegistry.registerTileEntity(TileEMCPatternProvider.class, Reference.MOD_ID + "TileEMCProvider");
        }
    }

    public void initRenderers() {}

    public void unmetDependency() {
        throw new ServerUnmetDependencyException("Equivalent Energistics requires either Equivalent Exchange 3 or ProjectE to be installed and enabled!");
    }

}
