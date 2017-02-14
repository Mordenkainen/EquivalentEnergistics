package com.mordenkainen.equivalentenergistics.tiles;

import java.util.List;

import com.mordenkainen.equivalentenergistics.integration.Integration;
import com.mordenkainen.equivalentenergistics.integration.waila.IWailaNBTProvider;
import com.mordenkainen.equivalentenergistics.items.ItemEMCCell;
import com.mordenkainen.equivalentenergistics.registries.ItemEnum;
import com.mordenkainen.equivalentenergistics.util.EMCPool;
import com.mordenkainen.equivalentenergistics.util.IDropItems;
import com.mordenkainen.equivalentenergistics.util.inventory.IInventoryInt;
import com.mordenkainen.equivalentenergistics.util.inventory.InvUtils;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public abstract class TileCondenserBase extends TileEntity implements IWailaNBTProvider, IInventoryInt, IDropItems {

    protected CondenserState state = CondenserState.Idle;
    protected EMCPool emcPool;

    @Override
    public NBTTagCompound getWailaTag(final NBTTagCompound tag) {
        tag.setFloat("currentEMC", emcPool.getCurrentEMC());
        tag.setFloat("maxEMC", emcPool.getMaxEMC());
        return tag;
    }

    @Override
    public void readFromNBT(final NBTTagCompound data) {
        super.readFromNBT(data);
        emcPool.setCurrentEMC(data.getFloat("CurrentEMC"));
        IInventoryInt.super.readFromNBT(data);
    }

    @Override
    public void writeToNBT(final NBTTagCompound data) {
        super.writeToNBT(data);
        data.setFloat("CurrentEMC", emcPool.getCurrentEMC());
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

    public boolean isBlocked() {
        return state == CondenserState.Blocked;
    }

    protected boolean hasWork() {
        return emcPool.getCurrentEMC() > 0 || !getInventory().isEmpty();
    }

    @Override
    public void getDrops(final World world, final int x, final int y, final int z, final List<ItemStack> drops) {
        drops.addAll(InvUtils.getInvAsList(getInventory()));
    }

    protected CondenserState condense() {
        if (hasWork()) {
            boolean didWork = false;
            didWork |= ejectEMC();
            didWork |= processInv();
            return didWork ? CondenserState.Active : CondenserState.Blocked;
        } else {
            return CondenserState.Idle;
        }
    }

    protected boolean processInv() {
        boolean didWork = false;
        int toProcess = getMaxItems();
        for (int slot = 0; slot < getInventory().getSizeInventory() && toProcess > 0; slot++) {
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

            if (resultStack == null || !isStackSame(stack, resultStack)) {
                didWork = true;
                getInventory().setInventorySlotContents(slot, resultStack);
                toProcess -= resultStack == null ? stack.stackSize : stack.stackSize - resultStack.stackSize;
            }
        }
        return didWork;
    }

    private boolean isStackSame(final ItemStack stack1, final ItemStack stack2) {
        return ItemStack.areItemStacksEqual(stack1, stack2) && ItemStack.areItemStackTagsEqual(stack1, stack2);
    }

    private boolean isCrystal(final ItemStack stack) {
        return ItemEnum.EMCCRYSTAL.isSameItem(stack) || ItemEnum.EMCCRYSTALOLD.isSameItem(stack);
    }

    protected ItemStack convertItemsToEMC(final ItemStack stack, final int maxItems) {
    	if (emcPool.isFull()) {
    		return stack;
    	}
    	
        final float itemEMC = Integration.emcHandler.getSingleEnergyValue(stack);
        int maxToDo = 1;
        
        if (emcPool.getAvail() > itemEMC) {
        	maxToDo = Math.min(stack.stackSize, Math.min(maxItems, (int) (emcPool.getAvail() / itemEMC)));
        }
    	
        if (maxToDo <= 0) {
            return stack;
        }

        emcPool.addEMC(itemEMC * maxToDo);
        if (maxToDo >= stack.stackSize) {
            return null;
        }

        final ItemStack retStack = stack.copy();
        retStack.stackSize = stack.stackSize - maxToDo;
        return retStack;
    }

    protected ItemStack processStorage(final ItemStack stack) {
        boolean eject = false;
        float toStore = emcPool.getAvail();
        if (stack.getItem() == ItemEnum.EMCCELL.getItem()) {
            final ItemEMCCell cell = (ItemEMCCell) ItemEnum.EMCCELL.getItem();
            toStore = Math.min(toStore, cell.getStoredCellEMC(stack));
            emcPool.addEMC(cell.extractCellEMC(stack, toStore));
            if (cell.getStoredCellEMC(stack) <= 0) {
                eject = true;
            }
        } else {
            toStore = Math.min(toStore, Integration.emcHandler.getStoredEMC(stack));
            emcPool.addEMC(Integration.emcHandler.extractEMC(stack, toStore));
            if (Integration.emcHandler.getStoredEMC(stack) <= 0) {
                eject = true;
            }
        }

        return eject ? ejectStack(stack) : stack;
    }

    protected ItemStack processItems(final ItemStack stack, final int maxItems) {
        final int maxToDo = Math.min(maxItems, getMaxItemsForPower(stack));
        final ItemStack retStack = convertItemsToEMC(stack, maxToDo);
        consumePower(stack, retStack == null ? stack.stackSize : stack.stackSize - retStack.stackSize);
        return retStack;
    }

    private boolean isEMCStorage(final ItemStack stack) {
        return ItemEnum.EMCCELL.isSameItem(stack) || Integration.emcHandler.isEMCStorage(stack);
    }

    protected abstract int getMaxItems();

    protected abstract ItemStack ejectStack(ItemStack stack);

    protected abstract boolean ejectEMC();

    protected abstract void consumePower(ItemStack items, int count);

    protected abstract int getMaxItemsForPower(ItemStack stack);

    protected enum CondenserState {
        Idle, Active, Blocked;
    }

}
