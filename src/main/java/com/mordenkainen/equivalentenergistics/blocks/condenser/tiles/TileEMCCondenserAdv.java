package com.mordenkainen.equivalentenergistics.blocks.condenser.tiles;

import com.mordenkainen.equivalentenergistics.blocks.BlockEnum;
import com.mordenkainen.equivalentenergistics.blocks.condenser.BlockEMCCondenser;
import com.mordenkainen.equivalentenergistics.blocks.condenser.CondenserState;

import appeng.api.networking.IGridNode;
import appeng.api.networking.ticking.TickRateModulation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class TileEMCCondenserAdv extends TileEMCCondenserBase {

    public TileEMCCondenserAdv() {
        this(new ItemStack(Item.getItemFromBlock(BlockEnum.EMCCONDENSER.getBlock()), 1, 1));
    }

    public TileEMCCondenserAdv(final ItemStack repItem) {
        super(repItem);
    }

    @Override
    protected double getEMCPerTick() {
        return BlockEMCCondenser.emcPerTick * 10;
    }

    @Override
    public TickRateModulation tickingRequest(final IGridNode node, final int ticksSinceLast) {
        if (refreshNetworkState()) {
            markForUpdate();
        }
        
        if (isActive() && worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord)) {
            updateState(CondenserState.IDLE);
            return TickRateModulation.IDLE;
        }

        return super.tickingRequest(node, ticksSinceLast);
    }

    @Override
    protected boolean updateState(final CondenserState newState) {
        if (state != newState) {
            state = newState;
            worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, blockType);
            markForUpdate();
            return true;
        }
        return false;
    }

}
