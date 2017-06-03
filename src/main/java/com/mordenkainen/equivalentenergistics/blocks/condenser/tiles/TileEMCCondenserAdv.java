package com.mordenkainen.equivalentenergistics.blocks.condenser.tiles;

import java.util.function.Predicate;

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
        BLOCKED("message.condenser.redstonemode.blocked", state -> state.isError()),
        NOTBLOCKED("message.condenser.redstonemode.notblocked", state -> !state.isError()),
        ACTIVE("message.condenser.redstonemode.active", state -> state == CondenserState.ACTIVE),
        IDLE("message.condenser.redstonemode.idle", state -> state == CondenserState.IDLE),
        DISABLE("message.condenser.redstonemode.disabled"),
        ENABLE("message.condenser.redstonemode.enabled");

        final private String description;
        final private Predicate<CondenserState> stateTest;

        RedstoneMode(final String description, final Predicate<CondenserState> stateTest) {
            this.description = StatCollector.translateToLocal(description);
            this.stateTest = stateTest;
        }

        RedstoneMode(final String description) {
            this.description = StatCollector.translateToLocal(description);
            stateTest = null;
        }

        public String description() {
            return description;
        }

        public boolean isTarget(final CondenserState state) {
            if (stateTest == null) {
                return false;
            }
            return stateTest.test(state);
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
    protected boolean readPacketData(final NBTTagCompound nbttagcompound) {
        final RedstoneMode newMode = RedstoneMode.values()[nbttagcompound.getInteger(MODE_TAG)];
        if (newMode != mode) {
            setMode(newMode);
        }
        return super.readPacketData(nbttagcompound);
    }

    @Override
    public void readFromNBT(final NBTTagCompound data) {
        super.readFromNBT(data);
        if (data.hasKey(MODE_TAG)) {
            setMode(RedstoneMode.values()[data.getInteger(MODE_TAG)]);
        }
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
        if (refreshNetworkState()) {
            markForUpdate();
        }
        
        if (isActive()) {
            final boolean powered = worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord);
            if (mode == RedstoneMode.DISABLE && powered || mode == RedstoneMode.ENABLE && !powered) {
                updateState(CondenserState.IDLE);
                return TickRateModulation.IDLE;
            }
        }

        return super.tickingRequest(node, ticksSinceLast);
    }

    @Override
    protected boolean updateState(final CondenserState newState) {
        if (state != newState) {
            state = newState;
            worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, blockType);
            markForUpdate();
            return true;
        }
        return false;
    }

    public void nextMode() {
        setMode(RedstoneMode.values()[(mode.ordinal() + 1) % 7]);
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
            markForUpdate();
        }
    }

}
