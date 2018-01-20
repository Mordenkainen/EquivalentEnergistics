package com.mordenkainen.equivalentenergistics.blocks.base.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;


public abstract class EqETileBase extends TileEntity {

    public void onChunkLoad() {
        if(isInvalid()) {
            validate();
        }
    }
    
    @Override
    public Packet getDescriptionPacket() {
        final NBTTagCompound nbttagcompound = new NBTTagCompound();
        getPacketData(nbttagcompound);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, -999, nbttagcompound);
    }

    @Override
    public void onDataPacket(final NetworkManager net, final S35PacketUpdateTileEntity pkt) {
        final NBTTagCompound nbttagcompound = pkt.func_148857_g();
        if (readPacketData(nbttagcompound)) {
            markForUpdate();
        }
    }

    public void markForUpdate() {
        if (worldObj != null) {
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }
    }

    public abstract void onReady();
    
    protected abstract boolean readPacketData(NBTTagCompound nbttagcompound);
    
    protected abstract void getPacketData(NBTTagCompound nbttagcompound);
    
}
