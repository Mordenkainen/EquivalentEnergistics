package com.mordenkainen.equivalentenergistics.integration.ae2.grid;

import java.util.EnumSet;

import com.mordenkainen.equivalentenergistics.integration.ae2.cache.EMCStorageGrid;

import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.networking.GridFlags;
import appeng.api.networking.GridNotification;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
import appeng.api.networking.crafting.ICraftingGrid;
import appeng.api.networking.energy.IEnergyGrid;
import appeng.api.networking.pathing.IPathingGrid;
import appeng.api.networking.security.ISecurityGrid;
import appeng.api.networking.security.MachineSource;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.networking.ticking.ITickManager;
import appeng.api.util.AEColor;
import appeng.api.util.DimensionalCoord;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import net.minecraftforge.common.util.ForgeDirection;

public class NullProxy implements IGridProxy {

	private final IGridProxyable tile;
	
	public NullProxy(final IGridProxyable _tile) {
		tile = _tile;
	}
	
	@Override
	public double getIdlePowerUsage() {
		return 0;
	}

	@Override
	public void setIdlePowerUsage(final double idle) {}

	@Override
	public EnumSet<GridFlags> getFlags() {
		return null;
	}

	@Override
	public void setFlags(final GridFlags... gridFlags) {}

	@Override
	public boolean isWorldAccessible() {
		return false;
	}

	@Override
	public DimensionalCoord getLocation() {
		return tile.getLocation();
	}

	@Override
	public AEColor getGridColor() {
		return null;
	}

	@Override
	public void setGridColor(final AEColor color) {}

	@Override
	public void onGridNotification(final GridNotification gridNotification) {}

	@Override
	public void setNetworkStatus(final IGrid grid, final int usedChannels) {}

	@Override
	public EnumSet<ForgeDirection> getConnectableSides() {
		return null;
	}

	@Override
	public void setConnectableSides(final EnumSet<ForgeDirection> validSides) {}

	@Override
	public IGridHost getMachine() {
		return null;
	}

	@Override
	public void gridChanged() {}

	@Override
	public ItemStack getMachineRepresentation() {
		return null;
	}

	@Override
	public void setMachineRepresentation(final ItemStack stack) {}

	@Override
	public void writeToNBT(final NBTTagCompound tag) {}

	@Override
	public void readFromNBT(final NBTTagCompound tag) {}

	@Override
	public void onChunkUnload() {}

	@Override
	public void invalidate() {}

	@Override
	public boolean onReady() {
		return false;
	}

	@Override
	public boolean isReady() {
		return false;
	}

	@Override
	public boolean isPowered() {
		return false;
	}

	@Override
	public void setOwner(final EntityPlayer player) {}

	@Override
	public IGridNode getNode() {
		return null;
	}

	@Override
	public boolean isActive() {
		return false;
	}

	@Override
	public IGrid getGrid() throws GridAccessException {
		throw new GridAccessException();
	}

	@Override
	public IPathingGrid getPath() throws GridAccessException {
		throw new GridAccessException();
	}

	@Override
	public IStorageGrid getStorage() throws GridAccessException {
		throw new GridAccessException();
	}

	@Override
	public ISecurityGrid getSecurity() throws GridAccessException {
		throw new GridAccessException();
	}

	@Override
	public ICraftingGrid getCrafting() throws GridAccessException {
		throw new GridAccessException();
	}

	@Override
	public IEnergyGrid getEnergy() throws GridAccessException {
		throw new GridAccessException();
	}
	
	@Override
	public ITickManager getTick() throws GridAccessException {
		throw new GridAccessException();
	}
	
	@Override
	public EMCStorageGrid getEMCStorage() throws GridAccessException {
		throw new GridAccessException();
	}

	@Override
	public double getAEDemand(final double amount) {
		return 0;
	}

	@Override
	public double sendAEToNet(final double amount, final Actionable mode) {
		return 0;
	}
	
	@Override
	public double extractAEPower(final double amount, final Actionable mode, final PowerMultiplier multiplier) {
		return 0;
	}

	@Override
	public double getAEMaxEnergy() {
		return 0;
	}

	@Override
	public double getAECurrentEnergy() {
		return 0;
	}

	@Override
	public boolean injectItems(final ItemStack stack, final double powerCost, final MachineSource source) {
		return false;
	}
	
}
