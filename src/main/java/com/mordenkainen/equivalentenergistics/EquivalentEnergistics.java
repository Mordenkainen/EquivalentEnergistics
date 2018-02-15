package com.mordenkainen.equivalentenergistics;

import org.apache.logging.log4j.Logger;

import com.mordenkainen.equivalentenergistics.core.CreativeTabEE;
import com.mordenkainen.equivalentenergistics.core.Reference;
import com.mordenkainen.equivalentenergistics.core.proxy.CommonProxy;
import com.mordenkainen.equivalentenergistics.integration.Integration;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

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
        proxy.preInit();
        Integration.preInit();
    }

    @EventHandler
    public void init(final FMLInitializationEvent event) {
        proxy.init();
        Integration.init();
    }

    @EventHandler
    public void postInit(final FMLPostInitializationEvent event) {
        proxy.postInit();
        Integration.postInit();
    }

}
