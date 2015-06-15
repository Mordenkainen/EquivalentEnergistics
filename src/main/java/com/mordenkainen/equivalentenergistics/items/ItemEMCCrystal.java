package com.mordenkainen.equivalentenergistics.items;

import com.mordenkainen.equivalentenergistics.EquivalentEnergistics;
import com.mordenkainen.equivalentenergistics.lib.Ref;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;

public class ItemEMCCrystal extends Item {

	public ItemEMCCrystal() {
		super();
		setMaxStackSize(64);
		setCreativeTab(EquivalentEnergistics.tabEE);
		setUnlocalizedName(Ref.getId("EMCCrystal"));
	}
	
	@Override
	public void registerIcons(IIconRegister reg) {
		itemIcon = reg.registerIcon(Ref.TEXTURE_PREFIX + "EMCCrystal");
	}
}
