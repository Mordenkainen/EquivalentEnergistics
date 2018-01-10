package com.mordenkainen.equivalentenergistics.blocks.condenser.tiles;

import com.mordenkainen.equivalentenergistics.blocks.ModBlocks;
import com.mordenkainen.equivalentenergistics.blocks.condenser.CondenserState;
import com.mordenkainen.equivalentenergistics.core.config.EqEConfig;

import appeng.api.networking.IGridNode;
import appeng.api.networking.ticking.TickRateModulation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class TileEMCCondenserAdv extends TileEMCCondenser {

	public TileEMCCondenserAdv() {
		super(new ItemStack(Item.getItemFromBlock(ModBlocks.CONDENSER), 1, 1));
	}
	
	public TileEMCCondenserAdv(ItemStack repItem) {
		super(repItem);
	}

	@Override
	protected float getEMCPerTick() {
        return EqEConfig.emcCondenser.emcPerTick * 10;
    }

	@Override
    public TickRateModulation tickingRequest(final IGridNode node, final int ticksSinceLast) {
        if (refreshNetworkState()) {
            markForUpdate();
        }
        
        if (isActive()) {
            if (getWorld().isBlockIndirectlyGettingPowered(pos) > 0) {
                updateState(CondenserState.IDLE);
                return TickRateModulation.IDLE;
            }
        }

        return super.tickingRequest(node, ticksSinceLast);
    }
	
}
