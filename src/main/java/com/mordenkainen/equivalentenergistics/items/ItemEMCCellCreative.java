package com.mordenkainen.equivalentenergistics.items;

import com.mordenkainen.equivalentenergistics.EquivalentEnergistics;
import com.mordenkainen.equivalentenergistics.core.config.IConfigurable;
import com.mordenkainen.equivalentenergistics.core.textures.TextureEnum;
import com.mordenkainen.equivalentenergistics.integration.ae2.cells.HandlerEMCCellCreative;

import appeng.api.storage.IMEInventory;
import appeng.api.storage.IMEInventoryHandler;
import appeng.api.storage.ISaveProvider;
import appeng.api.storage.StorageChannel;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.config.Configuration;

public class ItemEMCCellCreative extends ItemEMCCellBase implements IConfigurable {

    private static final String GROUP = "Storage Cells";

    public static double capacity = 16384000000f;

    public ItemEMCCellCreative() {
        super(1);
    }

    @Override
    public EnumRarity getRarity(final ItemStack stack) {
        return EnumRarity.epic;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(final int damage) {
        return TextureEnum.EMCCELLCREATIVE.getTexture();
    }

    @Optional.Method(modid = "appliedenergistics2")
    @SuppressWarnings("rawtypes")
    @Override
    public IMEInventoryHandler getCellInventory(final ItemStack stack, final ISaveProvider host, final StorageChannel channel) {
        if (channel == StorageChannel.ITEMS && isCell(stack)) {
            return new HandlerEMCCellCreative(host);
        }
        return null;
    }

    @Optional.Method(modid = "appliedenergistics2")
    @SuppressWarnings("rawtypes")
    @Override
    public int getStatusForCell(final ItemStack is, final IMEInventory handler) {
        return 1;
    }

    @Optional.Method(modid = "appliedenergistics2")
    @SuppressWarnings("rawtypes")
    @Override
    public double cellIdleDrain(final ItemStack is, final IMEInventory handler) {
        return 0;
    }

    @Override
    public void loadConfig(final Configuration config) {
        try {
            capacity = Double.valueOf(config.get(GROUP, "Creative_Capacity", String.format("%.0f", capacity)).getString());
        } catch (final NumberFormatException e) {
            EquivalentEnergistics.logger.warn("Creative Storage Cell Creative_Capacity configured for invalid value! Default will be used!");
        }
    }

}
