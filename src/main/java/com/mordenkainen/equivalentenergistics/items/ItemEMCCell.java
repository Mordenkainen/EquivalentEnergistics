package com.mordenkainen.equivalentenergistics.items;

import java.util.List;

import javax.annotation.Nullable;

import com.mordenkainen.equivalentenergistics.core.Names;
import com.mordenkainen.equivalentenergistics.core.config.EqEConfig;
import com.mordenkainen.equivalentenergistics.integration.ae2.cells.HandlerEMCCell;
import com.mordenkainen.equivalentenergistics.integration.ae2.cells.HandlerEMCCellBase;
import com.mordenkainen.equivalentenergistics.util.CommonUtils;

import appeng.api.AEApi;
import appeng.api.storage.IMEInventory;
import appeng.api.storage.IMEInventoryHandler;
import appeng.api.storage.ISaveProvider;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEStack;
import moze_intel.projecte.api.item.IItemEmc;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemEMCCell extends ItemCellBase implements IItemEmc {

	private static final String EMC_TAG = "emc";
	private static final float cellCapacities[] = {EqEConfig.cellCapacities.tier1_Cell, EqEConfig.cellCapacities.tier2_Cell, EqEConfig.cellCapacities.tier3_Cell, EqEConfig.cellCapacities.tier4_Cell, EqEConfig.cellCapacities.tier5_Cell, EqEConfig.cellCapacities.tier6_Cell, EqEConfig.cellCapacities.tier7_Cell, EqEConfig.cellCapacities.tier8_Cell};
	private static final double cellDrains[] = {EqEConfig.cellPowerDrain.tier1_Cell, EqEConfig.cellPowerDrain.tier2_Cell, EqEConfig.cellPowerDrain.tier3_Cell, EqEConfig.cellPowerDrain.tier4_Cell, EqEConfig.cellPowerDrain.tier5_Cell, EqEConfig.cellPowerDrain.tier6_Cell, EqEConfig.cellPowerDrain.tier7_Cell, EqEConfig.cellPowerDrain.tier8_Cell};
	
	public ItemEMCCell() {
		super(Names.CELL, 8);
	}

	@Override
    public EnumRarity getRarity(final ItemStack stack) {
        return EnumRarity.values()[stack.getItemDamage() / 2];
    }
	
	@Override
	@SideOnly(Side.CLIENT)
    public void addInformation(final ItemStack stack, final @Nullable World world, final List<String> tooltip, final ITooltipFlag flag) {
		 tooltip.add(I18n.format("message.cell.capacity", new Object[0]) + " " + CommonUtils.formatEMC((float) getMaximumEmc(stack)));
	}

	@Override
	public <T extends IAEStack<T>> double cellIdleDrain(final ItemStack stack, final IMEInventory<T> handler) {
		return cellDrains[stack.getItemDamage()];
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends IAEStack<T>> IMEInventoryHandler<T> getCellInventory(final ItemStack stack, final ISaveProvider host, final IStorageChannel<T> channel) {
		if (channel == AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class) && isCell(stack)) {
            return (IMEInventoryHandler<T>) new HandlerEMCCell(stack, host, cellCapacities[stack.getItemDamage()]);
        }
        return null;
	}

	@Override
	public <T extends IAEStack<T>> int getStatusForCell(final ItemStack stack, final IMEInventory<T> handler) {
		return handler instanceof HandlerEMCCellBase ? ((HandlerEMCCellBase) handler).getCellStatus() : 0;
	}
	
	private boolean isEmpty(final ItemStack stack) {
        return getStoredCellEMC(stack) == 0;
    }
	
	public float getStoredCellEMC(final ItemStack stack) {
        if (!isCell(stack) || !hasEMCTag(stack)) {
            return 0;
        }

        return Math.max(stack.getTagCompound().getFloat(EMC_TAG), 0);
    }
	
	private boolean hasEMCTag(final ItemStack stack) {
        return stack != null && stack.hasTagCompound() && stack.getTagCompound().hasKey(EMC_TAG);
    }
	
	public ActionResult<ItemStack> onItemRightClick(final World world, final EntityPlayer player, final EnumHand hand) {
		if (player != null && isEmpty(player.getHeldItem(hand)) && player.isSneaking() && player.inventory.addItemStackToInventory(new ItemStack(ModItems.MISC, 1, 0))) {
            return new ActionResult<ItemStack>(EnumActionResult.PASS, new ItemStack(ModItems.COMPONENT, 1, player.getHeldItem(hand).getItemDamage()));
        }
		
        return new ActionResult<ItemStack>(EnumActionResult.FAIL, player.getHeldItem(hand));
    }

	@Override
	public double addEmc(final ItemStack stack, final double toAdd) {
		final float currentEMC = getStoredCellEMC(stack);
        final float amountToAdd = Math.min((float) toAdd, cellCapacities[stack.getItemDamage()] - currentEMC);

        if (amountToAdd > 0) {
            if (!stack.hasTagCompound()) {
                stack.setTagCompound(new NBTTagCompound());
            }
            stack.getTagCompound().setFloat(EMC_TAG, currentEMC + amountToAdd);
        }

        return amountToAdd;
	}

	@Override
	public double extractEmc(final ItemStack stack, final double emc) {
		final float currentEMC = getStoredCellEMC(stack);
        final float toRemove = (float) Math.min(emc, currentEMC);

        if (hasEMCTag(stack)) {
            stack.getTagCompound().setFloat(EMC_TAG, currentEMC - toRemove);
            if (isEmpty(stack)) {
                stack.getTagCompound().removeTag(EMC_TAG);
                if (stack.getTagCompound().hasNoTags()) {
                    stack.setTagCompound(null);
                }
            }
        }

        return toRemove;
	}

	@Override
	public double getMaximumEmc(final ItemStack stack) {
		return cellCapacities[stack.getItemDamage()];
	}

	@Override
	public double getStoredEmc(final ItemStack stack) {
		return getStoredCellEMC(stack);
	}
}
