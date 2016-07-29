package com.mordenkainen.equivalentenergistics.items;

import java.util.List;

import com.mordenkainen.equivalentenergistics.config.ConfigManager;
import com.mordenkainen.equivalentenergistics.lib.Reference;
import com.mordenkainen.equivalentenergistics.registries.TextureEnum;
import com.pahimar.ee3.api.exchange.EnergyValue;
import com.pahimar.ee3.api.exchange.IEnergyValueProvider;

import cpw.mods.fml.common.Optional;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

@Optional.Interface(iface = "com.pahimar.ee3.api.exchange.IEnergyValueProvider", modid = "EE3")
public class ItemEMCCrystal extends Item implements IEnergyValueProvider {

	final public static float[] crystalValues = {1, 64, 256, 4096, 262144, 16777216};
	
	public ItemEMCCrystal() {
		super();
		setHasSubtypes(true);
	}
	
	@Override
	public void registerIcons(final IIconRegister reg) {}
	
	@Override
	@SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(final int damage) {
        return TextureEnum.EMCCRYSTAL.getTexture(damage);
    }
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(final Item item, final CreativeTabs tab, final List list) {
		for (int i = 0; i < 5; i++) {
			final ItemStack stack  = new ItemStack(item, 1, i);
			list.add(stack);
		}
	}
	
	@Override
	public String getUnlocalizedName(final ItemStack stack) {
		return "item." + Reference.MOD_ID + ":" + "EMCCrystal." + stack.getItemDamage();
	}

	@Optional.Method(modid = "EE3")
	@Override
	public EnergyValue getEnergyValue(ItemStack stack) {
		if (!ConfigManager.useEE3) {
			return null;
		}
		
		return new EnergyValue(crystalValues[stack.getItemDamage()]);
	}
	
}
