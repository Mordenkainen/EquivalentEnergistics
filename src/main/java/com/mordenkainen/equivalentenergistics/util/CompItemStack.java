package com.mordenkainen.equivalentenergistics.util;

import com.google.common.base.Equivalence;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class CompItemStack extends Equivalence<ItemStack> {
	protected boolean doEquivalent(ItemStack a, ItemStack b) {
		if ((a.getItem() != b.getItem()) || (a.getItemDamage() != b.getItemDamage()) || (a.stackTagCompound != b.stackTagCompound)) {
			return false;
		}
		return true;
	}

	protected int doHash(ItemStack t) {
		int prime = 31;
		int result = 1;
		result = prime * result + Item.getIdFromItem(t.getItem());
		result = prime * result + t.getItemDamage();
		result = prime * result + (t.hasTagCompound() ? t.stackTagCompound.hashCode() : 0);
		return result;
	}
}
