package com.mordenkainen.equivalentenergistics.integration.ae2.grid;

import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
import appeng.api.networking.security.IActionHost;
import appeng.api.util.AECableType;
import appeng.api.util.DimensionalCoord;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

public interface IGridProxyable extends IGridHost, IActionHost {
	
	default boolean isActive() {
		if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
			return false;
		}
		
		if (getProxy().getNode() == null) {
			return false;
		} else {
			return getProxy().getNode().isActive();
		}
	}
	
	default void setOwner(final EntityPlayer player) {
		getProxy().setOwner(player);
	}
	
	default void onChunkUnload() {
		getProxy().onChunkUnload();
	}
	
	default void invalidate() {
		getProxy().invalidate();
	}
	
	default void validate() {
		if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
			return;
		}
		getProxy().onReady();
	}
	
	default void readFromNBT(final NBTTagCompound data) {
		getProxy().readFromNBT(data);
	}
	
	default void writeToNBT(final NBTTagCompound data) {
		getProxy().writeToNBT(data);
	}
	
	@Override
	default IGridNode getGridNode(final ForgeDirection arg0) {
		return getProxy().getNode();
	}
	
	@Override
	default AECableType getCableConnectionType(final ForgeDirection arg0) {
		return AECableType.SMART;
	}
	
	@Override
	default IGridNode getActionableNode() {
		return getProxy().getNode();
	}
	
	IGridProxy getProxy();

	DimensionalCoord getLocation();

	void gridChanged();
	
}
