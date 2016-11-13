package com.mordenkainen.equivalentenergistics.tiles;

import com.mordenkainen.equivalentenergistics.blocks.BlockEMCCondenser;
import com.mordenkainen.equivalentenergistics.integration.Integration;
import com.mordenkainen.equivalentenergistics.integration.ae2.grid.GridAccessException;
import com.mordenkainen.equivalentenergistics.registries.BlockEnum;
import com.mordenkainen.equivalentenergistics.util.CommonUtils;

import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.networking.energy.IEnergyGrid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class TileEMCCondenser extends TileCondenserAEBase {
    public static final int SLOT_COUNT = 4;

    public TileEMCCondenser() {
        super(new ItemStack(Item.getItemFromBlock(BlockEnum.EMCCONDENSER.getBlock())));
    }

    @Override
    protected float getEMCPerTick() {
        return BlockEMCCondenser.emcPerTick;
    }

    @Override
    protected int getSlotCount() {
        return SLOT_COUNT;
    }

    @Override
    protected int getMaxItems() {
        return BlockEMCCondenser.itemsPerTick;
    }

    @Override
    protected float getMaxEMC() {
        return Float.MAX_VALUE;
    }

    @Override
    protected void consumePower(final ItemStack stack, final int count) {
        IEnergyGrid eGrid;
        try {
            eGrid = getProxy().getEnergy();
            final double powerRequired = Integration.emcHandler.getSingleEnergyValue(stack) * count * BlockEMCCondenser.activePower;
            eGrid.extractAEPower(powerRequired, Actionable.MODULATE, PowerMultiplier.ONE);
        } catch (final GridAccessException e) {
            CommonUtils.debugLog("consumePower: Error accessing grid:", e);
        }
    }

    @Override
    protected int getMaxItemsForPower(final ItemStack stack) {
        try {
            final IEnergyGrid eGrid = getProxy().getEnergy();
            final double powerRequired = Integration.emcHandler.getSingleEnergyValue(stack) * stack.stackSize * BlockEMCCondenser.activePower;
            final double powerAvail = eGrid.extractAEPower(powerRequired, Actionable.SIMULATE, PowerMultiplier.ONE);
            return (int) (powerAvail / (Integration.emcHandler.getSingleEnergyValue(stack) * BlockEMCCondenser.activePower));
        } catch (final GridAccessException e) {
            CommonUtils.debugLog("getMaxItemsForPower: Error accessing grid:", e);
            return 0;
        }
    }

}
