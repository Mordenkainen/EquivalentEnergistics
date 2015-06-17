package com.mordenkainen.equivalentenergistics.config;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class ConfigManager {
	public static Configuration config;
	public static int EMCValue;
	
	private ConfigManager() {}
	
    public static void init(File file) {
    	config = new Configuration(file);

        config.load();
        EMCValue = config.get("General", "CrystalEMC", 256).getInt(256);
        
        if (config.hasChanged()) {
       		config.save();
       	}
    }
}
