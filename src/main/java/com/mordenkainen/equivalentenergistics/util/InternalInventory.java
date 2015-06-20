package com.mordenkainen.equivalentenergistics.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class InternalInventory implements IInventory {
	private static final String NBT_KEY_SLOT = "Slot";
	
	public ItemStack[] slots;
	public String customName;
	private int stackLimit;
	
	public InternalInventory(final String customName, final int size, final int stackLimit)	{
		this.slots = new ItemStack[size];
		this.customName = customName;
		this.stackLimit = stackLimit;
	}
	
	@Override
	public int getSizeInventory() {
		return slots.length;
	}

	@Override
	public ItemStack getStackInSlot(final int index) {
		return slots[index];
	}

	@Override
	public ItemStack decrStackSize(final int slotId, final int amount) {
		ItemStack slotStack = slots[slotId];
		ItemStack resultStack = null;

		if(slotStack == null) {
			return null;
		}

		int decAmount = Math.min(amount, slotStack.stackSize);
		int remAmount = slotStack.stackSize - decAmount;

		if(remAmount > 0) {
			slots[slotId].stackSize = remAmount;
		} else {
			slots[slotId] = null;
		}

		if(decAmount > 0) {
			resultStack = slotStack.copy();
			resultStack.stackSize = decAmount;
		}

		markDirty();

		return resultStack;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(final int slotId) {
		return slots[slotId];
	}

	@Override
	public void setInventorySlotContents(final int slotId, final ItemStack itemStack) {
		if((itemStack != null) && (itemStack.stackSize > getInventoryStackLimit())) {
			itemStack.stackSize = getInventoryStackLimit();
		}

		slots[slotId] = itemStack;

		markDirty();
	}

	@Override
	public String getInventoryName() {
		return customName;
	}

	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}

	@Override
	public int getInventoryStackLimit() {
		return stackLimit;
	}

	@Override
	public void markDirty() {}

	@Override
	public boolean isUseableByPlayer(final EntityPlayer player) {
		return true;
	}

	@Override
	public void openInventory() {}

	@Override
	public void closeInventory() {}

	@Override
	public boolean isItemValidForSlot(final int slotId, final ItemStack itemStack) {
		return false;
	}

	public final void loadFromNBT(final NBTTagCompound data, final String tagName) {
		if(data == null) {
			return;
		}

		if(!data.hasKey(tagName)) {
			return;
		}

		NBTTagList invList = data.getTagList(tagName, (byte)10);

		for(int index = 0; index < invList.tagCount(); index++) {
			NBTTagCompound nbtCompound = invList.getCompoundTagAt(index);

			int slotIndex = nbtCompound.getByte(InternalInventory.NBT_KEY_SLOT) & 0xFF;

			if((slotIndex >= 0) && (slotIndex < this.slots.length)) {
				this.slots[slotIndex] = ItemStack.loadItemStackFromNBT(nbtCompound);
			}
		}
	}
	
	public final void saveToNBT(final NBTTagCompound data, final String tagName) {
		if(data == null) {
			return;
		}

		NBTTagList invList = new NBTTagList();

		for(int slotIndex = 0; slotIndex < this.slots.length; slotIndex++) {
			if(this.slots[slotIndex] != null) {
				NBTTagCompound nbtCompound = new NBTTagCompound();

				nbtCompound.setByte(InternalInventory.NBT_KEY_SLOT, (byte)slotIndex);

				this.slots[slotIndex].writeToNBT(nbtCompound);

				invList.appendTag(nbtCompound);
			}
		}

		if(invList.tagCount() > 0) {
			data.setTag(tagName, invList);
		}
	}
}
