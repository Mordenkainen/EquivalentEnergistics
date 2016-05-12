package com.mordenkainen.equivalentenergistics.items;

import java.util.List;

import com.mordenkainen.equivalentenergistics.lib.Reference;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemEMCCrystal extends Item {

	public ItemEMCCrystal() {
		super();
		setMaxStackSize(64);
		setHasSubtypes(true);
	}
	
	@Override
	public void registerIcons(final IIconRegister reg) {
		itemIcon = reg.registerIcon(Reference.TEXTURE_PREFIX + "EMCCrystal");
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(final Item item, final CreativeTabs tab, final List list) {
		for (int i = 0; i < 3; i++) {
			final ItemStack stack  = new ItemStack(item, 1, i);
			list.add(stack);
		}
	}
	
	@Override
	public String getUnlocalizedName(final ItemStack stack) {
		return "item." + Reference.getId("EMCCrystal") + "." + stack.getItemDamage();
	}
}
