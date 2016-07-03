package com.mordenkainen.equivalentenergistics.util.inventory;

import cpw.mods.fml.common.FMLCommonHandler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class InternalInventory implements IInventory {
	
	private static final String NBT_KEY_SLOT = "Slot";
	
	public ItemStack[] slots;
	public String customName;
	private final int stackLimit;
	private final IInvChangeNotifier te;
	
	public InternalInventory(final String _customName, final int size, final int _stackLimit, final IInvChangeNotifier _te) {
		slots = new ItemStack[size];
		customName = _customName;
		stackLimit = _stackLimit;
		te = _te;
	}
	
	public InternalInventory(final String _customName, final int size, final int _stackLimit) {
		this(_customName, size, _stackLimit, null);
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
		final ItemStack slotStack = slots[slotId];

		if (slotStack == null) {
			return null;
		}

		final int decAmount = Math.min(amount, slotStack.stackSize);
		final int remAmount = slotStack.stackSize - decAmount;

		if (remAmount > 0) {
			slots[slotId].stackSize = remAmount;
		} else {
			slots[slotId] = null;
		}

		ItemStack resultStack = null;
		
		if (decAmount > 0) {
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
		if (itemStack != null && itemStack.stackSize > getInventoryStackLimit()) {
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
	public void markDirty() {
		if (te != null && !FMLCommonHandler.instance().getEffectiveSide().isClient()) {
			te.onChangeInventory();
		}
	}

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
		if (data == null) {
			return;
		}

		if (!data.hasKey(tagName)) {
			return;
		}

		final NBTTagList invList = data.getTagList(tagName, (byte)10);

		for (int index = 0; index < invList.tagCount(); index++) {
			final NBTTagCompound nbtCompound = invList.getCompoundTagAt(index);

			final int slotIndex = nbtCompound.getByte(InternalInventory.NBT_KEY_SLOT) & 0xFF;

			if (slotIndex >= 0 && slotIndex < this.slots.length) {
				this.slots[slotIndex] = ItemStack.loadItemStackFromNBT(nbtCompound);
			}
		}
	}
	
	public final void saveToNBT(final NBTTagCompound data, final String tagName) {
		if (data == null) {
			return;
		}

		final NBTTagList invList = new NBTTagList();

		for (int slotIndex = 0; slotIndex < this.slots.length; slotIndex++) {
			if (this.slots[slotIndex] != null) {
				final NBTTagCompound nbtCompound = new NBTTagCompound();

				nbtCompound.setByte(InternalInventory.NBT_KEY_SLOT, (byte)slotIndex);

				this.slots[slotIndex].writeToNBT(nbtCompound);

				invList.appendTag(nbtCompound);
			}
		}

		if (invList.tagCount() > 0) {
			data.setTag(tagName, invList);
		}
	}
	
	public boolean isEmpty() {
		for (int x = 0; x < getSizeInventory(); x++) {
			if (getStackInSlot(x) != null) {
				return false;
			}
		}
		return true;
	}
	
}
