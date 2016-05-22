package com.mordenkainen.equivalentenergistics.integration.ae2.tiles;

import com.mordenkainen.equivalentenergistics.integration.ae2.grid.IGridProxy;
import com.mordenkainen.equivalentenergistics.integration.ae2.grid.IGridProxyable;
import com.mordenkainen.equivalentenergistics.integration.ae2.grid.NetworkProxy;
import com.mordenkainen.equivalentenergistics.integration.ae2.grid.NullProxy;
import com.mordenkainen.equivalentenergistics.util.CommonUtils;

import appeng.api.networking.IGridNode;
import appeng.api.networking.security.IActionHost;
import appeng.api.networking.security.MachineSource;
import appeng.api.util.AECableType;
import appeng.api.util.DimensionalCoord;

import cpw.mods.fml.common.FMLCommonHandler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

import net.minecraftforge.common.util.ForgeDirection;

public abstract class TileNetworkBase extends TileEntity implements IGridProxyable, IActionHost {
	
	protected IGridProxy gridProxy;
	protected MachineSource mySource;
	private boolean active;
	
	public TileNetworkBase(final ItemStack repItem) {
		super();
		mySource = new MachineSource(this);
		if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
			gridProxy = new NullProxy(this);
		} else {
			gridProxy = new NetworkProxy(this, "node0", repItem, true);
		}
	}
	
	public void setOwner(final EntityPlayer player) {
		gridProxy.setOwner(player);
	}
	
	public boolean isActive() {
		if(!worldObj.isRemote && gridProxy.getNode() != null) {
			active = gridProxy.getNode().isActive();
		}

		return active;
	}
	
	@Override
	public void onChunkUnload() {
		super.onChunkUnload();
		gridProxy.onChunkUnload();
	}
	
	@Override
	public void invalidate() {
		super.invalidate();
		gridProxy.invalidate();
	}
	
	@Override
	public void validate() {
		super.validate();
		if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
			return;
		}
		gridProxy.onReady();
	}
	
	@Override
	public void readFromNBT(final NBTTagCompound data) {
		super.readFromNBT(data);
		gridProxy.readFromNBT(data);
	}
	
	@Override
	public void writeToNBT(final NBTTagCompound data) {
		super.writeToNBT(data);
		gridProxy.writeToNBT(data);
	}
	
	@Override
	public IGridNode getGridNode(final ForgeDirection arg0) {
		return gridProxy.getNode();
	}

	@Override
	public AECableType getCableConnectionType(final ForgeDirection arg0) {
		return AECableType.SMART;
	}

	@Override
	public void securityBreak() {
		CommonUtils.destroyAndDrop(worldObj, xCoord, yCoord, zCoord);
	}

	@Override
	public IGridNode getActionableNode() {
		return gridProxy.getNode();
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
	
}
