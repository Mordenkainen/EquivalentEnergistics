package com.mordenkainen.equivalentenergistics.items;

import java.util.List;

import com.mordenkainen.equivalentenergistics.EquivalentEnergistics;
import com.mordenkainen.equivalentenergistics.lib.Ref;

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
		setCreativeTab(EquivalentEnergistics.tabEE);
		setHasSubtypes(true);
	}
	
	@Override
	public void registerIcons(IIconRegister reg) {
		itemIcon = reg.registerIcon(Ref.TEXTURE_PREFIX + "EMCCrystal");
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item id, CreativeTabs tab, List list) {
		for (int i = 0; i < 3; i++) {
			ItemStack stack  = new ItemStack(id, 1, i);
			list.add(stack);
		}
	}
	
	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return "item." + Ref.getId("EMCCrystal") + "." + stack.getItemDamage();
	}
}
