package com.mordenkainen.equivalentenergistics.lib;

import com.mordenkainen.equivalentenergistics.EquivalentEnergistics;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

public class CreativeTabEE extends CreativeTabs {

	public CreativeTabEE(int id, String label) {
		super(id, label);
	}

	@Override
	public Item getTabIconItem() {
		return EquivalentEnergistics.EMCCrystal;
	}

}
