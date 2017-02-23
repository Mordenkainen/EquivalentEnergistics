package com.mordenkainen.equivalentenergistics.integration.ae2.grid;

import java.util.EnumSet;

import appeng.api.networking.GridFlags;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
import appeng.api.util.AEColor;
import appeng.api.util.DimensionalCoord;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

public class NullProxy implements IGridProxy {

    private final IGridProxyable tile;

    public NullProxy(final IGridProxyable tile) {
        this.tile = tile;
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
	public boolean meetsChannelRequirements() {
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

}
