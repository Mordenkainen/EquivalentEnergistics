package com.mordenkainen.equivalentenergistics;

import java.io.File;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;

import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.registry.GameRegistry;

import com.mordenkainen.equivalentenergistics.blocks.BlockEMCCondenser;
import com.mordenkainen.equivalentenergistics.blocks.BlockEMCCrafter;
import com.mordenkainen.equivalentenergistics.config.ConfigManager;
import com.mordenkainen.equivalentenergistics.crafting.CraftingManager;
import com.mordenkainen.equivalentenergistics.items.ItemEMCBook;
import com.mordenkainen.equivalentenergistics.items.ItemEMCCrystal;
import com.mordenkainen.equivalentenergistics.lib.CreativeTabEE;
import com.mordenkainen.equivalentenergistics.lib.Ref;
import com.mordenkainen.equivalentenergistics.proxy.CommonProxy;
import com.mordenkainen.equivalentenergistics.tiles.TileEMCCondenser;
import com.mordenkainen.equivalentenergistics.tiles.TileEMCCrafter;
import com.mordenkainen.equivalentenergistics.util.EMCUtils;
import com.mordenkainen.equivalentenergistics.util.EventHandlerModule;

@Mod(modid = Ref.MOD_ID, name = Ref.MOD_NAME, version = Ref.MOD_VERSION, dependencies = Ref.MOD_DEPENDENCIES)
public class EquivalentEnergistics {
    
	@Instance(Ref.MOD_ID)
	public static EquivalentEnergistics instance;
	
	@SidedProxy(clientSide = Ref.PROXY_LOC + "ClientProxy", serverSide = Ref.PROXY_LOC + "CommonProxy")
	public static CommonProxy proxy;
	
	public static CreativeTabs tabEE = new CreativeTabEE(CreativeTabs.getNextID(), Ref.MOD_ID);
	
	public static Logger logger;

	public static Item itemEMCCrystal;
	public static Item itemEMCBook;
	
	public static Block blockEMCCondenser;
	public static Block blockEMCCrafter;
	
	@EventHandler
	public void onServerStarting(FMLServerStartingEvent event) {
		MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
		WorldServer worldServer = server.worldServers[0];
	}
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		logger = event.getModLog();
		ConfigManager.init(new File(event.getModConfigurationDirectory(), Ref.MOD_ID + ".cfg"));
	}
	
    @EventHandler
    public void init(FMLInitializationEvent event) {
    	itemEMCCrystal = new ItemEMCCrystal();
    	GameRegistry.registerItem(itemEMCCrystal, "EMCCrystal");
    	
    	blockEMCCondenser = new BlockEMCCondenser();
    	GameRegistry.registerBlock(blockEMCCondenser, "EMCCondenser");
    	GameRegistry.registerTileEntity(TileEMCCondenser.class, Ref.MOD_ID + "TileEMCCondenser");
    	
    	blockEMCCrafter = new BlockEMCCrafter();
    	GameRegistry.registerBlock(blockEMCCrafter, "EMCCrafter");
    	GameRegistry.registerTileEntity(TileEMCCrafter.class, Ref.MOD_ID + "TileEMCCrafter");
    	
    	if(Loader.isModLoaded("ProjectE")) {
	    	itemEMCBook = new ItemEMCBook();
	    	GameRegistry.registerItem(itemEMCBook, "EMCBook");
    	}
    	
    	new EventHandlerModule();
    	proxy.initRenderers();
    	CraftingManager.initRecipes();
    }
    
    @EventHandler
	public void postInit(FMLPostInitializationEvent event) {
    	EMCUtils.getInstance().setCrystalEMC(ConfigManager.crystalEMCValue);
    }
}
