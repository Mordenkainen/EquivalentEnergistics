package com.mordenkainen.equivalentenergistics.blocks.provider.tile;

import com.mordenkainen.equivalentenergistics.blocks.BlockEnum;
import com.mordenkainen.equivalentenergistics.integration.ae2.EMCCraftingPattern;
import com.mordenkainen.equivalentenergistics.integration.ae2.grid.GridAccessException;
import com.mordenkainen.equivalentenergistics.integration.ae2.grid.GridUtils;
import com.mordenkainen.equivalentenergistics.integration.ae2.tiles.TileAEBase;

import appeng.api.networking.IGridNode;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.networking.crafting.ICraftingProvider;
import appeng.api.networking.crafting.ICraftingProviderHelper;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class TileEMCPatternProvider extends TileAEBase implements IGridTickable, ICraftingProvider {

    public TileEMCPatternProvider() {
        super(new ItemStack(Item.getItemFromBlock(BlockEnum.EMCPROVIDER.getBlock()), 1));
        gridProxy.setIdlePowerUsage(10);
    }

    @Override
    public TickingRequest getTickingRequest(final IGridNode node) {
        return new TickingRequest(1, 20, false, true);
    }

    @Override
    public TickRateModulation tickingRequest(final IGridNode node, final int ticksSinceLast) {
        if (refreshNetworkState()) {
            markForUpdate();
        }
        return TickRateModulation.SAME;
    }

    @Override
    public boolean pushPattern(ICraftingPatternDetails patternDetails, InventoryCrafting table) {
        if(patternDetails instanceof EMCCraftingPattern) {
            EMCCraftingPattern pattern = (EMCCraftingPattern) patternDetails;
            try {
                return GridUtils.getEMCCrafting(getProxy()).addJob(pattern.getOutputs()[0].getItemStack(), pattern.inputEMC, pattern.outputEMC);
            } catch (GridAccessException e) {}
        }
        return false;
    }

    @Override
    public boolean isBusy() {
        try {
            return GridUtils.getEMCCrafting(getProxy()).allCraftersBusy();
        } catch (GridAccessException e) {
            return true;
        }
    }

    @Override
    public void provideCrafting(final ICraftingProviderHelper craftingTracker) {
        GridUtils.addPatterns(getProxy(), this, craftingTracker);
    }
    
}
