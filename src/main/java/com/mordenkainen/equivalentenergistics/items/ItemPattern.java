// TODO: Hide this in NEI
package com.mordenkainen.equivalentenergistics.items;

import com.mordenkainen.equivalentenergistics.util.EMCCraftingPattern;

import net.minecraft.client.renderer.texture.IIconRegister;
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
	public void registerIcons(final IIconRegister reg) {}
	
	@Override
	public ICraftingPatternDetails getPatternForItem(final ItemStack stack, final World world) {
		final ItemStack target = ItemStack.loadItemStackFromNBT(stack.getTagCompound());
		return EMCCraftingPattern.get(target); 
	}
	
	public void setTargetItem(final ItemStack pattern, final ItemStack target) {
		if(!pattern.hasTagCompound()) {
			pattern.stackTagCompound = new NBTTagCompound();
		}
		target.writeToNBT(pattern.getTagCompound());
	}
}
