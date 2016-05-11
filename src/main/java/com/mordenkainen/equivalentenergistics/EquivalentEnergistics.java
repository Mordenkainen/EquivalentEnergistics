package com.mordenkainen.equivalentenergistics;

import java.io.File;

import net.minecraft.creativetab.CreativeTabs;

import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

import com.mordenkainen.equivalentenergistics.config.ConfigManager;
import com.mordenkainen.equivalentenergistics.crafting.CraftingManager;
import com.mordenkainen.equivalentenergistics.integration.Integration;
import com.mordenkainen.equivalentenergistics.lib.CreativeTabEE;
import com.mordenkainen.equivalentenergistics.lib.Ref;
import com.mordenkainen.equivalentenergistics.proxy.CommonProxy;
import com.mordenkainen.equivalentenergistics.util.EMCUtils;
import com.mordenkainen.equivalentenergistics.util.EMCEventHandler;

@Mod(modid = Ref.MOD_ID, name = Ref.MOD_NAME, version = Ref.MOD_VERSION, dependencies = Ref.MOD_DEPENDENCIES)
public class EquivalentEnergistics {
    
	@Instance(Ref.MOD_ID)
	public static EquivalentEnergistics instance;
	
	@SidedProxy(clientSide = Ref.PROXY_LOC + "ClientProxy", serverSide = Ref.PROXY_LOC + "CommonProxy")
	public static CommonProxy proxy;
	
	public static Integration integration = new Integration();
	
	public static CreativeTabs tabEE = new CreativeTabEE(CreativeTabs.getNextID(), Ref.MOD_ID);
	
	public static Logger logger;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		logger = event.getModLog();
		ConfigManager.init(new File(event.getModConfigurationDirectory(), Ref.MOD_ID + ".cfg"));
		integration.preInit();
		proxy.registerBlocks();
	}
	
    @EventHandler
    public void init(FMLInitializationEvent event) {
    	if(!Integration.Mods.PROJECTE.isEnabled() && !Integration.Mods.EE3.isEnabled()) {
    		proxy.unmetDependency();
    	}
    	
    	proxy.registerTileEntities();
    	
    	proxy.registerItems();
    	
    	new EMCEventHandler();
    	proxy.initRenderers();
    	integration.init();
    	CraftingManager.initRecipes();
    }
    
    @EventHandler
	public void postInit(FMLPostInitializationEvent event) {
    	EMCUtils.getInstance().setCrystalEMC(ConfigManager.crystalEMCValue);
    	integration.postInit();
    }
}
