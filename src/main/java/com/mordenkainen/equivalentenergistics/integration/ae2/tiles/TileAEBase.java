package com.mordenkainen.equivalentenergistics.integration.ae2.tiles;

import com.mordenkainen.equivalentenergistics.integration.ae2.grid.GridAccessException;
import com.mordenkainen.equivalentenergistics.integration.ae2.grid.GridUtils;
import com.mordenkainen.equivalentenergistics.integration.ae2.grid.IGridProxy;
import com.mordenkainen.equivalentenergistics.integration.ae2.grid.IGridProxyable;
import com.mordenkainen.equivalentenergistics.util.CommonUtils;

import appeng.api.config.SecurityPermissions;
import appeng.api.networking.security.ISecurityGrid;
import appeng.api.networking.security.MachineSource;
import appeng.api.util.DimensionalCoord;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

public abstract class TileAEBase extends TileEntity implements IGridProxyable {

    protected final IGridProxy gridProxy;
    protected MachineSource mySource;

    public TileAEBase(final ItemStack repItem) {
        super();
        mySource = new MachineSource(this);
        gridProxy = IGridProxy.getDefaultProxy(repItem, this);
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
    public IGridProxy getProxy() {
        return gridProxy;
    }

    @Override
    public DimensionalCoord getLocation() {
        return new DimensionalCoord(this);
    }

    @Override
    public void securityBreak() {
        CommonUtils.destroyAndDrop(worldObj, xCoord, yCoord, zCoord);
    }

    @Override
    public void gridChanged() {}

    @Override
    public Packet getDescriptionPacket() {
        final NBTTagCompound nbttagcompound = new NBTTagCompound();
        getPacketData(nbttagcompound);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, -999, nbttagcompound);
    }

    @Override
    public void onDataPacket(final NetworkManager net, final S35PacketUpdateTileEntity pkt) {
        final NBTTagCompound nbttagcompound = pkt.func_148857_g();
        readPacketData(nbttagcompound);
    }
    
    protected boolean checkPermissions(final EntityPlayer player) {
        try {
            final ISecurityGrid sGrid = GridUtils.getSecurity(getProxy());

            return sGrid.hasPermission(player, SecurityPermissions.INJECT) && sGrid.hasPermission(player, SecurityPermissions.EXTRACT) && sGrid.hasPermission(player, SecurityPermissions.BUILD);
        } catch (final GridAccessException e) {
            CommonUtils.debugLog("TileAEBase:checkPermissions: Error accessing grid:", e);
        }
        return true;
    }

    protected abstract void getPacketData(final NBTTagCompound nbttagcompound);
    
    protected abstract void readPacketData(final NBTTagCompound nbttagcompound);
}
