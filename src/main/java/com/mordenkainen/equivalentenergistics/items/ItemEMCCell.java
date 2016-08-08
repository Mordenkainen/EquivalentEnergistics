package com.mordenkainen.equivalentenergistics.items;

import java.util.List;

import org.lwjgl.input.Keyboard;

import com.mordenkainen.equivalentenergistics.EquivalentEnergistics;
import com.mordenkainen.equivalentenergistics.config.ConfigManager;
import com.mordenkainen.equivalentenergistics.config.IConfigurable;
import com.mordenkainen.equivalentenergistics.integration.ae2.HandlerEMCCell;
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

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import net.minecraftforge.common.config.Configuration;

@Optional.InterfaceList({
	@Optional.Interface(iface = "moze_intel.projecte.api.item.IItemEmc", modid = "ProjectE"),
	@Optional.Interface(iface = "appeng.api.storage.ICellHandler", modid = "appliedenergistics2") // NOPMD
})
public class ItemEMCCell extends ItemBase implements ICellHandler, IConfigurable, IItemEmc {

	private static final String GROUP = "Storage Cells";
	private static final String EMC_TAG = "emc";
	private static final int NUM_CELLS = 8;
	
	private static float[] capacities = {1000000, 4000000, 16000000, 64000000, 256000000, 1024000000, 4096000000f, 16384000000f};
	private static double[] drain = {0.1, 0.2, 0.4, 0.8, 1.6, 3.2, 6.4, 12.8};
	
	public ItemEMCCell() {
		super();

		setMaxStackSize(1);
		setHasSubtypes(true);
	}
	
	// Item Overrides
	// ------------------------
	@Override
	@SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(final int damage) {
        return TextureEnum.EMCCELL.getTexture(damage);
    }
	
	@SuppressWarnings({ "rawtypes", "unchecked" }) // NOPMD
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(final Item item, final CreativeTabs tab, final List list) {
		for (int i = 0; i < NUM_CELLS; i++) {
			list.add(new ItemStack(item, 1, i));
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	@SideOnly(Side.CLIENT)
    public void addInformation(final ItemStack stack, final EntityPlayer player, final List list, final boolean param4) {
		if (isCell(stack)) {
			final float curEMC = hasEMCTag(stack) ? stack.getTagCompound().getFloat(EMC_TAG) : 0;
			
			if (Keyboard.isKeyDown(Keyboard.KEY_RSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
				list.add(StatCollector.translateToLocal("tooltip.emc.name") + " " + String.format("%.2f", curEMC) + " / " + String.format("%.2f", capacities[stack.getItemDamage()]));
			} else {
				list.add(StatCollector.translateToLocal("tooltip.emc.name") + " " + CommonUtils.formatEMC(curEMC) + " / " + CommonUtils.formatEMC(capacities[stack.getItemDamage()]));
			}
		}
	}
	
	@Override
	public String getUnlocalizedName(final ItemStack stack) {
		return super.getUnlocalizedName() + "." + stack.getItemDamage();
	}
	
	@Override
	public ItemStack onItemRightClick(final ItemStack stack, final World world, final EntityPlayer player) {
		if (stack == null || !player.isSneaking() || !isEmpty(stack)) {
			return stack;
		}
		
		if (player.inventory.addItemStackToInventory(ItemEnum.MISCITEM.getDamagedStack(0))) {
			return ItemEnum.CELLCOMPONENT.getDamagedStack(stack.getItemDamage());	
		}
		return stack;
	}

	// ------------------------

	// IConfigurable Overrides
	// ------------------------
	@Override
	public void loadConfig(final Configuration config) {
		for (int i = 0; i < NUM_CELLS; i++) {
			try {
				capacities[i] = Float.valueOf(config.get(GROUP, "Tier" + i + "_Capacity", String.format("%.0f", capacities[i])).getString());
			} catch (NumberFormatException e) {
				EquivalentEnergistics.logger.warn("Storage Cell Tier" + i + "_Capacity configured for invalid value! Default will be used!");
			}
			drain[i] = config.get(GROUP, "Tier_" + i + "_PowerDrain", drain[i]).getDouble(drain[i]);
		}		
	}
	// ------------------------
	
	// ICellHandler Overrides
	// ------------------------
	@Optional.Method(modid = "appliedenergistics2")
	@Override
	public boolean isCell(final ItemStack stack) {
		return stack != null && stack.getItem() == this;
	}

	@Optional.Method(modid = "appliedenergistics2")
	@SuppressWarnings("rawtypes") // NOPMD
	@Override
	public IMEInventoryHandler getCellInventory(final ItemStack stack, final ISaveProvider host, final StorageChannel channel) {
		if (channel == StorageChannel.ITEMS && isCell(stack)) {
			return new HandlerEMCCell(stack, host, capacities[stack.getItemDamage()]);
		}
		return null;
	}

	@Optional.Method(modid = "appliedenergistics2")
	@Override
	public IIcon getTopTexture_Light() {
		return null;
	}

	@Optional.Method(modid = "appliedenergistics2")
	@Override
	public IIcon getTopTexture_Medium() {
		return null;
	}

	@Optional.Method(modid = "appliedenergistics2")
	@Override
	public IIcon getTopTexture_Dark() {
		return null;
	}

	@Optional.Method(modid = "appliedenergistics2")
	@SuppressWarnings("rawtypes")
	@Override
	public void openChestGui(final EntityPlayer player, final IChestOrDrive chest, final ICellHandler handler, final IMEInventoryHandler inv, final ItemStack stack, final StorageChannel channel) {
		player.addChatMessage(new ChatComponentText(StatCollector.translateToLocal("message.cell.chestwarning")));
	}

	@Optional.Method(modid = "appliedenergistics2")
	@SuppressWarnings("rawtypes")
	@Override
	public int getStatusForCell(final ItemStack stack, final IMEInventory handler) {
		if (handler instanceof HandlerEMCCell) {
			return ((HandlerEMCCell) handler).getCellStatus();
		}
		return 0;
	}

	@Optional.Method(modid = "appliedenergistics2")
	@SuppressWarnings("rawtypes")
	@Override
	public double cellIdleDrain(final ItemStack stack, final IMEInventory handler) {
		return drain[stack.getItemDamage()];
	}
	// ------------------------
	
	// IItemEmc Overrides
	// ------------------------
	@Override
	public double addEmc(final ItemStack stack, final double toAdd) {

		if(ConfigManager.useEE3 || !isCell(stack)) {
			return 0;
		}
		
		final float currentEMC = hasEMCTag(stack) ? stack.getTagCompound().getFloat(EMC_TAG) : 0;
		final float amountToAdd = Math.min((float) toAdd, capacities[stack.getItemDamage()] - currentEMC);
		
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
	// ------------------------
	
	public float getStoredCellEMC(final ItemStack stack) {
		if (!isCell(stack) || !hasEMCTag(stack)) {
			return 0;
		}
		
		return stack.getTagCompound().getFloat(EMC_TAG);
	}
	
	public float extractCellEMC(final ItemStack stack, final float emc) {
		if (!isCell(stack) || !hasEMCTag(stack)) {
			return 0;
		}
		
		final float currentEMC = stack.getTagCompound().getFloat(EMC_TAG);
		final float toRemove = Math.min(emc, currentEMC);
		if (currentEMC - toRemove >= 0) {
			stack.stackTagCompound.removeTag(EMC_TAG);
			if (stack.stackTagCompound.hasNoTags()) {
				stack.stackTagCompound = null;
			}
		} else {
			stack.stackTagCompound.setFloat(EMC_TAG, currentEMC - toRemove);
		}
		return toRemove;
	}
	
	private boolean hasEMCTag(final ItemStack stack) {
		return stack != null && stack.hasTagCompound() && stack.getTagCompound().hasKey(EMC_TAG);
	}
	
	private boolean isEmpty(final ItemStack stack) {
		return !hasEMCTag(stack) || stack.getTagCompound().getFloat(EMC_TAG) <= 0;
	}
	
}
