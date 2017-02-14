package com.mordenkainen.equivalentenergistics.tiles;

import java.util.EnumSet;

import com.mordenkainen.equivalentenergistics.blocks.BlockEMCCondenser;
import com.mordenkainen.equivalentenergistics.integration.Integration;
import com.mordenkainen.equivalentenergistics.integration.ae2.grid.GridAccessException;
import com.mordenkainen.equivalentenergistics.integration.ae2.grid.GridUtils;
import com.mordenkainen.equivalentenergistics.integration.ae2.tiles.TileAEInv;
import com.mordenkainen.equivalentenergistics.integration.waila.IWailaNBTProvider;
import com.mordenkainen.equivalentenergistics.items.ItemEMCCell;
import com.mordenkainen.equivalentenergistics.registries.BlockEnum;
import com.mordenkainen.equivalentenergistics.util.CommonUtils;
import com.mordenkainen.equivalentenergistics.util.inventory.IInvChangeNotifier;
import com.mordenkainen.equivalentenergistics.util.inventory.InternalInventory;

import appeng.api.config.Actionable;
import appeng.api.networking.GridFlags;
import appeng.api.networking.IGridNode;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;

public class TileCondenserNew extends TileAEInv implements IWailaNBTProvider, IGridTickable, IInvChangeNotifier {

	protected float currentEMC;
	public CondenserState state = CondenserState.IDLE;
	protected boolean sleeping;
	private final EnumSet<CondenserState> failedStates = EnumSet.of(CondenserState.MISSING_CHANNEL, CondenserState.NO_NETWORK, CondenserState.UNPOWERED);
	
    public TileCondenserNew() {
		super(new ItemStack(Item.getItemFromBlock(BlockEnum.EMCCONDENSER.getBlock())));
		internalInventory = new CondenserInventory();
        gridProxy.setFlags(GridFlags.REQUIRE_CHANNEL);
        gridProxy.setIdlePowerUsage(BlockEMCCondenser.idlePower);
        sleeping = false;
	}
    
	//TODO: Better Waila info: Channel errors, power issues, current inventory, other issues, etc.
	@Override
    public NBTTagCompound getWailaTag(final NBTTagCompound tag) {
		if (currentEMC > 0) {
			tag.setFloat("currentEMC", currentEMC);
		}
        return tag;
    }

    @Override
    public void readFromNBT(final NBTTagCompound data) {
        super.readFromNBT(data);
        if (data.hasKey("CurrentEMC")) {
        	currentEMC = data.getFloat("CurrentEMC");
        }
    }

    @Override
    public void writeToNBT(final NBTTagCompound data) {
        super.writeToNBT(data);
        if (currentEMC > 0) {
        	data.setFloat("CurrentEMC", currentEMC);
        }
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
    
    @Override
	public void onChangeInventory() {
	    if (sleeping) {
	        try {
	        	GridUtils.getTick(getProxy()).alertDevice(getActionableNode());
	        } catch (final GridAccessException e) {
	            CommonUtils.debugLog("onChangeInventory: Error accessing grid:", e);
	        }
	    }
	}

    @Override
	public TickingRequest getTickingRequest(final IGridNode node) {
		return new TickingRequest(1, 20, sleeping, true);
	}

	@Override
	public TickRateModulation tickingRequest(final IGridNode node, final int ticksSinceLast) {
		sleeping = false;
		CondenserState newState = state;
		
		newState = checkRequirements();
		
		if (newState != state) {
			state = newState;
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			return TickRateModulation.IDLE;
		}
		
		if (failedStates.contains(state)) {
			return TickRateModulation.IDLE;
		}
		
		if (currentEMC > 0) {
			newState = injectExcessEMC();
			if (newState != state) {
				state = newState;
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
				return TickRateModulation.IDLE;
			}
		}
		
		if (!getInventory().isEmpty()) {
			newState = processInv();
			if (newState != state) {
				state = newState;
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			}
			return state == CondenserState.BLOCKED ? TickRateModulation.IDLE : TickRateModulation.URGENT;
		}
		
		sleeping = true;
		return TickRateModulation.SLEEP;
	}

	private CondenserState injectExcessEMC() {
		try {
			final float oldEMC = currentEMC;
			currentEMC -= GridUtils.getEMCStorage(getProxy()).injectEMC(currentEMC, Actionable.MODULATE);
			if (oldEMC != currentEMC) {
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			}
		} catch (final GridAccessException e) {
            CommonUtils.debugLog("tickingRequest: Error accessing grid:", e);
        }

		return currentEMC > 0 ? CondenserState.BLOCKED : state;
	}

	private CondenserState checkRequirements() {
		if (!gridProxy.isActive()) {
			return CondenserState.NO_NETWORK;
		}
		
		if (!gridProxy.meetsChannelRequirements()) {
			return CondenserState.MISSING_CHANNEL;
		}
		
		if (!gridProxy.isPowered()) {
			return CondenserState.UNPOWERED;
		}
		
		return state;
	}

	private CondenserState processInv() {
		return null;
	}

	protected enum CondenserState {
        IDLE, ACTIVE, BLOCKED, UNPOWERED, MISSING_CHANNEL, NO_NETWORK;
    }
	
	protected class CondenserInventory extends InternalInventory {
        CondenserInventory() {
            super("EMCCondenserInventory", 4, 64, TileCondenserNew.this);
        }

        @Override
        public boolean isItemValidForSlot(final int slotId, final ItemStack itemStack) {
            return Integration.emcHandler.hasEMC(itemStack) || itemStack.getItem() instanceof ItemEMCCell;
        }
    }
	
}
