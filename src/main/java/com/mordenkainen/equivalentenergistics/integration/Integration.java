package com.mordenkainen.equivalentenergistics.integration;

import com.google.common.base.Predicate;
import com.mordenkainen.equivalentenergistics.EquivalentEnergistics;
import com.mordenkainen.equivalentenergistics.config.ConfigManager;
import com.mordenkainen.equivalentenergistics.integration.ae2.AppliedEnergistics2;
import com.mordenkainen.equivalentenergistics.integration.ee3.EquivExchange3;
import com.mordenkainen.equivalentenergistics.integration.projecte.ProjectE;
import com.mordenkainen.equivalentenergistics.integration.waila.Waila;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModAPIManager;
import cpw.mods.fml.relauncher.Side;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;

public final class Integration {

    public static IEMCHandler emcHandler;

    public enum Mods {
        WAILA("Waila"),
        NEI("NotEnoughItems", Side.CLIENT),
        EE3("EE3", "EquivalentExchange3"),
        PROJECTE("ProjectE"),
        AE2("appliedenergistics2", false);

        private final String modID;

        private boolean shouldLoad = true;

        private final String name;

        private final Side modSide;

        private final boolean usesConfig;

        Mods(final String modid) {
            this(modid, modid, null, true);
        }

        Mods(final String modid, final boolean hasConfig) {
            this(modid, modid, null, hasConfig);
        }

        Mods(final String modid, final String modName) {
            this(modid, modName, null, true);
        }

        Mods(final String modid, final Side side) {
            this(modid, modid, side, true);
        }

        Mods(final String modid, final String modName, final boolean hasConfig) {
            this(modid, modName, null, hasConfig);
        }

        Mods(final String modid, final String modName, final Side side) {
            this(modid, modid, side, true);
        }

        Mods(final String modid, final Side side, final boolean hasConfig) {
            this(modid, modid, side, hasConfig);
        }

        Mods(final String modid, final String modName, final Side side, final boolean hasConfig) {
            modID = modid;
            name = modName;
            modSide = side;
            usesConfig = hasConfig;
        }

        public String getModID() {
            return modID;
        }

        public String getModName() {
            return name;
        }

        public boolean isOnClient() {
            return modSide != Side.SERVER;
        }

        public boolean isOnServer() {
            return modSide != Side.CLIENT;
        }

        public void loadConfig(final Configuration config) {
            if (usesConfig) {
                shouldLoad = config.get("Integration", "enable" + getModName(), true, "Enable " + getModName() + " Integration.").getBoolean(true);
            } else {
                shouldLoad = true;
            }
        }

        public boolean isEnabled() {
            return (Loader.isModLoaded(getModID()) || ModAPIManager.INSTANCE.hasAPI(getModID())) && shouldLoad && correctSide();
        }

        public Predicate<Mods> getTest() {
            return new Predicate<Mods>() {
                @Override
                public boolean apply(final Mods input) {
                    return isEnabled();
                }
            };
        }

        private boolean correctSide() {
            return EquivalentEnergistics.proxy.isClient() ? isOnClient() : isOnServer();
        }
    }

    private Integration() {}

    public static void loadConfig(final Configuration config) {
        for (final Mods mod : Mods.values()) {
            mod.loadConfig(config);
        }
    }

    public static void preInit() {}

    public static void init() {
        if (Mods.WAILA.isEnabled()) {
            Waila.init();
        }
        if (Mods.AE2.isEnabled()) {
            AppliedEnergistics2.init();
        }
        if (ConfigManager.useEE3) {
            emcHandler = new EquivExchange3();
        } else {
            emcHandler = new ProjectE();
        }
    }

    public static void postInit() {
        emcHandler.setCrystalEMC();
        MinecraftForge.EVENT_BUS.register(emcHandler);
    }

}
