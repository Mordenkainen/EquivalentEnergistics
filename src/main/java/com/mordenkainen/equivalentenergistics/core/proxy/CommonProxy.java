package com.mordenkainen.equivalentenergistics.core.proxy;

import net.minecraft.item.Item;

public class CommonProxy {

    public void preInit() {}

    public void init() {
        initRenderers();
        registerModelBakeryVariants();
    }

    public void postInit() {}

    public boolean isClient() {
        return false;
    }

    public boolean isServer() {
        return true;
    }

    public void initRenderers() {}

    public void registerItemRenderer(final Item item, final int meta, final String name) {}

    public void registerItemRenderer(final Item item, final int meta, final String name, final String variant) {}

    public void registerModelBakeryVariants() {}

}
