package com.mordenkainen.equivalentenergistics.blocks.condenser.tiles;

import com.mordenkainen.equivalentenergistics.blocks.BlockEnum;
import com.mordenkainen.equivalentenergistics.blocks.condenser.BlockEMCCondenser;
import com.mordenkainen.equivalentenergistics.blocks.condenser.CondenserState;

import appeng.api.networking.IGridNode;
import appeng.api.networking.ticking.TickRateModulation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;

public class TileEMCCondenserAdv extends TileEMCCondenserBase {

    private final static String MODE_TAG = "mode";

    protected RedstoneMode mode = RedstoneMode.NONE;

    public enum RedstoneMode {
        NONE("message.condenser.redstonemode.none"),
        BLOCKED("message.condenser.redstonemode.blocked", CondenserState.BLOCKED),
        ACTIVE("message.condenser.redstonemode.active", CondenserState.ACTIVE),
        IDLE("message.condenser.redstonemode.idle", CondenserState.IDLE),
        DISABLE("message.condenser.redstonemode.disabled"),
        ENABLE("message.condenser.redstonemode.enabled");

        final private String description;
        final private CondenserState targetState;

        RedstoneMode(final String description, final CondenserState targetState) {
            this.description = StatCollector.translateToLocal(description);
            this.targetState = targetState;
        }

        RedstoneMode(final String description) {
            this.description = StatCollector.translateToLocal(description);
            this.targetState = null;
        }

        public String description() {
            return description;
        }

        public boolean isTarget(final CondenserState state) {
            return state == targetState;
        }
    }

    public TileEMCCondenserAdv() {
        this(new ItemStack(Item.getItemFromBlock(BlockEnum.EMCCONDENSER.getBlock()), 1, 1));
    }

    public TileEMCCondenserAdv(final ItemStack repItem) {
        super(repItem);
    }

    @Override
    protected void getPacketData(final NBTTagCompound nbttagcompound) {
        super.getPacketData(nbttagcompound);
        nbttagcompound.setInteger(MODE_TAG, mode.ordinal());
    }

    @Override
    protected void readPacketData(final NBTTagCompound nbttagcompound) {
        super.readPacketData(nbttagcompound);
        setMode(RedstoneMode.values()[nbttagcompound.getInteger(MODE_TAG)]);
    }

    @Override
    public void readFromNBT(final NBTTagCompound data) {
        super.readFromNBT(data);
        setMode(RedstoneMode.values()[data.getInteger(MODE_TAG)]);
    }

    @Override
    public void writeToNBT(final NBTTagCompound data) {
        super.writeToNBT(data);
        data.setInteger(MODE_TAG, mode.ordinal());
    }

    @Override
    protected float getEMCPerTick() {
        return BlockEMCCondenser.emcPerTick * 10;
    }

    @Override
    public TickRateModulation tickingRequest(final IGridNode node, final int ticksSinceLast) {
        final boolean powered = worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord);
        if (mode == RedstoneMode.DISABLE && powered || mode == RedstoneMode.ENABLE && !powered) {
            updateState(CondenserState.IDLE);
            return TickRateModulation.IDLE;
        }

        return super.tickingRequest(node, ticksSinceLast);
    }

    @Override
    protected boolean updateState(final CondenserState newState) {
        if (state != newState) {
            state = newState;
            worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, blockType);
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
            return true;
        }
        return false;
    }

    public void nextMode() {
        setMode(RedstoneMode.values()[(mode.ordinal() + 1) % 6]);
    }

    public RedstoneMode getMode() {
        return mode;
    }

    public boolean isProducingPower() {
        return mode.isTarget(state);
    }

    private void setMode(final RedstoneMode newMode) {
        if (newMode != null && newMode != mode) {
            mode = newMode;
            worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, blockType);
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }
    }

}
