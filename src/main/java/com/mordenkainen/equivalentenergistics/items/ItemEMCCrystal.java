package com.mordenkainen.equivalentenergistics.items;

import com.mordenkainen.equivalentenergistics.config.ConfigManager;
import com.mordenkainen.equivalentenergistics.registries.TextureEnum;
import com.pahimar.ee3.api.exchange.EnergyValue;
import com.pahimar.ee3.api.exchange.IEnergyValueProvider;

import cpw.mods.fml.common.Optional;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

@Optional.Interface(iface = "com.pahimar.ee3.api.exchange.IEnergyValueProvider", modid = "EE3")
public class ItemEMCCrystal extends ItemMultiBase implements IEnergyValueProvider {

    final public static float[] CRYSTAL_VALUES = { 1, 64, 4096, 262144, 16777216 };

    public ItemEMCCrystal() {
        super(5);
    }
    
    @Override
    public EnumRarity getRarity(final ItemStack stack) {
    	final int damage = stack.getItemDamage();
        return damage <= 1 ? EnumRarity.common : EnumRarity.values()[damage - 1];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(final int damage) {
        return TextureEnum.EMCCRYSTAL.getTexture(damage);
    }
   
    @Optional.Method(modid = "EE3")
    @Override
    public EnergyValue getEnergyValue(final ItemStack stack) {
        if (!ConfigManager.useEE3) {
            return null;
        }

        return new EnergyValue(CRYSTAL_VALUES[stack.getItemDamage()]);
    }

}
