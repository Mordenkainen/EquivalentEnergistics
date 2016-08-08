package com.mordenkainen.equivalentenergistics.integration.ae2.tiles;

import com.mordenkainen.equivalentenergistics.integration.ae2.grid.IGridProxy;
import com.mordenkainen.equivalentenergistics.integration.ae2.grid.IGridProxyable;
import com.mordenkainen.equivalentenergistics.integration.ae2.grid.NetworkProxy;
import com.mordenkainen.equivalentenergistics.integration.ae2.grid.NullProxy;
import com.mordenkainen.equivalentenergistics.util.CommonUtils;

import appeng.api.networking.security.MachineSource;
import appeng.api.util.DimensionalCoord;

import cpw.mods.fml.common.FMLCommonHandler;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public abstract class TileNetworkBase extends TileEntity implements IGridProxyable {
	
	protected IGridProxy gridProxy;
	protected MachineSource mySource;
	
	public TileNetworkBase(final ItemStack repItem) {
		super();
		mySource = new MachineSource(this);
		if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
			gridProxy = new NullProxy(this);
		} else {
			gridProxy = new NetworkProxy(this, "node0", repItem, true);
		}
	}
	
	// TileEntity Overrides
	// ------------------------
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
	// -----------------------

	// IGridHost Overrides
	// ------------------------
	@Override
	public void securityBreak() {
		CommonUtils.destroyAndDrop(worldObj, xCoord, yCoord, zCoord);
	}
	// ------------------------

	// IGridProxyable Overrides
	// ------------------------
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
	// ------------------------
	
}
