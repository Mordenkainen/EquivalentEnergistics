package com.mordenkainen.equivalentenergistics.blocks.crafter.tiles;

import com.mordenkainen.equivalentenergistics.blocks.crafter.BlockEMCCrafter;
import com.mordenkainen.equivalentenergistics.integration.ae2.grid.GridUtils;
import com.mordenkainen.equivalentenergistics.integration.ae2.grid.IGridProxy;

import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.networking.security.MachineSource;
import net.minecraft.item.ItemStack;

public class CraftingJob {

    private double craftingTicks;
    private final ItemStack outputStack;
    private final IGridProxy proxy;
    private final MachineSource source;
    private boolean finished;

    public CraftingJob(final double craftingTicks, final ItemStack outputStack, final IGridProxy proxy, final MachineSource source) {
        this.craftingTicks = craftingTicks;
        this.outputStack = outputStack;
        this.proxy = proxy;
        this.source = source;
    }

    public boolean isFinished() {
        return finished;
    }

    public ItemStack getOutput() {
        return outputStack;
    }

    public void craftingTick() {
        if (craftingTicks <= 0) {
            final ItemStack rejected = GridUtils.injectItems(proxy, outputStack, Actionable.SIMULATE, source);

            if (rejected == null || rejected.stackSize == 0) {
                GridUtils.injectItems(proxy, outputStack, Actionable.MODULATE, source);
                finished = true;
            }
        } else {
            final double powerExtracted = GridUtils.extractAEPower(proxy, BlockEMCCrafter.activePower, Actionable.SIMULATE, PowerMultiplier.CONFIG);

            if (powerExtracted - BlockEMCCrafter.activePower >= 0.0D) {
                GridUtils.extractAEPower(proxy, BlockEMCCrafter.activePower, Actionable.MODULATE, PowerMultiplier.CONFIG);
                craftingTicks--;
            }
        }
    }

    public double getRemainingTicks() {
        return craftingTicks;
    }

}
