package com.mordenkainen.equivalentenergistics.items;

import java.util.List;

import com.mordenkainen.equivalentenergistics.config.ConfigManager;
import com.mordenkainen.equivalentenergistics.config.IConfigurable;
import com.mordenkainen.equivalentenergistics.integration.ae2.HandlerEMCCell;
import com.mordenkainen.equivalentenergistics.lib.Reference;
import com.mordenkainen.equivalentenergistics.registries.ItemEnum;
import com.mordenkainen.equivalentenergistics.registries.TextureEnum;
import com.mordenkainen.equivalentenergistics.util.CommonUtils;

import appeng.api.implementations.tiles.IChestOrDrive;
import appeng.api.storage.ICellHandler;
import appeng.api.storage.IMEInventory;
import appeng.api.storage.IMEInventoryHandler;
import appeng.api.storage.ISaveProvider;
import appeng.api.storage.StorageChannel;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import cpw.mods.fml.common.Optional;

import moze_intel.projecte.api.item.IItemEmc;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;

import net.minecraftforge.common.config.Configuration;

@Optional.InterfaceList({
	@Optional.Interface(iface = "moze_intel.projecte.api.item.IItemEmc", modid = "ProjectE"),
	@Optional.Interface(iface = "appeng.api.storage.ICellHandler", modid = "appliedenergistics2") // NOPMD
})
public class ItemEMCCell extends Item implements ICellHandler, IConfigurable, IItemEmc {

	private static float[] capacities = {1000000, 4000000, 16000000, 64000000};
	private static double[] drain = {0.1, 0.2, 0.4, 0.8};
	private static final String GROUP = "Storage Cells";
	private static final String EMC_TAG = "emc";
	
	public ItemEMCCell() {
		super();

		setMaxStackSize(1);
		setHasSubtypes(true);
	}
	
	@Optional.Method(modid = "appliedenergistics2") // NOPMD
	@Override
	public boolean isCell(final ItemStack stack) {
		return stack.getItem() == this;
	}

	@Optional.Method(modid = "appliedenergistics2") // NOPMD
	@SuppressWarnings("rawtypes") // NOPMD
	@Override
	public IMEInventoryHandler getCellInventory(final ItemStack stack, final ISaveProvider host, final StorageChannel channel) {
		if (channel != StorageChannel.ITEMS || !(stack.getItem() instanceof ItemEMCCell)) {
			return null;
		}
		
		return new HandlerEMCCell(stack, host, capacities[stack.getItemDamage()]);
	}

	@Optional.Method(modid = "appliedenergistics2") // NOPMD
	@Override
	public IIcon getTopTexture_Light() {
		return null;
	}

	@Optional.Method(modid = "appliedenergistics2") // NOPMD
	@Override
	public IIcon getTopTexture_Medium() {
		return null;
	}

	@Optional.Method(modid = "appliedenergistics2") // NOPMD
	@Override
	public IIcon getTopTexture_Dark() {
		return null;
	}

	@Optional.Method(modid = "appliedenergistics2") // NOPMD
	@SuppressWarnings("rawtypes")
	@Override
	public void openChestGui(final EntityPlayer player, final IChestOrDrive chest, final ICellHandler cellHandler, final IMEInventoryHandler inv, final ItemStack is, final StorageChannel chan) {}

	@Optional.Method(modid = "appliedenergistics2") // NOPMD
	@SuppressWarnings("rawtypes")
	@Override
	public int getStatusForCell(final ItemStack is, final IMEInventory handler) {
		if (handler instanceof HandlerEMCCell) {
			return ((HandlerEMCCell) handler).getCellStatus();
		}
		return 0;
	}

	@Optional.Method(modid = "appliedenergistics2") // NOPMD
	@SuppressWarnings("rawtypes")
	@Override
	public double cellIdleDrain(final ItemStack is, final IMEInventory handler) {
		return drain[is.getItemDamage()];
	}

	@Override
	public void registerIcons(final IIconRegister reg) {}
	
	@Override
	@SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(final int damage) {
        return TextureEnum.EMCCELL.getTexture(damage);
    }
	
	@SuppressWarnings({ "rawtypes", "unchecked" }) // NOPMD
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(final Item item, final CreativeTabs tab, final List list) {
		for (int i = 0; i < 4; i++) {
			final ItemStack stack = new ItemStack(item, 1, i);
			list.add(stack);
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" }) // NOPMD
	@Override
	@SideOnly(Side.CLIENT)
    public void addInformation(final ItemStack stack, final EntityPlayer player, final List list, final boolean param4) {
		if (stack != null && stack.getItem() == ItemEnum.EMCCELL.getItem()) {
			float curEMC = 0;
			if (stack.hasTagCompound() && stack.stackTagCompound.hasKey(EMC_TAG)) {
				curEMC = stack.stackTagCompound.getFloat(EMC_TAG);
			}
			
			list.add("EMC: " + CommonUtils.formatEMC(curEMC) + " / " + CommonUtils.formatEMC(capacities[stack.getItemDamage()]));
		}
	}
	
	@Override
	public String getUnlocalizedName(final ItemStack stack) {
		return "item." + Reference.MOD_ID + ":" + "EMCCell." + stack.getItemDamage();
	}

	@Override
	public void loadConfig(final Configuration config) {
		for (int i = 0; i < 4; i++) {
			capacities[i] = (float) config.get(GROUP, "Tier" + i + "_Capacity", capacities[i]).getDouble(capacities[i]);
			drain[i] = config.get(GROUP, "Tier_" + i + "_PowerDrain", drain[i]).getDouble(drain[i]);
		}		
	}
	
	public float getStoredCellEMC(final ItemStack stack) {
		if (stack == null || stack.getItem() != this || !stack.hasTagCompound() || !stack.stackTagCompound.hasKey(EMC_TAG)) {
			return 0;
		}
		
		return stack.stackTagCompound.getFloat(EMC_TAG);
	}
	
	public float extractCellEMC(final ItemStack stack, final float emc) {
		if (stack == null || stack.getItem() != this || !stack.hasTagCompound() || !stack.stackTagCompound.hasKey(EMC_TAG)) {
			return 0;
		}
		
		final float currentEMC = stack.stackTagCompound.getFloat(EMC_TAG);
		final float toRemove = Math.min(emc, currentEMC);
		stack.stackTagCompound.setFloat(EMC_TAG, currentEMC - toRemove);
		return toRemove;
	}

	@Override
	public double addEmc(final ItemStack stack, final double toAdd) {
		if(ConfigManager.useEE3) {
			return 0;
		}
		
		if (stack == null || stack.getItem() != this) {
			return 0;
		}
		
		if (!stack.hasTagCompound()) {
			stack.stackTagCompound = new NBTTagCompound();
		}
		final float currentEMC = stack.stackTagCompound.getFloat(EMC_TAG);
		final float amountToAdd = Math.min((float) toAdd, capacities[stack.getItemDamage()] - currentEMC);
		
		stack.stackTagCompound.setFloat(EMC_TAG, currentEMC + amountToAdd);
		
		return amountToAdd;
	}

	@Override
	public double extractEmc(final ItemStack stack, final double toRemove) {
		if(ConfigManager.useEE3) {
			return 0;
		}
		return extractCellEMC(stack, (float) toRemove);
	}

	@Override
	public double getStoredEmc(final ItemStack stack) {
		if(ConfigManager.useEE3) {
			return 0;
		}
		return getStoredCellEMC(stack);
	}

	@Override
	public double getMaximumEmc(final ItemStack stack) {
		if(ConfigManager.useEE3) {
			return 0;
		}
		return capacities[stack.getItemDamage()];
	}
	
}
