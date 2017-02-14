package com.mordenkainen.equivalentenergistics.tiles;

import java.util.EnumSet;

import com.mordenkainen.equivalentenergistics.blocks.BlockEMCCondenser;
import com.mordenkainen.equivalentenergistics.integration.Integration;
import com.mordenkainen.equivalentenergistics.integration.ae2.grid.GridAccessException;
import com.mordenkainen.equivalentenergistics.integration.ae2.grid.GridUtils;
import com.mordenkainen.equivalentenergistics.integration.ae2.tiles.TileAEInv;
import com.mordenkainen.equivalentenergistics.items.ItemEMCCell;
import com.mordenkainen.equivalentenergistics.util.CommonUtils;
import com.mordenkainen.equivalentenergistics.util.inventory.InternalInventory;

import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.networking.GridFlags;
import appeng.api.networking.IGridNode;
import appeng.api.networking.energy.IEnergyGrid;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickingRequest;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;

public abstract class TileEMCCondenserBase extends TileAEInv implements IGridTickable {

	protected CondenserState state = CondenserState.IDLE;
	protected final EnumSet<CondenserState> failedStates = EnumSet.of(CondenserState.MISSING_CHANNEL, CondenserState.UNPOWERED);

	protected enum CondenserState {
	    IDLE, ACTIVE, BLOCKED, UNPOWERED, MISSING_CHANNEL;
	}
	
	protected class CondenserInventory extends InternalInventory {
        CondenserInventory() {
            super("EMCCondenserInventory", 4, 64);
        }

        @Override
        public boolean isItemValidForSlot(final int slotId, final ItemStack itemStack) {
            return Integration.emcHandler.hasEMC(itemStack) || itemStack.getItem() instanceof ItemEMCCell;
        }
    }
	
	public TileEMCCondenserBase(final ItemStack repItem) {
		super(repItem);
		internalInventory = new CondenserInventory();
        gridProxy.setFlags(GridFlags.REQUIRE_CHANNEL);
        gridProxy.setIdlePowerUsage(BlockEMCCondenser.idlePower);
	}

	@Override
	public Packet getDescriptionPacket() {
	    final NBTTagCompound nbttagcompound = new NBTTagCompound();
	    nbttagcompound.setInteger("state", state.ordinal());
	    return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, -999, nbttagcompound);
	}

	@Override
	public void onDataPacket(final NetworkManager net, final S35PacketUpdateTileEntity pkt) {
		final NBTTagCompound nbttagcompound = pkt.func_148857_g();
	    state = CondenserState.values()[nbttagcompound.getInteger("state")];
	}
	
	@Override
	public TickingRequest getTickingRequest(final IGridNode node) {
		return new TickingRequest(1, 20, false, true);
	}

	protected float getEMCPerTick() {
	    return BlockEMCCondenser.emcPerTick;
	}

	public boolean isBlocked() {
	    return state == CondenserState.BLOCKED || failedStates.contains(state);
	}
	
	protected CondenserState checkRequirements() {
		if (!gridProxy.isActive()) {			
			if (!gridProxy.meetsChannelRequirements()) {
				return CondenserState.MISSING_CHANNEL;
			}
			
			if (!gridProxy.isPowered()) {
				return CondenserState.UNPOWERED;
			}
		}
		
		return CondenserState.IDLE;
	}
	
	protected int getMaxItemsForPower(final int stackSize, final float emcValue) {
        try {
            final IEnergyGrid eGrid = GridUtils.getEnergy(getProxy());
            final double powerRequired = emcValue * stackSize * BlockEMCCondenser.activePower;
            final double powerAvail = eGrid.extractAEPower(powerRequired, Actionable.SIMULATE, PowerMultiplier.ONE);
            return (int) (powerAvail / (emcValue * BlockEMCCondenser.activePower));
        } catch (final GridAccessException e) {
            CommonUtils.debugLog("getMaxItemsForPower: Error accessing grid:", e);
            return 0;
        }
	}
}
