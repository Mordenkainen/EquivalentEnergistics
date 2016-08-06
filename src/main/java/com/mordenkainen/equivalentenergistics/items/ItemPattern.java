package com.mordenkainen.equivalentenergistics.items;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import com.mordenkainen.equivalentenergistics.integration.ae2.EMCCraftingPattern;

import appeng.api.implementations.ICraftingPatternItem;
import appeng.api.networking.crafting.ICraftingPatternDetails;

import cpw.mods.fml.common.Optional;

@Optional.Interface(iface = "appeng.api.implementations.ICraftingPatternItem", modid = "appliedenergistics2") // NOPMD
public class ItemPattern extends ItemBase implements ICraftingPatternItem {

	public ItemPattern() {
		super();
		setMaxStackSize(1);
	}
	
	// ICraftingPatternItem Overrides
	// ------------------------
	@Optional.Method(modid = "appliedenergistics2")
	@Override
	public ICraftingPatternDetails getPatternForItem(final ItemStack stack, final World world) {
		return EMCCraftingPattern.get(ItemStack.loadItemStackFromNBT(stack.getTagCompound()));
	}
	
	@Optional.Method(modid = "appliedenergistics2")
	public void setTargetItem(final ItemStack pattern, final ItemStack target) {
		if(!pattern.hasTagCompound()) {
			pattern.stackTagCompound = new NBTTagCompound();
		}
		target.writeToNBT(pattern.getTagCompound());
	}
	// ------------------------
}
