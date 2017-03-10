package com.mordenkainen.equivalentenergistics.items;

import com.mordenkainen.equivalentenergistics.core.config.ConfigManager;
import com.mordenkainen.equivalentenergistics.core.config.IConfigurable;
import com.mordenkainen.equivalentenergistics.core.textures.TextureEnum;
import com.pahimar.ee3.api.exchange.EnergyValue;
import com.pahimar.ee3.api.exchange.IEnergyValueProvider;

import cpw.mods.fml.common.Optional;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.config.Configuration;

@Optional.Interface(iface = "com.pahimar.ee3.api.exchange.IEnergyValueProvider", modid = "EE3")
public class ItemEMCCrystalOld extends ItemMultiBase implements IEnergyValueProvider, IConfigurable {

    public final static float[] CRYSTAL_VALUES = { 256, 147456, 84934656 };

    public ItemEMCCrystalOld() {
        super(3);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(final int damage) {
        return TextureEnum.EMCCRYSTALOLD.getTexture();
    }

    @Optional.Method(modid = "EE3")
    @Override
    public EnergyValue getEnergyValue(final ItemStack stack) {
        if (!ConfigManager.useEE3) {
            return null;
        }

        return new EnergyValue(CRYSTAL_VALUES[stack.getItemDamage()]);
    }

    @Override
    public void loadConfig(final Configuration config) {
        CRYSTAL_VALUES[0] = (float) config.get("General", "CrystalEMC", CRYSTAL_VALUES[0]).getDouble(CRYSTAL_VALUES[0]);
        CRYSTAL_VALUES[1] = CRYSTAL_VALUES[0] * 576;
        CRYSTAL_VALUES[2] = CRYSTAL_VALUES[0] * (float) Math.pow(576, 2);
    }

}
