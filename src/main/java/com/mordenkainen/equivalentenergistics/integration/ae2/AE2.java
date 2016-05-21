package com.mordenkainen.equivalentenergistics.integration.ae2;

import java.lang.reflect.Method;

import com.mordenkainen.equivalentenergistics.registries.ItemEnum;
import com.mordenkainen.equivalentenergistics.util.CommonUtils;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.oredict.OreDictionary;

public final class AE2 {
	
	private static AE2 instance;
	
	private AE2() {}
	
	public static void init() {
		instance = new AE2();
		MinecraftForge.EVENT_BUS.register(instance);
	}
	
	@SubscribeEvent
	public void worldLoad(WorldEvent.Load event) {
		try {
			Class<?> cellInv = Class.forName("appeng.me.storage.CellInventory");
			Method blackList = cellInv.getDeclaredMethod("addBasicBlackList", int.class, int.class);
			blackList.invoke(null, Item.getIdFromItem(ItemEnum.EMCCRYSTAL.getItem()), OreDictionary.WILDCARD_VALUE);
		} catch (Exception e) {
			CommonUtils.debugLog("Failed to blacklist EMC Crystals from AE Cells", e);
		}
	}
}
