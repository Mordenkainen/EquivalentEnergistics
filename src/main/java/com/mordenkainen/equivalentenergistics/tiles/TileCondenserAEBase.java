package com.mordenkainen.equivalentenergistics.tiles;

import com.mordenkainen.equivalentenergistics.blocks.BlockEMCCondenser;
import com.mordenkainen.equivalentenergistics.integration.Integration;
import com.mordenkainen.equivalentenergistics.integration.ae2.grid.GridAccessException;
import com.mordenkainen.equivalentenergistics.integration.ae2.grid.IGridProxy;
import com.mordenkainen.equivalentenergistics.integration.ae2.grid.IGridProxyable;
import com.mordenkainen.equivalentenergistics.integration.ae2.grid.NetworkProxy;
import com.mordenkainen.equivalentenergistics.integration.ae2.grid.NullProxy;
import com.mordenkainen.equivalentenergistics.items.ItemEMCCell;
import com.mordenkainen.equivalentenergistics.util.CommonUtils;
import com.mordenkainen.equivalentenergistics.util.inventory.IInvChangeNotifier;
import com.mordenkainen.equivalentenergistics.util.inventory.InternalInventory;

import appeng.api.AEApi;
import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.networking.GridFlags;
import appeng.api.networking.IGridNode;
import appeng.api.networking.energy.IEnergyGrid;
import appeng.api.networking.security.MachineSource;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.util.DimensionalCoord;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import cpw.mods.fml.common.FMLCommonHandler;

public abstract class TileCondenserAEBase extends TileCondenserBase implements IGridProxyable, IGridTickable, IInvChangeNotifier {

	protected final CondenserInventory internalInventory;
	private IGridProxy gridProxy;
	protected MachineSource mySource;
	private boolean sleeping;
	
	public TileCondenserAEBase(final ItemStack repItem) {
		super();
		internalInventory = new CondenserInventory();
		mySource = new MachineSource(this);
		if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
			gridProxy = new NullProxy(this);
		} else {
			gridProxy = new NetworkProxy(this, "node0", repItem, true);
		}
		gridProxy.setFlags(GridFlags.REQUIRE_CHANNEL);
		gridProxy.setIdlePowerUsage(BlockEMCCondenser.idlePower);
		sleeping = false;
	}
	
	@Override
	public void onChunkUnload() {
		super.onChunkUnload();
		IGridProxyable.super.onChunkUnload();
	}
	
	@Override
	public void invalidate() {
		super.invalidate();
		IGridProxyable.super.invalidate();
	}
	
	@Override
	public void validate() {
		super.validate();
		IGridProxyable.super.validate();
	}
	
	@Override
	public void readFromNBT(final NBTTagCompound data) {
		super.readFromNBT(data);
		IGridProxyable.super.readFromNBT(data);
	}
	
	@Override
	public void writeToNBT(final NBTTagCompound data) {
		super.writeToNBT(data);
		IGridProxyable.super.writeToNBT(data);
	}
	
	@Override
	public TickingRequest getTickingRequest(final IGridNode node) {
		return new TickingRequest(1, 20, sleeping, true);
	}
	
	@Override
	public TickRateModulation tickingRequest(final IGridNode node, final int ticks) {
		final CondenserState newState = condense();
		if (newState != state) {
			state = newState;
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}

		if (state == CondenserState.Idle) {
			sleeping = true;
			return TickRateModulation.SLEEP;
		} else {
			sleeping = false;
			return TickRateModulation.URGENT;
		}
	}
	
	@Override
	public void securityBreak() {
		CommonUtils.destroyAndDrop(worldObj, xCoord, yCoord, zCoord);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends InternalInventory> T getInventory() {
		return (T) internalInventory;
	}

	@Override
	public IGridProxy getProxy() {
		return gridProxy;
	}

	@Override
	public DimensionalCoord getLocation() {
		return new DimensionalCoord(this);
	}

	@Override
	public void gridChanged() {}

	@Override
	protected ItemStack ejectStack(final ItemStack stack) {
		final IAEItemStack toInject = AEApi.instance().storage().createItemStack(stack.copy());
		try {
			final IStorageGrid storageGrid = getProxy().getStorage();
			
			IAEItemStack rejected = storageGrid.getItemInventory().injectItems(toInject, Actionable.SIMULATE, mySource);
	
			long stored = toInject.getStackSize();
			if (rejected != null) {
				stored -= rejected.getStackSize();
			}
			
			final IEnergyGrid eGrid = getProxy().getEnergy();
			final double availablePower = eGrid.extractAEPower(stored, Actionable.SIMULATE, PowerMultiplier.CONFIG);

			final long itemToAdd = Math.min((long) (availablePower + 0.9), stored);
			
			if (itemToAdd > 0)	{
				eGrid.extractAEPower(stored, Actionable.MODULATE, PowerMultiplier.CONFIG);
				
				if (itemToAdd < toInject.getStackSize()) {
					final IAEItemStack split = toInject.copy();
					split.decStackSize(itemToAdd);
					toInject.setStackSize(itemToAdd);
					split.add(storageGrid.getItemInventory().injectItems(toInject, Actionable.MODULATE, mySource));

					return split.getItemStack();
				}
				
				rejected = storageGrid.getItemInventory().injectItems(toInject, Actionable.MODULATE, mySource);
				
				return rejected == null ? null : rejected.getItemStack();
			}
		} catch (GridAccessException e) {
			CommonUtils.debugLog("ejectStack: Error accessing grid:", e);
		}
		
		return stack;
	}

	@Override
	protected boolean ejectEMC() {
		try	{
			if (currentEMC > 0) {
				final float toInject = Math.min(currentEMC, getEMCPerTick());
				float ejected = getProxy().getEMCStorage().injectEMC(toInject, Actionable.MODULATE);
				if (ejected > 0) {
					currentEMC -= ejected;
					return true;
				}
				return false;
			}
			
		} catch(GridAccessException e) {
			CommonUtils.debugLog("ejectEMC: Error accessing grid:", e);
		}
		return false;
	}
	
	@Override
	public void onChangeInventory() {
		if (sleeping) {
			try {
				getProxy().getTick().alertDevice(getActionableNode());
			} catch (GridAccessException e) {
				CommonUtils.debugLog("onChangeInventory: Error accessing grid:", e);
			}
		}
	}
	
	protected abstract int getSlotCount();
	
	protected abstract float getEMCPerTick();

	protected class CondenserInventory extends InternalInventory {
		CondenserInventory() {
			super("EMCCondenserInventory", getSlotCount(), 64, TileCondenserAEBase.this); 
		}

		@Override
		public boolean isItemValidForSlot(final int slotId, final ItemStack itemStack) {
			return Integration.emcHandler.hasEMC(itemStack) || itemStack.getItem() instanceof ItemEMCCell;
		}
	}
	
}
