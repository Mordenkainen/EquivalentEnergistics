package com.mordenkainen.equivalentenergistics.core.config;

import java.io.File;

import com.mordenkainen.equivalentenergistics.EquivalentEnergistics;

import net.minecraftforge.common.config.Configuration;

public final class Config {
	
	public static Configuration configuration;

	public static int numCells = 8;
	public static float[] cellCapacities = { 1000000, 4000000, 16000000, 64000000, 256000000, 1024000000, 4096000000f, 16384000000f };
    public static double[] cellDrains = { 0.1, 0.2, 0.4, 0.8, 1.6, 3.2, 6.4, 12.8 };
    public static float creativeCapacity = 16384000000f;
    
    public static boolean debug;
    public static float maxStackEMC = 131072;

	public static double condenserIdlePower;
	public static double condenserActivePower;
	public static float condenserEMCPerTick;
	
	public static double crafterIdlePower;
    public static double crafterPowerPerEMC;
    public static double crafterCraftingTime;
    public static int crafterRefreshTime;
    
    private Config() {}
    
	public static void init(final File configFile) {
		configuration = new Configuration(configFile);

        configuration.load();
	    
        for (int i = 0; i < numCells; i++) {
            try {
                cellCapacities[i] = Float.valueOf(configuration.get("Storage Cells", "Tier" + i + "_Capacity", String.format("%.0f", cellCapacities[i])).getString());
            } catch (final NumberFormatException e) {
                EquivalentEnergistics.logger.warn("Storage Cell Tier" + i + "_Capacity configured for invalid value! Default will be used!");
            }
            cellDrains[i] = configuration.get("Storage Cells", "Tier_" + i + "_PowerDrain", cellDrains[i]).getDouble(cellDrains[i]);
        }
        
        try {
            creativeCapacity = Float.valueOf(configuration.get("Storage Cells", "Creative_Capacity", String.format("%.0f", creativeCapacity)).getString());
        } catch (final NumberFormatException e) {
            EquivalentEnergistics.logger.warn("Creative Storage Cell Creative_Capacity configured for invalid value! Default will be used!");
        }
        
        condenserIdlePower = configuration.get("Condenser", "IdlePowerDrain", 0.0).getDouble(0.0);
        condenserActivePower = configuration.get("Condenser", "PowerDrainPerEMCCondensed", 0.01).getDouble(0.01);
        condenserEMCPerTick = (float) configuration.get("Condenser", "EMCProducedPerTick", 8192).getDouble(8192);
        
        crafterIdlePower = configuration.get("Crafter", "IdlePowerDrain", 0.0).getDouble(0.0);
        crafterPowerPerEMC = configuration.get("Crafter", "PowerDrainPerEMC", 0.01).getDouble(0.01);
        crafterCraftingTime = configuration.get("Crafter", "TicksPerCrafting", 20).getInt(20);
        crafterRefreshTime = configuration.get("Crafter", "SecondsBetweenPatternRefreshes", 10).getInt(10);
        
        debug = configuration.get("General", "Debug", debug).getBoolean(false);
        
        maxStackEMC = (float) configuration.get("General", "MaxEMCForAStack", maxStackEMC).getDouble(maxStackEMC);

        if (configuration.hasChanged()) {
            configuration.save();
        }
	}
	
}
