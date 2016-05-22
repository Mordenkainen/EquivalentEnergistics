package com.mordenkainen.equivalentenergistics.integration.ae2.grid;

import java.util.Collections;
import java.util.EnumSet;

import com.mordenkainen.equivalentenergistics.util.CommonUtils;

import appeng.api.AEApi;
import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.networking.GridFlags;
import appeng.api.networking.GridNotification;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
import appeng.api.networking.crafting.ICraftingGrid;
import appeng.api.networking.energy.IEnergyGrid;
import appeng.api.networking.events.MENetworkPowerIdleChange;
import appeng.api.networking.pathing.IPathingGrid;
import appeng.api.networking.security.ISecurityGrid;
import appeng.api.networking.security.MachineSource;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.util.AEColor;
import appeng.api.util.DimensionalCoord;

import cpw.mods.fml.common.FMLCommonHandler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import net.minecraftforge.common.util.ForgeDirection;

public class NetworkProxy implements IGridProxy {
	
	private final IGridProxyable tile;
	private final boolean worldNode;
	private final String nbtName;
	private double idleDraw;
	private ItemStack myRepInstance;
	private EnumSet<ForgeDirection> validSides;
	private EnumSet<GridFlags> flags = EnumSet.noneOf(GridFlags.class);
	private AEColor myColor = AEColor.Transparent;
	private EntityPlayer owner;
	private boolean ready;
	private IGridNode node;
	
	public NetworkProxy(final IGridProxyable _tile, final String name, final ItemStack visual, final boolean inWorld) {
		tile = _tile;
		nbtName = name;
		worldNode = inWorld;
		myRepInstance = visual;
		validSides = EnumSet.allOf(ForgeDirection.class);
	}
	
	@Override
	public double getIdlePowerUsage() {
		return idleDraw;
	}
	
	@Override
	public void setIdlePowerUsage(final double idle) {
		idleDraw = idle;

		if(node != null) {
			try {
				final IGrid grid = getGrid();
				grid.postEvent(new MENetworkPowerIdleChange(node));
			} catch(final GridAccessException e) {
				CommonUtils.debugLog("NetworkProxy:setIdlePowerUsage: Error accessing grid:", e);
			}
		}
	}

	@Override
	public EnumSet<GridFlags> getFlags() {
		return flags;
	}
	
	@Override
	public void setFlags(final GridFlags... gridFlags)	{
		final EnumSet<GridFlags> _flags = EnumSet.noneOf(GridFlags.class);

		Collections.addAll(_flags, gridFlags);

		flags = _flags;
	}

	@Override
	public boolean isWorldAccessible() {
		return worldNode;
	}

	@Override
	public DimensionalCoord getLocation() {
		return tile.getLocation();
	}

	@Override
	public AEColor getGridColor() {
		return myColor;
	}
	
	@Override
	public void setGridColor(final AEColor color) {
		myColor = color;
	}

	@Override
	public void onGridNotification(final GridNotification gridNotification) {}

	@Override
	public void setNetworkStatus(final IGrid grid, final int usedChannels) {}

	@Override
	public EnumSet<ForgeDirection> getConnectableSides() {
		return validSides;
	}
	
	@Override
	public void setConnectableSides(final EnumSet<ForgeDirection> _validSides) {
		validSides = _validSides;
		if(node != null) {
			node.updateState();
		}
	}

	@Override
	public IGridHost getMachine() {
		return tile;
	}

	@Override
	public void gridChanged() {
		tile.gridChanged();
	}

	@Override
	public ItemStack getMachineRepresentation() {
		return myRepInstance;
	}
	
	@Override
	public void setMachineRepresentation(final ItemStack stack) {
		myRepInstance = stack;
	}

	@Override
	public void writeToNBT(final NBTTagCompound tag) {
		if(node != null)	{
			node.saveToNBT(nbtName, tag);
		}
	}
	
	@Override
	public void readFromNBT(final NBTTagCompound tag) {
		if(node != null && tag.hasKey(nbtName)) {
			node.loadFromNBT(nbtName, tag);
		}
	}
	
	@Override
	public void onChunkUnload() {
		invalidate();
	}

	@Override
	public void invalidate() {
		ready = false;
		if(node != null) {
			node.destroy();
			node = null;
		}
	}
	
	@Override
	public boolean onReady() {
		ready = true;
		return getNode() != null;
	}
	
	@Override
	public boolean isReady() {
		return ready;
	}
	
	@Override
	public boolean isPowered() {
		try {
			return getEnergy().isNetworkPowered();
		} catch(final GridAccessException e) {
			CommonUtils.debugLog("NetworkProxy:isPowered: Error accessing grid:", e);
			return false;
		}
	}

	@Override
	public void setOwner(final EntityPlayer player) {
		owner = player;
		if(node != null && owner != null) {
			node.setPlayerID(AEApi.instance().registries().players().getID(owner));
		}
	}
	
	@Override
	public IGridNode getNode() {
		if(node == null && FMLCommonHandler.instance().getEffectiveSide().isServer() && ready) {
			node = AEApi.instance().createGridNode(this);
			if(owner != null) {
				node.setPlayerID(AEApi.instance().registries().players().getID(owner));
			}
			node.updateState();
		}

		return node;
	}
	
	@Override
	public boolean isActive() {
		return node != null && ready && node.isActive();
	}
	
	@Override
	public IGrid getGrid() throws GridAccessException {
		if(node == null) {
			throw new GridAccessException();
		}
		
		final IGrid grid = node.getGrid();
		
		if(grid == null) {
			throw new GridAccessException();
		}
		
		return grid;
	}
	
	@Override
	public IPathingGrid getPath() throws GridAccessException {
		final IGrid grid = getGrid();
		if(grid == null) {
			throw new GridAccessException();
		}
		
		final IPathingGrid pathingGrid = grid.getCache(IPathingGrid.class);
		
		if(pathingGrid == null) {
			throw new GridAccessException();
		}
		
		return pathingGrid;
	}

	@Override
	public IStorageGrid getStorage() throws GridAccessException {
		final IGrid grid = getGrid();
		if(grid == null) {
			throw new GridAccessException();
		}

		final IStorageGrid storageGrid = grid.getCache(IStorageGrid.class);

		if(storageGrid == null) {
			throw new GridAccessException();
		}

		return storageGrid;
	}

	@Override
	public ISecurityGrid getSecurity() throws GridAccessException {
		final IGrid grid = getGrid();
		if(grid == null) {
			throw new GridAccessException();
		}

		final ISecurityGrid securityGrid = grid.getCache(ISecurityGrid.class);

		if(securityGrid == null) {
			throw new GridAccessException();
		}

		return securityGrid;
	}

	@Override
	public ICraftingGrid getCrafting() throws GridAccessException {
		final IGrid grid = getGrid();
		if(grid == null) {
			throw new GridAccessException();
		}

		final ICraftingGrid craftingGrid = grid.getCache(ICraftingGrid.class);

		if(craftingGrid == null) {
			throw new GridAccessException();
		}

		return craftingGrid;
	}

	@Override
	public IEnergyGrid getEnergy() throws GridAccessException {
		final IGrid grid = getGrid();
		if(grid == null) {
			throw new GridAccessException();
		}
		
		final IEnergyGrid energyGrid = grid.getCache(IEnergyGrid.class);
		
		if(energyGrid == null) {
			throw new GridAccessException();
		}
		
		return energyGrid;
	}
	
	@Override
	public double getAEDemand(final double amount) {
		try {
			return getEnergy().getEnergyDemand(amount);
		} catch (GridAccessException e) {
			CommonUtils.debugLog("NetworkProxy:getAEDemand: Error accessing grid:", e);
		}
		return 0.0;
	}
	
	@Override
	public double sendAEToNet(final double amount, final Actionable mode) {
		try {
			final double overflow = getEnergy().injectPower(amount, mode);
			return mode == Actionable.SIMULATE ? overflow : 0.0;
		} catch (GridAccessException e) {
			CommonUtils.debugLog("NetworkProxy:sendAEToNet: Error accessing grid:", e);
		}
		return 0.0;
	}
	
	@Override
	public double extractAEPower(final double amount, final Actionable mode, final PowerMultiplier multiplier) {
		try {
			return getEnergy().extractAEPower(amount, mode, multiplier);
		} catch (GridAccessException e) {
			CommonUtils.debugLog("NetworkProxy:extractAEPower: Error accessing grid:", e);
		}
		return 0.0;
	}
	
	@Override
	public double getAEMaxEnergy() {
		try {
			return getEnergy().getMaxStoredPower();
		} catch (GridAccessException e) {
			CommonUtils.debugLog("NetworkProxy:getAEMaxEnergy: Error accessing grid:", e);
		}
		return 0.0;
	}
	
	@Override
	public double getAECurrentEnergy() {
		try {
			return getEnergy().getStoredPower();
		} catch (GridAccessException e) {
			CommonUtils.debugLog("NetworkProxy:getAECurrentEnergy: Error accessing grid:", e);
		}
		return 0.0;
	}

	@Override
	public boolean injectItems(final ItemStack stack, final double powerCost, final MachineSource source) {
		try {
			final IStorageGrid storageGrid = getStorage();
			
			final IAEItemStack rejected = storageGrid.getItemInventory().injectItems(AEApi.instance().storage().createItemStack(stack), Actionable.SIMULATE, source);
	
			if(rejected == null || rejected.getStackSize() == 0) {
				storageGrid.getItemInventory().injectItems(AEApi.instance().storage().createItemStack(stack), Actionable.MODULATE, source);
				if (powerCost > 0) {
					getEnergy().extractAEPower(powerCost, Actionable.MODULATE, PowerMultiplier.CONFIG);
				}
				return true;
			}
		} catch (GridAccessException e) {
			CommonUtils.debugLog("NetworkProxy:injectItems: Error accessing grid:", e);
		}
		return false;
	}
	
}
