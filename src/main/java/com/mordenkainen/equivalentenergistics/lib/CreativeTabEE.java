package com.mordenkainen.equivalentenergistics.lib;

import com.mordenkainen.equivalentenergistics.EquivalentEnergistics;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class CreativeTabEE extends CreativeTabs {

	public CreativeTabEE(final int id, final String label) {
		super(id, label);
	}

	@Override
	public Item getTabIconItem() {
		return EquivalentEnergistics.EMCCrystal;
	}

}
