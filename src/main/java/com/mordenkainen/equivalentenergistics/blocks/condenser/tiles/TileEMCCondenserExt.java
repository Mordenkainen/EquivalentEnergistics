package com.mordenkainen.equivalentenergistics.blocks.condenser.tiles;

import java.util.HashMap;
import java.util.Map;

import com.mordenkainen.equivalentenergistics.blocks.BlockEnum;
import com.mordenkainen.equivalentenergistics.blocks.condenser.BlockEMCCondenser;
import com.mordenkainen.equivalentenergistics.blocks.condenser.CondenserState;
import com.mordenkainen.equivalentenergistics.util.CommonUtils;
import com.mordenkainen.equivalentenergistics.util.inventory.InternalInventory;
import com.mordenkainen.equivalentenergistics.util.inventory.InvUtils;

import appeng.api.networking.IGridNode;
import appeng.api.networking.ticking.TickRateModulation;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEMCCondenserExt extends TileEMCCondenserAdv {

    private final static String SIDE_TAG = "sides";

    private final Map<ForgeDirection, SideSetting> sides = new HashMap<ForgeDirection, SideSetting>();
    
    public enum SideSetting {
        NONE,
        INPUT,
        OUTPUT;
        
        public SideSetting getNext() {
            int setting = this.ordinal() + 1;
            if (setting >= 3) {
                setting = 0;
            }
            return SideSetting.values()[setting];
        }
    }
    
    public TileEMCCondenserExt() {
        this(new ItemStack(Item.getItemFromBlock(BlockEnum.EMCCONDENSER.getBlock()), 1, 2));
    }

    public TileEMCCondenserExt(final ItemStack repItem) {
        super(repItem);
        internalInventory = new InternalInventory("EMCCondenserInventory", 4, 64);
        for (final ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
            sides.put(side, SideSetting.NONE);
        }
    }

    @Override
    public TickRateModulation tickingRequest(final IGridNode node, final int ticksSinceLast) {
        if (refreshNetworkState()) {
            markForUpdate();
        }
        
        if (isActive()) {
            final boolean powered = worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord);
            if (mode == RedstoneMode.DISABLE && powered || mode == RedstoneMode.ENABLE && !powered) {
                updateState(CondenserState.IDLE);
                return TickRateModulation.IDLE;
            }
    
            importItems();
        }

        return super.tickingRequest(node, ticksSinceLast);
    }

    protected void importItems() {
        int numItems = itemsToTransfer();

        for (int slot = 0; slot < getInventory().getSizeInventory() && numItems > 0; slot++) {
            final ItemStack slotContent = getInventory().getStackInSlot(slot);
            if (slotContent == null || slotContent.stackSize < slotContent.getMaxStackSize()) {
                for (final ForgeDirection side : sides.keySet()) {
                    if (sides.get(side) != SideSetting.INPUT) {
                        continue;
                    }
                    final IInventory sourceInv = CommonUtils.getTE(IInventory.class, worldObj, xCoord + side.offsetX, yCoord + side.offsetY, zCoord + side.offsetZ);
                    if (sourceInv != null) {
                        numItems -= InvUtils.importFromAdjInv(side.getOpposite(), sourceInv, getInventory(), slot, numItems);
                    }
                    if (numItems <= 0) {
                        break;
                    }
                }
            }
        }
    }

    @Override
    protected float getEMCPerTick() {
        return BlockEMCCondenser.emcPerTick * 100;
    }

    @Override
    protected ItemStack ejectItem(final ItemStack stack) {
        int numItems = itemsToTransfer();

        for (final ForgeDirection side : sides.keySet()) {
            if (sides.get(side) != SideSetting.OUTPUT) {
                continue;
            }
            final IInventory destInv = CommonUtils.getTE(IInventory.class, worldObj, xCoord + side.offsetX, yCoord + side.offsetY, zCoord + side.offsetZ);
            if (destInv != null) {
                final int ejected = InvUtils.ejectStack(stack, destInv, side.getOpposite(), numItems);
                numItems -= ejected;
                stack.stackSize -= ejected;
            }

            if (numItems <= 0) {
                break;
            }
        }

        if (stack.stackSize > 0) {
            return super.ejectItem(stack);
        } else {
            return null;
        }
    }

    @Override
    protected void getPacketData(final NBTTagCompound nbttagcompound) {
        super.getPacketData(nbttagcompound);
        final NBTTagCompound list = new NBTTagCompound();
        for (final ForgeDirection side : sides.keySet()) {
            list.setInteger(side.name(), sides.get(side).ordinal());
        }
        nbttagcompound.setTag(SIDE_TAG, list);
    }

    @Override
    protected boolean readPacketData(final NBTTagCompound nbttagcompound) {
        boolean flag = super.readPacketData(nbttagcompound);
        final NBTTagCompound list = (NBTTagCompound) nbttagcompound.getTag(SIDE_TAG);
        for (final ForgeDirection side : sides.keySet()) {
            final SideSetting newData = SideSetting.values()[list.getInteger(side.name())];
            if (newData != sides.get(side)) {
                sides.put(side, newData);
                flag = true;
            }
            
        }
        return flag;
    }

    @Override
    public void readFromNBT(final NBTTagCompound data) {
        super.readFromNBT(data);
        final NBTTagCompound list = (NBTTagCompound) data.getTag(SIDE_TAG);
        for (final ForgeDirection side : sides.keySet()) {
            sides.put(side, SideSetting.values()[list.getInteger(side.name())]);
        }
    }

    @Override
    public void writeToNBT(final NBTTagCompound data) {
        super.writeToNBT(data);
        final NBTTagCompound list = new NBTTagCompound();
        for (final ForgeDirection side : sides.keySet()) {
            list.setInteger(side.name(), sides.get(side).ordinal());
        }
        data.setTag(SIDE_TAG, list);
    }

    public void toggleSide(final int side) {
        final ForgeDirection forgeSide = ForgeDirection.getOrientation(side);
        sides.put(forgeSide, sides.get(forgeSide).getNext());
        markForUpdate();
    }

    public SideSetting getSide(final int side) {
        return sides.get(ForgeDirection.getOrientation(side));
    }

    protected int itemsToTransfer() {
        return 16;
    }
}
