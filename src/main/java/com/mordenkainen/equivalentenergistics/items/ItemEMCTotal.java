// TODO: Hide this in NEI
package com.mordenkainen.equivalentenergistics.items;

import java.util.List;

import com.mordenkainen.equivalentenergistics.lib.Reference;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemEMCTotal extends Item {

	public ItemEMCTotal() {
		super();
		setMaxStackSize(1);
	}
	
	@Override
	public void registerIcons(final IIconRegister reg) {
		itemIcon = reg.registerIcon(Reference.MOD_ID + ":EMCTotal");
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@SideOnly(Side.CLIENT)
    public void addInformation(final ItemStack stack, final EntityPlayer player, final List info, final boolean p_77624_4_) {
		if (stack.hasTagCompound()) {
			final long emc = stack.stackTagCompound.getLong("emc");
			if (emc > 0) {
				info.add("Stored EMC: " + emc);
			}
		}
	}
}
