package com.mordenkainen.equivalentenergistics.items;

import com.mordenkainen.equivalentenergistics.registries.ItemEnum;

import appeng.api.AEApi;
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

	private final NBTTagCompound cellData;
	private final ISaveProvider saveProvider;
	private float currentEMC;
	private float capacity;

	public HandlerEMCCell(final ItemStack storageStack, final ISaveProvider saveProvider, final float capacity) {
		// Ensure we have a NBT tag
		if( !storageStack.hasTagCompound() )
		{
			storageStack.setTagCompound( new NBTTagCompound() );
		}

		// Get the NBT tag
		this.cellData = storageStack.getTagCompound();
		
		this.saveProvider = saveProvider;
		
		if(cellData.hasKey("emc")) {
			currentEMC = cellData.getLong("emc");
		}
		
		this.capacity = capacity;
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
		if(!stacks.isEmpty() && stacks.getFirstItem().getItem().equals(ItemEnum.EMCCOMM.getItem())) {
			IItemList<IAEItemStack> retStack = AEApi.instance().storage().createItemList();
			ItemStack retItem = new ItemStack(ItemEnum.EMCCOMM.getItem());
			ItemStack stack = stacks.getFirstItem().getItemStack();
			float change = 0;
			if(stack.hasTagCompound() && stack.stackTagCompound.hasKey("change")) {
				change = adjustEMC(stack.stackTagCompound.getFloat("change"));
			}
			retItem.stackTagCompound = new NBTTagCompound();
			retItem.getTagCompound().setFloat("emc", currentEMC);
			retItem.getTagCompound().setFloat("max", capacity);
			retItem.getTagCompound().setFloat("avail", capacity - currentEMC);
			retItem.getTagCompound().setFloat("adjust", change);
			retStack.add(AEApi.instance().storage().createItemStack(retItem));
			return retStack;
		}
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
		return pass == 1;
	}

	public float adjustEMC(float amount) {
		float toAdjust;
		
		if(amount > 0) {
			toAdjust = Math.min(amount, capacity - currentEMC);
		} else {
			toAdjust = Math.max(amount, -currentEMC);
		}
		
		if(toAdjust != 0) {
			currentEMC += toAdjust;
			cellData.setFloat("emc", currentEMC);
			if (saveProvider != null) {
				saveProvider.saveChanges(this);
			}
		}
		
		return toAdjust;
	}
	
	public int getCellStatus() {
		if(currentEMC >= capacity) {
			return 3;
		}
		if(currentEMC > capacity * 0.75) {
			return 2;
		}
		return 1;
	}
	
}
