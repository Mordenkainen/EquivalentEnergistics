package com.mordenkainen.equivalentenergistics.tiles;

import java.util.List;

import com.mordenkainen.equivalentenergistics.integration.Integration;
import com.mordenkainen.equivalentenergistics.integration.waila.IWailaNBTProvider;
import com.mordenkainen.equivalentenergistics.items.ItemEMCCell;
import com.mordenkainen.equivalentenergistics.registries.ItemEnum;
import com.mordenkainen.equivalentenergistics.util.inventory.IInventoryInt;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public abstract class TileCondenserBase extends TileEntity implements IWailaNBTProvider, IInventoryInt {

	protected float currentEMC;
	protected CondenserState state = CondenserState.Idle; 
	
	// IWailaNBTProvider Overrides
	// ------------------------
	@Override
	public NBTTagCompound getWailaTag(final NBTTagCompound tag) {
		tag.setFloat("currentEMC", currentEMC);
		tag.setFloat("maxEMC", getMaxEMC());
		return tag;
	}
	// ------------------------
	
	// TileEntity Overrides
	// ------------------------
	@Override
	public void readFromNBT(final NBTTagCompound data) {
		super.readFromNBT(data);
		currentEMC = data.getFloat("CurrentEMC");
		IInventoryInt.super.readFromNBT(data);
	}
	
	@Override
	public void writeToNBT(final NBTTagCompound data) {
		super.writeToNBT(data);
		data.setFloat("CurrentEMC", currentEMC);
		IInventoryInt.super.writeToNBT(data);
	}	
	
	@Override
	public Packet getDescriptionPacket() {
		final NBTTagCompound nbttagcompound = new NBTTagCompound();
		nbttagcompound.setInteger("state", state.ordinal());
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, -999, nbttagcompound);
	}

	@Override
	public void onDataPacket(final NetworkManager net, final S35PacketUpdateTileEntity pkt) {
		state = CondenserState.values()[pkt.func_148857_g().getInteger("state")];
	}
	// ------------------------
	
	public boolean isBlocked() {
		return state == CondenserState.Blocked;
	}
	
	protected boolean hasWork() {
		if (currentEMC > 0) {
			return true;
		}
		
		return !getInventory().isEmpty();
	}
	
	public void getDrops(final World world, final int x, final int y, final int z, final List<ItemStack> drops) {		
		for (int slot = 0; slot < getInventory().getSizeInventory(); slot++) {
			final ItemStack item = getInventory().getStackInSlot(slot);

			if (item != null) {
				drops.add(item);
			}
		}
	}
	
	protected CondenserState condense() {
		if (hasWork()) {
			boolean didWork = false;
			didWork |= processInv();
			didWork |= ejectEMC();
			return didWork ? CondenserState.Active : CondenserState.Blocked;
		} else {
			return CondenserState.Idle;
		}
	}
	
	protected boolean processInv() {
		boolean didWork = false;
		int toProcess = getMaxItems();
		for (int slot = 0; slot < getInventory().getSizeInventory(); slot++) {			
			final ItemStack stack = getInventory().getStackInSlot(slot);
			
			if (stack == null) {
				continue;
			}
			
			ItemStack resultStack;
			if (isCrystal(stack)) {
				resultStack = convertItemsToEMC(stack, toProcess);
			} else if (isEMCStorage(stack)) {
				resultStack = processStorage(stack);
			} else if (Integration.emcHandler.hasEMC(stack)) {
				resultStack = processItems(stack, toProcess);
			} else {
				resultStack = ejectStack(stack);
			}
			
			if (resultStack == null) {
				didWork = true;
				getInventory().setInventorySlotContents(slot, null);
				toProcess -= stack.stackSize;
			} else if (!isStackSame(stack, resultStack)){
				didWork = true;
				getInventory().setInventorySlotContents(slot, resultStack);
				toProcess -= stack.stackSize - resultStack.stackSize;
			}
			
			if (toProcess <= 0) {
				break;
			}
		}
		return didWork;
	}

	private boolean isStackSame(final ItemStack stack1, final ItemStack stack2) {
		return ItemStack.areItemStacksEqual(stack1, stack2) && ItemStack.areItemStackTagsEqual(stack1, stack2);
	}

	private boolean isCrystal(final ItemStack stack) {
		return stack.getItem() == ItemEnum.EMCCRYSTAL.getItem() || stack.getItem() == ItemEnum.EMCCRYSTALOLD.getItem();
	}

	protected ItemStack convertItemsToEMC(final ItemStack stack, final int maxItems) {
		final int maxToDo = Math.min(stack.stackSize, Math.min(maxItems, (int) ((getMaxEMC() - currentEMC) / Integration.emcHandler.getSingleEnergyValue(stack))));
		if (maxToDo > 0) {
			currentEMC += Integration.emcHandler.getSingleEnergyValue(stack) * maxToDo;
			if (maxToDo >= stack.stackSize) {
				return null;
			} else {
				final ItemStack retStack = stack.copy();
				retStack.stackSize = stack.stackSize - maxToDo;
				return retStack;
			}
		}
		return stack;
	}

	protected ItemStack processStorage(final ItemStack stack) {
		boolean eject = false;
		float toStore = getMaxEMC() - currentEMC;
		if (stack.getItem() == ItemEnum.EMCCELL.getItem()) {
			toStore = Math.min(toStore, ((ItemEMCCell) ItemEnum.EMCCELL.getItem()).getStoredCellEMC(stack));
			currentEMC += ((ItemEMCCell) ItemEnum.EMCCELL.getItem()).extractCellEMC(stack, toStore);
			if (((ItemEMCCell) ItemEnum.EMCCELL.getItem()).getStoredCellEMC(stack) <= 0) {
				eject = true;
			}
		} else {
			toStore = Math.min(toStore, Integration.emcHandler.getStoredEMC(stack));
			currentEMC += Integration.emcHandler.extractEMC(stack, toStore);
			if (Integration.emcHandler.getStoredEMC(stack) <= 0) {
				eject = true;
			}
		}
		
		if (eject) {
			return ejectStack(stack);
		} else {
			return stack;
		}
	}
	
	protected ItemStack processItems(final ItemStack stack, final int maxItems) {
		final int maxToDo = Math.min(maxItems, getMaxItemsForPower(stack));
		final ItemStack retStack = convertItemsToEMC(stack, maxToDo);
		consumePower(stack, retStack == null ? stack.stackSize : stack.stackSize - retStack.stackSize);
		return retStack;
	}

	private boolean isEMCStorage(final ItemStack stack) {
		if (stack.getItem() == ItemEnum.EMCCELL.getItem()) {
			return true;
		}
		return Integration.emcHandler.isEMCStorage(stack);
	}
	
	protected abstract int getMaxItems();
	
	protected abstract ItemStack ejectStack(ItemStack stack);
	
	protected abstract boolean ejectEMC();
	
	protected abstract float getMaxEMC();
	
	protected abstract void consumePower(ItemStack items, int count);

	protected abstract int getMaxItemsForPower(ItemStack stack);
	
	protected enum CondenserState {
		Idle,
		Active,
		Blocked;
	}
	
}
