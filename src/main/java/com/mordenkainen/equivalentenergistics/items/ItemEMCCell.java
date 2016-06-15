package com.mordenkainen.equivalentenergistics.items;

import java.util.List;

import com.mordenkainen.equivalentenergistics.config.IConfigurable;
import com.mordenkainen.equivalentenergistics.integration.ae2.HandlerEMCCell;
import com.mordenkainen.equivalentenergistics.lib.Reference;
import com.mordenkainen.equivalentenergistics.registries.ItemEnum;
import com.mordenkainen.equivalentenergistics.registries.TextureEnum;
import com.mordenkainen.equivalentenergistics.util.CommonUtils;

import appeng.api.AEApi;
import appeng.api.implementations.tiles.IChestOrDrive;
import appeng.api.storage.ICellHandler;
import appeng.api.storage.IMEInventory;
import appeng.api.storage.IMEInventoryHandler;
import appeng.api.storage.ISaveProvider;
import appeng.api.storage.StorageChannel;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.config.Configuration;

public class ItemEMCCell extends Item implements ICellHandler, IConfigurable {

	private static float[] capacities = {1000000, 4000000, 16000000, 64000000};
	private static double[] drain = {0.1, 0.2, 0.4, 0.8};
	private static final String GROUP = "Storage Cells";
	
	public ItemEMCCell() {
		super();
		
		AEApi.instance().registries().cell().addCellHandler(this);

		setMaxStackSize(1);
		setHasSubtypes(true);
	}
	
	@Override
	public boolean isCell(final ItemStack stack) {
		return stack.getItem() == this;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public IMEInventoryHandler getCellInventory(final ItemStack stack, final ISaveProvider host, final StorageChannel channel) {
		if (channel != StorageChannel.ITEMS || !(stack.getItem() instanceof ItemEMCCell)) {
			return null;
		}
		
		return new HandlerEMCCell(stack, host, capacities[stack.getItemDamage()]);
	}

	@Override
	public IIcon getTopTexture_Light() {
		return null;
	}

	@Override
	public IIcon getTopTexture_Medium() {
		return null;
	}

	@Override
	public IIcon getTopTexture_Dark() {
		return null;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void openChestGui(final EntityPlayer player, final IChestOrDrive chest, final ICellHandler cellHandler, final IMEInventoryHandler inv, final ItemStack is, final StorageChannel chan) {}

	@SuppressWarnings("rawtypes")
	@Override
	public int getStatusForCell(final ItemStack is, final IMEInventory handler) {
		if (handler instanceof HandlerEMCCell) {
			return ((HandlerEMCCell) handler).getCellStatus();
		}
		return 0;
	}

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
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(final Item item, final CreativeTabs tab, final List list) {
		for (int i = 0; i < 4; i++) {
			final ItemStack stack = new ItemStack(item, 1, i);
			list.add(stack);
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	@SideOnly(Side.CLIENT)
    public void addInformation(final ItemStack stack, final EntityPlayer player, final List list, final boolean param4) {
		if (stack != null && stack.getItem() == ItemEnum.EMCCELL.getItem()) {
			float curEMC = 0;
			if (stack.hasTagCompound() && stack.stackTagCompound.hasKey("emc")) {
				curEMC = stack.stackTagCompound.getFloat("emc");
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
		for(int i = 0; i < 4; i++) {
			capacities[i] = (float) config.get(GROUP, "Tier" + i + "_Capacity", capacities[i]).getDouble(capacities[i]);
			drain[i] = config.get(GROUP, "Tier_" + i + "_PowerDrain", drain[i]).getDouble(drain[i]);
		}		
	}
	
}
