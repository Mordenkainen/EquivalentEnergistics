package com.mordenkainen.equivalentenergistics.items;

import com.mordenkainen.equivalentenergistics.EquivalentEnergistics;
import com.mordenkainen.equivalentenergistics.lib.Ref;
import com.mordenkainen.equivalentenergistics.util.CrystalCraftingPattern;
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
		setUnlocalizedName(Ref.getId("EMCPattern"));
	}
	
	@Override
	public ICraftingPatternDetails getPatternForItem(ItemStack paramItemStack, World paramWorld) {
		ItemStack target = ItemStack.loadItemStackFromNBT(paramItemStack.getTagCompound());
		if(target.getItem() == EquivalentEnergistics.itemEMCCrystal) {
			return new CrystalCraftingPattern(target.getItemDamage());
		} else {
			return new EMCCraftingPattern(target);
		}
	}
	
	public void setTargetItem(ItemStack pattern, ItemStack target) {
		if(!pattern.hasTagCompound()) {
			pattern.stackTagCompound = new NBTTagCompound();
		}
		target.writeToNBT(pattern.getTagCompound());
	}
	
	@Override
	public void registerIcons(IIconRegister reg) {
		itemIcon = reg.registerIcon(Ref.TEXTURE_PREFIX + "EMCPattern");
	}

}
