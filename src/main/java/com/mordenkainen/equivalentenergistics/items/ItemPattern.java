// TODO: Hide this in NEI
package com.mordenkainen.equivalentenergistics.items;

import com.mordenkainen.equivalentenergistics.util.EMCCraftingPattern;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import appeng.api.implementations.ICraftingPatternItem;
import appeng.api.networking.crafting.ICraftingPatternDetails;

public class ItemPattern extends Item implements ICraftingPatternItem {

	public ItemPattern() {
		super();
		setMaxStackSize(1);
	}
	
	@Override
	public ICraftingPatternDetails getPatternForItem(ItemStack paramItemStack, World paramWorld) {
		ItemStack target = ItemStack.loadItemStackFromNBT(paramItemStack.getTagCompound());
		return EMCCraftingPattern.get(target);
	}
	
	public void setTargetItem(ItemStack pattern, ItemStack target) {
		if(!pattern.hasTagCompound()) {
			pattern.stackTagCompound = new NBTTagCompound();
		}
		target.writeToNBT(pattern.getTagCompound());
	}
}
