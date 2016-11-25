// TODO fix config handling
package com.mordenkainen.equivalentenergistics.config;

import java.io.File;

import com.mordenkainen.equivalentenergistics.integration.Integration;
import com.mordenkainen.equivalentenergistics.registries.BlockEnum;
import com.mordenkainen.equivalentenergistics.registries.ItemEnum;

import net.minecraftforge.common.config.Configuration;

public final class ConfigManager {

    public static Configuration config;
    public static float crystalEMCValue;
    public static boolean useEE3;
    public static boolean debug;

    private ConfigManager() {}

    public static void init(final File file) {
        config = new Configuration(file);

        config.load();

        Integration.loadConfig(config);

        BlockEnum.loadConfig(config);

        ItemEnum.loadConfig(config);

        if (Integration.Mods.EE3.isEnabled()) {
            useEE3 = true;
        }
        useEE3 = config.get("General", "UseEE3", useEE3).getBoolean(useEE3);

        debug = config.get("General", "Debug", debug).getBoolean(false);

        if (config.hasChanged()) {
            config.save();
        }
    }

}
