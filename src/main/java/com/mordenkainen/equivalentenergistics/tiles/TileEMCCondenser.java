package com.mordenkainen.equivalentenergistics.tiles;

import com.mordenkainen.equivalentenergistics.blocks.BlockEMCCondenser;
import com.mordenkainen.equivalentenergistics.integration.Integration;
import com.mordenkainen.equivalentenergistics.integration.ae2.cache.EMCStorageGrid;
import com.mordenkainen.equivalentenergistics.integration.ae2.grid.GridAccessException;
import com.mordenkainen.equivalentenergistics.integration.ae2.grid.GridUtils;
import com.mordenkainen.equivalentenergistics.integration.waila.IWailaNBTProvider;
import com.mordenkainen.equivalentenergistics.registries.BlockEnum;
import com.mordenkainen.equivalentenergistics.registries.ItemEnum;
import com.mordenkainen.equivalentenergistics.util.CommonUtils;

import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.networking.IGridNode;
import appeng.api.networking.ticking.TickRateModulation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class TileEMCCondenser extends TileEMCCondenserBase implements IWailaNBTProvider {

	protected float currentEMC;
	public TileEMCCondenser() {
		super(new ItemStack(Item.getItemFromBlock(BlockEnum.EMCCONDENSER.getBlock())));
	}
    
	//TODO: Better Waila info: Channel errors, power issues, current inventory, other issues, etc.
	@Override
    public NBTTagCompound getWailaTag(final NBTTagCompound tag) {
		if (currentEMC > 0) {
			tag.setFloat("currentEMC", currentEMC);
		}
        return tag;
    }

    @Override
    public void readFromNBT(final NBTTagCompound data) {
        super.readFromNBT(data);
        if (data.hasKey("CurrentEMC")) {
        	currentEMC = data.getFloat("CurrentEMC");
        }
    }

    @Override
    public void writeToNBT(final NBTTagCompound data) {
        super.writeToNBT(data);
        if (currentEMC > 0) {
        	data.setFloat("CurrentEMC", currentEMC);
        }
    }

	@Override
	public TickRateModulation tickingRequest(final IGridNode node, final int ticksSinceLast) {
		CondenserState newState = state;
		
		newState = checkRequirements();
		
		if (newState != state) {
			state = newState;
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			return TickRateModulation.IDLE;
		}
		
		if (failedStates.contains(state)) {
			return TickRateModulation.IDLE;
		}
		
		if (currentEMC > 0) {
			newState = injectExcessEMC();
			if (newState != state) {
				state = newState;
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
				return TickRateModulation.IDLE;
			}
		}
		
		if (!getInventory().isEmpty()) {
			newState = processInv();
			if (newState != state) {
				state = newState;
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			}
			return state == CondenserState.BLOCKED ? TickRateModulation.IDLE : TickRateModulation.URGENT;
		}
		
		if(state != CondenserState.IDLE) {
			state = CondenserState.IDLE;
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}
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

	private CondenserState processInv() {
		float remainingEMC = getEMCPerTick();
		for (int slot = 0; slot < 4 && remainingEMC > 0; slot ++) {
			final ItemStack stack = getInventory().getStackInSlot(slot);
			if (stack == null) {
				continue;
			}
			
			if (Integration.emcHandler.isEMCStorage(stack)) {
				remainingEMC = processStorage(slot, remainingEMC);
			} else if (ItemEnum.isCrystal(stack)) {
				remainingEMC = processItems(slot, remainingEMC, false);
			} else if (Integration.emcHandler.hasEMC(stack)) {
				remainingEMC = processItems(slot, remainingEMC, true);
			} else {
				getInventory().setInventorySlotContents(slot, GridUtils.injectItemsForPower(getProxy(), stack, mySource));
				if (getInventory().getStackInSlot(slot) != null) {
					remainingEMC = -1;
				}
			}
		}
		
		return remainingEMC == -1 ? CondenserState.BLOCKED : CondenserState.ACTIVE;
	}
	
	private float processItems(final int slot, final float remainingEMC, final boolean usePower) {
		ItemStack stack = getInventory().getStackInSlot(slot);
		final float itemEMC = Integration.emcHandler.getSingleEnergyValue(stack);
		try {
			final EMCStorageGrid emcGrid = GridUtils.getEMCStorage(getProxy());
			final float availEMC = emcGrid.getAvail();
			
			if (itemEMC > availEMC) {
				return -1;
			}
			
			int maxToDo = Math.min(stack.stackSize, Math.min((int) (availEMC / itemEMC), (int) (remainingEMC / itemEMC)));
			if (usePower) {
				maxToDo = getMaxItemsForPower(maxToDo, itemEMC);
			}
			
			if (maxToDo <= 0) {
				return -1;
			}
			
			float toStore = itemEMC * maxToDo;
			if (usePower) {
				GridUtils.getEnergy(getProxy()).extractAEPower(toStore * BlockEMCCondenser.activePower, Actionable.MODULATE, PowerMultiplier.ONE);
			}
			emcGrid.injectEMC(maxToDo * itemEMC, Actionable.MODULATE);
			stack.stackSize -= maxToDo;
			getInventory().setInventorySlotContents(slot, stack.stackSize > 0 ? stack : null);
			
			return remainingEMC - toStore;
		} catch (GridAccessException e) {
			CommonUtils.debugLog("processItems: Error accessing grid:", e);
			return -1;
		}
	}

	private float processStorage(final int slot, final float remainingEMC) {
		final ItemStack stack = getInventory().getStackInSlot(slot);
		final float itemEMC = Integration.emcHandler.getStoredEMC(stack);
		float toStore = 0;
		
		try {			
			if (itemEMC > 0) {
				final EMCStorageGrid emcGrid = GridUtils.getEMCStorage(getProxy());
				toStore = Math.min(Math.min(remainingEMC, itemEMC), emcGrid.getAvail());
				if (toStore <= 0) {
					return -1;
				}
				
				emcGrid.injectEMC(toStore, Actionable.MODULATE);
				Integration.emcHandler.extractEMC(stack, toStore);
				getInventory().setInventorySlotContents(slot, stack);
			}		
			
			if (Integration.emcHandler.getStoredEMC(stack) <= 0) {
				getInventory().setInventorySlotContents(slot, GridUtils.injectItemsForPower(getProxy(), stack, mySource));
				if (getInventory().getStackInSlot(slot) != null) {
					return -1;
				}
			}
			
			return remainingEMC - toStore;
		} catch (GridAccessException e) {
			CommonUtils.debugLog("processStorage: Error accessing grid:", e);
			return -1;
		}
	}
	
}
