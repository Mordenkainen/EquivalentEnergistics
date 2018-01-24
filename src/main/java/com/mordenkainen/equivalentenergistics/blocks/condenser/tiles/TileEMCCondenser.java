package com.mordenkainen.equivalentenergistics.blocks.condenser.tiles;

import com.mordenkainen.equivalentenergistics.blocks.BlockEnum;
import com.mordenkainen.equivalentenergistics.blocks.condenser.BlockEMCCondenser;
import com.mordenkainen.equivalentenergistics.blocks.condenser.CondenserState;
import com.mordenkainen.equivalentenergistics.integration.ae2.grid.GridUtils;
import com.mordenkainen.equivalentenergistics.integration.waila.IWailaNBTProvider;

import appeng.api.config.Actionable;
import appeng.api.networking.IGridNode;
import appeng.api.networking.ticking.TickRateModulation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class TileEMCCondenser extends TileEMCCondenserBase implements IWailaNBTProvider {

    private final static String EMC_TAG = "CurrentEMC";

    protected double currentEMC;

    public TileEMCCondenser() {
        super(new ItemStack(Item.getItemFromBlock(BlockEnum.EMCCONDENSER.getBlock())));
    }

    @Override
    public NBTTagCompound getWailaTag(final NBTTagCompound tag) {
        if (currentEMC > 0) {
            tag.setDouble(EMC_TAG, currentEMC);
        }
        return tag;
    }

    @Override
    public void readFromNBT(final NBTTagCompound data) {
        super.readFromNBT(data);
        if (data.hasKey(EMC_TAG)) {
            currentEMC = data.getDouble(EMC_TAG);
        }
    }

    @Override
    public void writeToNBT(final NBTTagCompound data) {
        super.writeToNBT(data);
        if (currentEMC > 0) {
            data.setDouble(EMC_TAG, currentEMC);
        }
    }

    @Override
    public TickRateModulation tickingRequest(final IGridNode node, final int ticksSinceLast) {
        if (refreshNetworkState()) {
            markForUpdate();
        }
        
        if (isActive()) {   
            CondenserState newState = state;
    
            if (currentEMC > 0) {
                newState = injectExcessEMC();
                if (updateState(newState)) {
                    return TickRateModulation.IDLE;
                }
            }
            
            if (getInventory().isEmpty()) {
                updateState(CondenserState.IDLE);
            } else {
                newState = processInv();
                updateState(newState);
            }
        } else {
            updateState(CondenserState.IDLE);
        }

        return state.getTickRate();
    }

    private CondenserState injectExcessEMC() {
        currentEMC -= GridUtils.injectEMC(getProxy(), currentEMC, Actionable.MODULATE);
        return currentEMC > 0 ? CondenserState.NOEMCSTORAGE : state;
    }

    @Override
    protected double getEMCPerTick() {
        return BlockEMCCondenser.emcPerTick;
    }

}
