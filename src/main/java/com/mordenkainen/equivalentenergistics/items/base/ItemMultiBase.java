package com.mordenkainen.equivalentenergistics.items.base;

import com.mordenkainen.equivalentenergistics.EquivalentEnergistics;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public abstract class ItemMultiBase extends ItemBase {

	public int item_count;
	
	public ItemMultiBase(String name, int count) {
		super(name);
		item_count = count;
		setHasSubtypes(true);
	}
	
	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if (this.isInCreativeTab(tab)) {
			for (int i = 0; i < item_count; i++) {
				items.add(new ItemStack(this, 1, i));
			}
		}
	}
	
	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return this.getUnlocalizedName() + "_" + stack.getItemDamage();
	}
	
	@Override
	public void registerItemModel() {
		for (int i = 0; i < item_count; i++) {
			EquivalentEnergistics.proxy.registerItemRenderer(this, i, name + "_" + i);
		}
	}

}
