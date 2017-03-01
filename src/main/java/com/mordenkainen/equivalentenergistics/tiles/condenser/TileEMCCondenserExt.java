package com.mordenkainen.equivalentenergistics.tiles.condenser;

import java.util.HashMap;
import java.util.Map;

import com.mordenkainen.equivalentenergistics.blocks.BlockEMCCondenser;
import com.mordenkainen.equivalentenergistics.integration.ae2.grid.GridUtils;
import com.mordenkainen.equivalentenergistics.registries.BlockEnum;
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

    private final Map<ForgeDirection, Boolean> sides = new HashMap<ForgeDirection, Boolean>();

    public TileEMCCondenserExt() {
        this(new ItemStack(Item.getItemFromBlock(BlockEnum.EMCCONDENSER.getBlock()), 1, 2));
    }

    public TileEMCCondenserExt(final ItemStack repItem) {
        super(repItem);
        internalInventory = new InternalInventory("EMCCondenserInventory", 4, 64);
    }

    @Override
    public TickRateModulation tickingRequest(final IGridNode node, final int ticksSinceLast) {
        final boolean powered = worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord);
        if (mode == RedstoneMode.DISABLE && powered || mode == RedstoneMode.ENABLE && !powered) {
            updateState(CondenserState.IDLE);
            return TickRateModulation.IDLE;
        }

        importItems();

        return super.tickingRequest(node, ticksSinceLast);
    }

    protected void importItems() {
        int numItems = itemsToTransfer();

        outerLoop: for (int slot = 0; slot < getInventory().getSizeInventory(); slot++) {
            final ItemStack slotContent = getInventory().getStackInSlot(slot);
            if (slotContent == null || slotContent.stackSize < slotContent.getMaxStackSize()) {
                for (final ForgeDirection side : sides.keySet()) {
                    if (sides.get(side)) {
                        continue;
                    }
                    final IInventory sourceInv = CommonUtils.getTE(IInventory.class, worldObj, xCoord + side.offsetX, yCoord + side.offsetY, zCoord + side.offsetZ);
                    if (sourceInv != null) {
                        numItems -= InvUtils.importFromAdjInv(side.getOpposite(), sourceInv, getInventory(), slot, numItems);
                    }
                    if (numItems <= 0) {
                        break outerLoop;
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
            if (!sides.get(side)) {
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
            return GridUtils.injectItemsForPower(getProxy(), stack, mySource);
        } else {
            return null;
        }
    }

    @Override
    protected void getPacketData(final NBTTagCompound nbttagcompound) {
        super.getPacketData(nbttagcompound);
        final NBTTagCompound list = new NBTTagCompound();
        for (final ForgeDirection side : sides.keySet()) {
            list.setBoolean(side.name(), sides.get(side));
        }
        nbttagcompound.setTag(SIDE_TAG, list);
    }

    @Override
    protected void readPacketData(final NBTTagCompound nbttagcompound) {
        super.readPacketData(nbttagcompound);
        sides.clear();
        final NBTTagCompound list = (NBTTagCompound) nbttagcompound.getTag(SIDE_TAG);
        for (final ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
            if (list.hasKey(side.name())) {
                sides.put(side, list.getBoolean(side.name()));
            }
        }
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    @Override
    public void readFromNBT(final NBTTagCompound data) {
        super.readFromNBT(data);
        sides.clear();
        final NBTTagCompound list = (NBTTagCompound) data.getTag(SIDE_TAG);
        if (list == null) {
            return;
        }

        for (final ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
            if (list.hasKey(side.name())) {
                sides.put(side, list.getBoolean(side.name()));
            }
        }
    }

    @Override
    public void writeToNBT(final NBTTagCompound data) {
        super.writeToNBT(data);
        final NBTTagCompound list = new NBTTagCompound();
        for (final ForgeDirection side : sides.keySet()) {
            list.setBoolean(side.name(), sides.get(side));
        }
        data.setTag(SIDE_TAG, list);
    }

    public void toggleSide(final int side) {
        if (sides.containsKey(ForgeDirection.getOrientation(side))) {
            final boolean state = sides.get(ForgeDirection.getOrientation(side));
            if (state) {
                sides.remove(ForgeDirection.getOrientation(side));
            } else {
                sides.put(ForgeDirection.getOrientation(side), true);
            }
        } else {
            sides.put(ForgeDirection.getOrientation(side), false);
        }
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    public int getSide(final int side) {
        if (sides.containsKey(ForgeDirection.getOrientation(side))) {
            return sides.get(ForgeDirection.getOrientation(side)) ? 2 : 3;
        } else {
            return 0;
        }
    }

    protected int itemsToTransfer() {
        return 16;
    }
}
