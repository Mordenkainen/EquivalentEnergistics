package com.mordenkainen.equivalentenergistics;

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
import com.mordenkainen.equivalentenergistics.lib.CreativeTabEE;
import com.mordenkainen.equivalentenergistics.lib.Reference;
import com.mordenkainen.equivalentenergistics.proxy.CommonProxy;

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
		ConfigManager.init(event.getSuggestedConfigurationFile());
		proxy.preInit();
	}
	
    @EventHandler
    public void init(final FMLInitializationEvent event) {
    	proxy.init();
    }
    
    @EventHandler
	public void postInit(final FMLPostInitializationEvent event) {
    	proxy.postInit();
    }
    
}
