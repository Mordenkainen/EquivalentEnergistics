package com.mordenkainen.equivalentenergistics.tiles.condenser;

import com.mordenkainen.equivalentenergistics.blocks.BlockEMCCondenser;
import com.mordenkainen.equivalentenergistics.integration.ae2.grid.GridAccessException;
import com.mordenkainen.equivalentenergistics.integration.ae2.grid.GridUtils;
import com.mordenkainen.equivalentenergistics.integration.waila.IWailaNBTProvider;
import com.mordenkainen.equivalentenergistics.registries.BlockEnum;
import com.mordenkainen.equivalentenergistics.util.CommonUtils;

import appeng.api.config.Actionable;
import appeng.api.networking.IGridNode;
import appeng.api.networking.ticking.TickRateModulation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class TileEMCCondenser extends TileEMCCondenserBase implements IWailaNBTProvider {
	private final static String EMC_TAG = "CurrentEMC";
	
	protected float currentEMC;
	
	public TileEMCCondenser() {
		super(new ItemStack(Item.getItemFromBlock(BlockEnum.EMCCONDENSER.getBlock())));
	}
    
	@Override
    public NBTTagCompound getWailaTag(final NBTTagCompound tag) {
		if (currentEMC > 0) {
			tag.setFloat(EMC_TAG, currentEMC);
		}
        return tag;
    }

    @Override
    public void readFromNBT(final NBTTagCompound data) {
        super.readFromNBT(data);
        if (data.hasKey(EMC_TAG)) {
        	currentEMC = data.getFloat(EMC_TAG);
        }
    }

    @Override
    public void writeToNBT(final NBTTagCompound data) {
        super.writeToNBT(data);
        if (currentEMC > 0) {
        	data.setFloat(EMC_TAG, currentEMC);
        }
    }

	@Override
	public TickRateModulation tickingRequest(final IGridNode node, final int ticksSinceLast) {
		CondenserState newState = state;
		
		newState = checkRequirements();
		
		if (updateState(newState) || failedStates.contains(state)) {
			return TickRateModulation.IDLE;
		}
		
		if (currentEMC > 0) {
			newState = injectExcessEMC();
			if (updateState(newState)) {
				return TickRateModulation.IDLE;
			}
		}
		
		if (!getInventory().isEmpty()) {
			newState = processInv();
			updateState(newState);
			return state == CondenserState.BLOCKED ? TickRateModulation.IDLE : TickRateModulation.URGENT;
		}
		
		updateState(CondenserState.IDLE);
		return TickRateModulation.IDLE;
	}

	private CondenserState injectExcessEMC() {
		try {
			currentEMC -= GridUtils.getEMCStorage(getProxy()).injectEMC(currentEMC, Actionable.MODULATE);
		} catch (final GridAccessException e) {
            CommonUtils.debugLog("tickingRequest: Error accessing grid:", e);
        }

		return currentEMC > 0 ? CondenserState.BLOCKED : state;
	}
	
	@Override
	protected float getEMCPerTick() {
	    return BlockEMCCondenser.emcPerTick;
	}
	
}
