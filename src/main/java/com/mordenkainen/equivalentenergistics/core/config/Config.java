package com.mordenkainen.equivalentenergistics.core.config;

import java.io.File;

import com.mordenkainen.equivalentenergistics.EquivalentEnergistics;

import net.minecraftforge.common.config.Configuration;

public class Config {
	
	public static Configuration config;

	public static int num_Cells = 8;
	public static float[] cell_Capacities = { 1000000, 4000000, 16000000, 64000000, 256000000, 1024000000, 4096000000f, 16384000000f };
    public static double[] cell_Drains = { 0.1, 0.2, 0.4, 0.8, 1.6, 3.2, 6.4, 12.8 };
    public static float creative_Capacity = 16384000000f;
    
    public static boolean debug;
    public static float maxStackEMC = 131072;

	public static double condenser_Idle_Power;
	public static double condenser_Active_Power;
	public static float condenser_EMC_Per_Tick;
	
	public static double crafter_Idle_Power;
    public static double crafter_Power_Per_EMC;
    public static double crafter_Crafting_Time;
    public static int crafter_Refresh_Time;
    
    private Config() {}
    
	public static void init(File configFile) {
		config = new Configuration(configFile);

        config.load();
	    
        for (int i = 0; i < num_Cells; i++) {
            try {
                cell_Capacities[i] = Float.valueOf(config.get("Storage Cells", "Tier" + i + "_Capacity", String.format("%.0f", cell_Capacities[i])).getString());
            } catch (final NumberFormatException e) {
                EquivalentEnergistics.logger.warn("Storage Cell Tier" + i + "_Capacity configured for invalid value! Default will be used!");
            }
            cell_Drains[i] = config.get("Storage Cells", "Tier_" + i + "_PowerDrain", cell_Drains[i]).getDouble(cell_Drains[i]);
        }
        
        try {
            creative_Capacity = Float.valueOf(config.get("Storage Cells", "Creative_Capacity", String.format("%.0f", creative_Capacity)).getString());
        } catch (final NumberFormatException e) {
            EquivalentEnergistics.logger.warn("Creative Storage Cell Creative_Capacity configured for invalid value! Default will be used!");
        }
        
        condenser_Idle_Power = config.get("Condenser", "IdlePowerDrain", 0.0).getDouble(0.0);
        condenser_Active_Power = config.get("Condenser", "PowerDrainPerEMCCondensed", 0.01).getDouble(0.01);
        condenser_EMC_Per_Tick = (float) config.get("Condenser", "EMCProducedPerTick", 8192).getDouble(8192);
        
        crafter_Idle_Power = config.get("Crafter", "IdlePowerDrain", 0.0).getDouble(0.0);
        crafter_Power_Per_EMC = config.get("Crafter", "PowerDrainPerEMC", 0.01).getDouble(0.01);
        crafter_Crafting_Time = config.get("Crafter", "TicksPerCrafting", 20).getInt(20);
        crafter_Refresh_Time = config.get("Crafter", "SecondsBetweenPatternRefreshes", 10).getInt(10);
        
        debug = config.get("General", "Debug", debug).getBoolean(false);
        
        maxStackEMC = (float) config.get("General", "MaxEMCForAStack", maxStackEMC).getDouble(maxStackEMC);

        if (config.hasChanged()) {
            config.save();
        }
	}
	
}
