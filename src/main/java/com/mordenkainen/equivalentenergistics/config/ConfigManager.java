package com.mordenkainen.equivalentenergistics.config;

import java.io.File;

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
	
	private ConfigManager() {}
	
    public static void init(File file) {
    	config = new Configuration(file);

        config.load();
        crystalEMCValue = (float)config.get("General", "CrystalEMC", 256).getDouble(256);
        itemsPerTick = config.get("Condenser", "ItemsCondensedPerTick", 8).getInt(8);
        crystalsPerTick = config.get("Condenser", "CrystalsProducedPerTick", 16).getInt(16);
        condenserIdlePower = config.get("Condenser", "IdlePowerDrain", 0).getDouble(0);
        condenserActivePower = config.get("Condenser", "PowerDrainPerEMCCondensed", 0.01).getDouble(0.01);
        crafterIdlePower = config.get("Crafter", "IdlePowerDrain", 0).getDouble(0);
        crafterActivePower = config.get("Crafter", "PowerDrainPerCraftingTick", 1.5).getDouble(1.5);
        craftingTime = config.get("Crafter", "TicksPerCrafting", 20).getDouble(20);
        
        if (config.hasChanged()) {
       		config.save();
       	}
    }
}
