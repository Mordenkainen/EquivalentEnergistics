package com.mordenkainen.equivalentenergistics;

import org.apache.logging.log4j.Logger;

import com.mordenkainen.equivalentenergistics.core.CreativeTabEE;
import com.mordenkainen.equivalentenergistics.core.Reference;
import com.mordenkainen.equivalentenergistics.core.config.ConfigManager;
import com.mordenkainen.equivalentenergistics.core.proxy.CommonProxy;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraft.creativetab.CreativeTabs;

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
