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
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;

import com.mordenkainen.equivalentenergistics.blocks.BlockEMCCondenser;
import com.mordenkainen.equivalentenergistics.blocks.BlockEMCCrafter;
import com.mordenkainen.equivalentenergistics.config.ConfigManager;
import com.mordenkainen.equivalentenergistics.crafting.CraftingManager;
import com.mordenkainen.equivalentenergistics.items.ItemEMCBook;
import com.mordenkainen.equivalentenergistics.items.ItemEMCCrystal;
import com.mordenkainen.equivalentenergistics.items.ItemPattern;
import com.mordenkainen.equivalentenergistics.lib.CreativeTabEE;
import com.mordenkainen.equivalentenergistics.lib.Ref;
import com.mordenkainen.equivalentenergistics.proxy.CommonProxy;
import com.mordenkainen.equivalentenergistics.tiles.TileEMCCondenser;
import com.mordenkainen.equivalentenergistics.tiles.TileEMCCrafter;
import com.mordenkainen.equivalentenergistics.util.EMCUtils;
import com.mordenkainen.equivalentenergistics.util.EventHandlerEE3;
import com.mordenkainen.equivalentenergistics.util.EventHandlerPE;

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
	public static Item itemPattern;
	
	public static Block blockEMCCondenser;
	public static Block blockEMCCrafter;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		logger = event.getModLog();
		ConfigManager.init(new File(event.getModConfigurationDirectory(), Ref.MOD_ID + ".cfg"));
	}
	
    @EventHandler
    public void init(FMLInitializationEvent event) {
    	if(!Loader.isModLoaded("ProjectE") && !Loader.isModLoaded("EE3")) {
    		proxy.unmetDependency();
    	}
    	
    	itemEMCCrystal = new ItemEMCCrystal();
    	GameRegistry.registerItem(itemEMCCrystal, "EMCCrystal");
    	
    	itemPattern = new ItemPattern();
    	GameRegistry.registerItem(itemPattern, "EMCPattern");
    	
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
    	
    	if(ConfigManager.useEE3) {
    		new EventHandlerEE3();
    	} else {
    		new EventHandlerPE();
    	}
    	proxy.initRenderers();
    	CraftingManager.initRecipes();
    	FMLInterModComms.sendMessage("Waila", "register", "com.mordenkainen.equivalentenergistics.waila.WailaProvider.callbackRegister");
    }
    
    @EventHandler
	public void postInit(FMLPostInitializationEvent event) {
    	EMCUtils.getInstance().setCrystalEMC(ConfigManager.crystalEMCValue);
    }
}
