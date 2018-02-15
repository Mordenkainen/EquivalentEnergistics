package com.mordenkainen.equivalentenergistics.items;

import java.util.List;

import com.mordenkainen.equivalentenergistics.core.Names;
import com.mordenkainen.equivalentenergistics.core.config.EqEConfig;
import com.mordenkainen.equivalentenergistics.integration.ae2.cells.HandlerEMCCell;
import com.mordenkainen.equivalentenergistics.integration.ae2.cells.HandlerEMCCellBase;
import com.mordenkainen.equivalentenergistics.util.CommonUtils;

import appeng.api.storage.IMEInventory;
import appeng.api.storage.IMEInventoryHandler;
import appeng.api.storage.ISaveProvider;
import appeng.api.storage.StorageChannel;
import moze_intel.projecte.api.item.IItemEmc;
import net.minecraft.client.resources.I18n;
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
    private static final double CELL_CAPACITIES[] = {EqEConfig.cellCapacities.tier1Cell, EqEConfig.cellCapacities.tier2Cell, EqEConfig.cellCapacities.tier3Cell, EqEConfig.cellCapacities.tier4Cell, EqEConfig.cellCapacities.tier5Cell, EqEConfig.cellCapacities.tier6Cell, EqEConfig.cellCapacities.tier7Cell, EqEConfig.cellCapacities.tier8Cell};
    private static final double CELL_DRAINS[] = {EqEConfig.cellPowerDrain.tier1Cell, EqEConfig.cellPowerDrain.tier2Cell, EqEConfig.cellPowerDrain.tier3Cell, EqEConfig.cellPowerDrain.tier4Cell, EqEConfig.cellPowerDrain.tier5Cell, EqEConfig.cellPowerDrain.tier6Cell, EqEConfig.cellPowerDrain.tier7Cell, EqEConfig.cellPowerDrain.tier8Cell};

    public ItemEMCCell() {
        super(Names.CELL, 8);
    }

    @Override
    public EnumRarity getRarity(final ItemStack stack) {
        return EnumRarity.values()[stack.getMetadata() / 2];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(final ItemStack stack, final EntityPlayer player, final List<String> tooltip, final boolean flag) {
        tooltip.add(I18n.format("message.cell.capacity", new Object[0]) + " " + CommonUtils.formatEMC(getMaximumEmc(stack)));
    }
    
    @Override
    public ActionResult<ItemStack> onItemRightClick(final ItemStack stack, final World world, final EntityPlayer player, final EnumHand hand) {
        if (player != null && isEmpty(player.getHeldItem(hand)) && player.isSneaking() && player.inventory.addItemStackToInventory(new ItemStack(ModItems.MISC, 1, 0))) {
            return new ActionResult<ItemStack>(EnumActionResult.PASS, new ItemStack(ModItems.COMPONENT, 1, player.getHeldItem(hand).getItemDamage()));
        }

        return new ActionResult<ItemStack>(EnumActionResult.FAIL, player.getHeldItem(hand));
    }
    
    @SuppressWarnings({ "rawtypes" })
    @Override
    public IMEInventoryHandler getCellInventory(final ItemStack stack, final ISaveProvider host, final StorageChannel channel) {
        if (channel == StorageChannel.ITEMS && isCell(stack)) {
            return new HandlerEMCCell(stack, host, CELL_CAPACITIES[stack.getItemDamage()]);
        }
        return null;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public int getStatusForCell(final ItemStack stack, final IMEInventory handler) {
        return handler instanceof HandlerEMCCellBase ? ((HandlerEMCCellBase) handler).getCellStatus() : 0;
    }

    
    @SuppressWarnings("rawtypes")
    @Override
    public double cellIdleDrain(final ItemStack stack, final IMEInventory handler) {
        return CELL_DRAINS[stack.getItemDamage()];
    }

    public double getStoredCellEMC(final ItemStack stack) {
        if (!isCell(stack) || !hasEMCTag(stack)) {
            return 0;
        }

        return Math.max(stack.getTagCompound().getDouble(EMC_TAG), 0);
    }

    @Override
    public double addEmc(final ItemStack stack, final double toAdd) {
        final double currentEMC = getStoredCellEMC(stack);
        final double amountToAdd = Math.min(toAdd, CELL_CAPACITIES[stack.getItemDamage()] - currentEMC);

        if (amountToAdd > 0) {
            if (!stack.hasTagCompound()) {
                stack.setTagCompound(new NBTTagCompound());
            }
            stack.getTagCompound().setDouble(EMC_TAG, currentEMC + amountToAdd);
        }

        return amountToAdd;
    }

    @Override
    public double extractEmc(final ItemStack stack, final double emc) {
        final double currentEMC = getStoredCellEMC(stack);
        final double toRemove = Math.min(emc, currentEMC);

        if (hasEMCTag(stack)) {
            stack.getTagCompound().setDouble(EMC_TAG, currentEMC - toRemove);
            if (isEmpty(stack)) {
                removeEMCTag(stack);
            }
        }

        return toRemove;
    }

    @Override
    public double getStoredEmc(final ItemStack stack) {
        return getStoredCellEMC(stack);
    }
    
    @Override
    public double getMaximumEmc(final ItemStack stack) {
        return CELL_CAPACITIES[stack.getItemDamage()];
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
