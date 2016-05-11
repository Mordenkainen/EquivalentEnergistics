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
import com.mordenkainen.equivalentenergistics.lib.Reference;
import com.mordenkainen.equivalentenergistics.proxy.CommonProxy;
import com.mordenkainen.equivalentenergistics.util.EMCEventHandler;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.MOD_VERSION, dependencies = Reference.MOD_DEPENDENCIES)
public class EquivalentEnergistics {
    
	@Instance(Reference.MOD_ID)
	public static EquivalentEnergistics instance;
	
	@SidedProxy(clientSide = Reference.PROXY_LOC + "ClientProxy", serverSide = Reference.PROXY_LOC + "CommonProxy")
	public static CommonProxy proxy;
	
	public static CreativeTabs tabEE = new CreativeTabEE(CreativeTabs.getNextID(), Reference.MOD_ID);
	
	public static Logger logger;
	
	@EventHandler
	public void preInit(final FMLPreInitializationEvent event) {
		logger = event.getModLog();
		ConfigManager.init(new File(event.getModConfigurationDirectory(), Reference.MOD_ID + ".cfg"));
		proxy.preInit();
	}
	
    @EventHandler
    public void init(final FMLInitializationEvent event) {
    	if(!Integration.Mods.PROJECTE.isEnabled() && !Integration.Mods.EE3.isEnabled()) {
    		proxy.unmetDependency();
    	}
    	
    	proxy.init();
    	
    	new EMCEventHandler();
    	CraftingManager.initRecipes();
    }
    
    @EventHandler
	public void postInit(final FMLPostInitializationEvent event) {
    	proxy.postInit();
    }
}
