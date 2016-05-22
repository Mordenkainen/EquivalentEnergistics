package com.mordenkainen.equivalentenergistics.lib;

import com.mordenkainen.equivalentenergistics.registries.ItemEnum;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class CreativeTabEE extends CreativeTabs {

	public CreativeTabEE(final int tabID, final String label) {
		super(tabID, label);
	}

	@Override
	public Item getTabIconItem() {
		return ItemEnum.EMCCRYSTAL.getItem();
	}
	
}
