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

	public void registerItemRenderer(Item item, int meta, String name) {}
	
	public void registerItemRenderer(Item item, int meta, String name, String variant) {}
	
	public void registerModelBakeryVariants() {}

}
