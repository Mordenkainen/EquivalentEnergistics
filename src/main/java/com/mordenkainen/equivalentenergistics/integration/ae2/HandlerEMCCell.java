package com.mordenkainen.equivalentenergistics.integration.ae2;

import appeng.api.config.AccessRestriction;
import appeng.api.config.Actionable;
import appeng.api.networking.security.BaseActionSource;
import appeng.api.storage.IMEInventoryHandler;
import appeng.api.storage.ISaveProvider;
import appeng.api.storage.StorageChannel;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IItemList;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class HandlerEMCCell implements IMEInventoryHandler<IAEItemStack> {

	private static final String EMC_TAG = "emc";
	
	private final NBTTagCompound cellData;
	private final ISaveProvider saveProvider;
	private float currentEMC;
	private final float capacity;

	public HandlerEMCCell(final ItemStack storageStack, final ISaveProvider _saveProvider, final float _capacity) {
		if (!storageStack.hasTagCompound()) {
			storageStack.setTagCompound(new NBTTagCompound());
		}

		cellData = storageStack.getTagCompound();
		if (cellData.hasKey("emc")) {
			currentEMC = cellData.getLong(EMC_TAG);
		}
		
		saveProvider = _saveProvider;
		capacity = _capacity;
	}
	
	@Override
	public IAEItemStack injectItems(final IAEItemStack input, final Actionable type, final BaseActionSource src) {
		return input;
	}

	@Override
	public IAEItemStack extractItems(final IAEItemStack request, final Actionable mode, final BaseActionSource src) {
		return null;
	}

	@Override
	public IItemList<IAEItemStack> getAvailableItems(final IItemList<IAEItemStack> stacks) {
		return stacks;
	}

	@Override
	public StorageChannel getChannel() {
		return StorageChannel.ITEMS;
	}

	@Override
	public AccessRestriction getAccess() {
		return AccessRestriction.READ_WRITE;
	}

	@Override
	public boolean isPrioritized(final IAEItemStack input) {
		return false;
	}

	@Override
	public boolean canAccept(final IAEItemStack input) {
		return false;
	}

	@Override
	public int getPriority() {
		return 0;
	}

	@Override
	public int getSlot() {
		return 0;
	}

	@Override
	public boolean validForPass(final int pass) {
		return false;
	}

	public float adjustEMC(final float amount) {
		float toAdjust;
		
		if (amount > 0) {
			toAdjust = Math.min(amount, capacity - currentEMC);
		} else {
			toAdjust = Math.max(amount, -currentEMC);
		}
		
		if (toAdjust != 0) {
			currentEMC += toAdjust;
			cellData.setFloat(EMC_TAG, currentEMC);
			if (saveProvider != null) {
				saveProvider.saveChanges(this);
			}
		}
		
		return toAdjust;
	}
	
	public int getCellStatus() {
		if (currentEMC >= capacity) {
			return 3;
		}
		if (currentEMC > capacity * 0.75) {
			return 2;
		}
		return 1;
	}
	
	public float getCapacity() {
		return capacity;
	}
	
	public float getEMC() {
		return currentEMC;
	}
	
	public float getAvail() {
		return Math.max(capacity - currentEMC, 0);
	}
	
}
