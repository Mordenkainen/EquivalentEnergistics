package com.mordenkainen.equivalentenergistics.config;

import java.io.File;

import cpw.mods.fml.common.Loader;
import net.minecraftforge.common.config.Configuration;

public class ConfigManager {
	public static Configuration config;
	public static float crystalEMCValue;
	public static int itemsPerTick;
	public static int crystalsPerTick;
	public static double condenserIdlePower;
	public static double condenserActivePower;
	public static double crafterIdlePower;
	public static double crafterActivePower;
	public static double craftingTime;
	public static boolean useEE3;
	
	private ConfigManager() {}
	
    public static void init(File file) {
    	if(Loader.isModLoaded("EE3")) {
    		useEE3 = true;
    	}
    	config = new Configuration(file);

        config.load();
        crystalEMCValue = (float)config.get("General", "CrystalEMC", 256.0).getDouble(256.0);
        itemsPerTick = config.get("Condenser", "ItemsCondensedPerTick", 8).getInt(8);
        crystalsPerTick = config.get("Condenser", "CrystalsProducedPerTick", 16).getInt(16);
        condenserIdlePower = config.get("Condenser", "IdlePowerDrain", 0.0).getDouble(0.0);
        condenserActivePower = config.get("Condenser", "PowerDrainPerEMCCondensed", 0.01).getDouble(0.01);
        crafterIdlePower = config.get("Crafter", "IdlePowerDrain", 0.0).getDouble(0.0);
        crafterActivePower = config.get("Crafter", "PowerDrainPerCraftingTick", 1.5).getDouble(1.5);
        craftingTime = config.get("Crafter", "TicksPerCrafting", 7).getInt(7);
        useEE3 = config.get("General", "UseEE3", useEE3).getBoolean(useEE3);
        
        if (config.hasChanged()) {
       		config.save();
       	}
    }
}
