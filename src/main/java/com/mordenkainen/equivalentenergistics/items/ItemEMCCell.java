package com.mordenkainen.equivalentenergistics.items;

import java.util.List;

import org.lwjgl.input.Keyboard;

import com.mordenkainen.equivalentenergistics.EquivalentEnergistics;
import com.mordenkainen.equivalentenergistics.core.config.ConfigManager;
import com.mordenkainen.equivalentenergistics.core.config.IConfigurable;
import com.mordenkainen.equivalentenergistics.core.textures.TextureEnum;
import com.mordenkainen.equivalentenergistics.integration.ae2.HandlerEMCCell;
import com.mordenkainen.equivalentenergistics.integration.ae2.HandlerEMCCellBase;
import com.mordenkainen.equivalentenergistics.util.CommonUtils;

import appeng.api.storage.IMEInventory;
import appeng.api.storage.IMEInventoryHandler;
import appeng.api.storage.ISaveProvider;
import appeng.api.storage.StorageChannel;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import moze_intel.projecte.api.item.IItemEmc;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;

@Optional.Interface(iface = "moze_intel.projecte.api.item.IItemEmc", modid = "ProjectE")
public class ItemEMCCell extends ItemEMCCellBase implements IConfigurable, IItemEmc {

    private static final String GROUP = "Storage Cells";
    private static final String EMC_TAG = "emc";

    private static final float[] CAPACITIES = { 1000000, 4000000, 16000000, 64000000, 256000000, 1024000000, 4096000000f, 16384000000f };
    private static final double[] DRAIN = { 0.1, 0.2, 0.4, 0.8, 1.6, 3.2, 6.4, 12.8 };

    public ItemEMCCell() {
        super(8);
    }

    @Override
    public EnumRarity getRarity(final ItemStack stack) {
        return EnumRarity.values()[stack.getItemDamage() / 2];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(final int damage) {
        return TextureEnum.EMCCELL.getTexture(damage);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" }) // NOPMD
    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(final ItemStack stack, final EntityPlayer player, final List list, final boolean param4) {
        final float curEMC = getStoredCellEMC(stack);
        final String tooltip = StatCollector.translateToLocal("tooltip.emc.name") + " ";
        if (Keyboard.isKeyDown(Keyboard.KEY_RSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
            list.add(tooltip + String.format("%.2f", curEMC) + " / " + String.format("%.2f", CAPACITIES[stack.getItemDamage()]));
        } else {
            list.add(tooltip + CommonUtils.formatEMC(curEMC) + " / " + CommonUtils.formatEMC(CAPACITIES[stack.getItemDamage()]));
        }
    }

    @Override
    public ItemStack onItemRightClick(final ItemStack stack, final World world, final EntityPlayer player) {
        if (isEmpty(stack) && player != null && player.isSneaking() && player.inventory.addItemStackToInventory(ItemEnum.MISCITEM.getDamagedStack(0))) {
            return ItemEnum.CELLCOMPONENT.getDamagedStack(stack.getItemDamage());
        }

        return stack;
    }

    @Override
    public void loadConfig(final Configuration config) {
        for (int i = 0; i < itemCount; i++) {
            try {
                CAPACITIES[i] = Float.valueOf(config.get(GROUP, "Tier" + i + "_Capacity", String.format("%.0f", CAPACITIES[i])).getString());
            } catch (final NumberFormatException e) {
                EquivalentEnergistics.logger.warn("Storage Cell Tier" + i + "_Capacity configured for invalid value! Default will be used!");
            }
            DRAIN[i] = config.get(GROUP, "Tier_" + i + "_PowerDrain", DRAIN[i]).getDouble(DRAIN[i]);
        }
    }

    @Optional.Method(modid = "appliedenergistics2")
    @SuppressWarnings("rawtypes") // NOPMD
    @Override
    public IMEInventoryHandler getCellInventory(final ItemStack stack, final ISaveProvider host, final StorageChannel channel) {
        if (channel == StorageChannel.ITEMS && isCell(stack)) {
            return new HandlerEMCCell(stack, host, CAPACITIES[stack.getItemDamage()]);
        }
        return null;
    }

    @Optional.Method(modid = "appliedenergistics2")
    @SuppressWarnings("rawtypes")
    @Override
    public int getStatusForCell(final ItemStack stack, final IMEInventory handler) {
        return handler instanceof HandlerEMCCellBase ? ((HandlerEMCCellBase) handler).getCellStatus() : 0;
    }

    @Optional.Method(modid = "appliedenergistics2")
    @SuppressWarnings("rawtypes")
    @Override
    public double cellIdleDrain(final ItemStack stack, final IMEInventory handler) {
        return DRAIN[stack.getItemDamage()];
    }
    
    public float getStoredCellEMC(final ItemStack stack) {
        if (!isCell(stack) || !hasEMCTag(stack)) {
            return 0;
        }

        return Math.max(stack.getTagCompound().getFloat(EMC_TAG), 0);
    }

    @Override
    public double addEmc(final ItemStack stack, final double toAdd) {
        if (ConfigManager.useEE3) {
            return 0;
        }

        final float currentEMC = getStoredCellEMC(stack);
        final float amountToAdd = Math.min((float) toAdd, CAPACITIES[stack.getItemDamage()] - currentEMC);

        if (amountToAdd > 0) {
            if (!stack.hasTagCompound()) {
                stack.setTagCompound(new NBTTagCompound());
            }
            stack.getTagCompound().setFloat(EMC_TAG, currentEMC + amountToAdd);
        }

        return amountToAdd;
    }

    @Override
    public double extractEmc(final ItemStack stack, final double toRemove) {
        return ConfigManager.useEE3 ? 0 : extractCellEMC(stack, (float) toRemove);
    }

    @Override
    public double getStoredEmc(final ItemStack stack) {
        return ConfigManager.useEE3 ? 0 : getStoredCellEMC(stack);
    }

    @Override
    public double getMaximumEmc(final ItemStack stack) {
        return ConfigManager.useEE3 ? 0 : CAPACITIES[stack.getItemDamage()];
    }

    public float extractCellEMC(final ItemStack stack, final float emc) {
        final float currentEMC = getStoredCellEMC(stack);
        final float toRemove = Math.min(emc, currentEMC);

        if (hasEMCTag(stack)) {
            stack.getTagCompound().setFloat(EMC_TAG, currentEMC - toRemove);
            if (isEmpty(stack)) {
                removeEMCTag(stack);
            }
        }

        return toRemove;
    }
    
    private void removeEMCTag(final ItemStack stack) {
        stack.getTagCompound().removeTag(EMC_TAG);
        if (stack.getTagCompound().hasNoTags()) {
            stack.setTagCompound(null);
        }
    }

    private boolean hasEMCTag(final ItemStack stack) {
        return stack != null && stack.hasTagCompound() && stack.getTagCompound().hasKey(EMC_TAG);
    }

    private boolean isEmpty(final ItemStack stack) {
        return getStoredCellEMC(stack) == 0;
    }

}
