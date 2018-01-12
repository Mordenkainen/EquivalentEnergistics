package com.mordenkainen.equivalentenergistics.blocks.base.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;


public abstract class EqETileBase extends TileEntity {

    public void onChunkLoad() {
        if(isInvalid()) {
            validate();
        }
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        final NBTTagCompound tag = writeToNBT(new NBTTagCompound());
        getPacketData(tag);
        return tag;
    }

    @Override
    public void handleUpdateTag(final NBTTagCompound tag) {
        readFromNBT(tag);
        readPacketData(tag);
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        final NBTTagCompound nbttagcompound = new NBTTagCompound();
        getPacketData(nbttagcompound);
        return new SPacketUpdateTileEntity(pos, -999, nbttagcompound);
    }

    @Override
    public void onDataPacket(final NetworkManager net, final SPacketUpdateTileEntity pkt) {
        final NBTTagCompound nbttagcompound = pkt.getNbtCompound();
        if (readPacketData(nbttagcompound)) {
            markForUpdate();
        }
    }

    public void markForUpdate() {
        if (getWorld() != null) {
            getWorld().notifyBlockUpdate(pos, getWorld().getBlockState(pos) , getWorld().getBlockState(pos), 3);
            getWorld().notifyNeighborsOfStateChange(pos, getWorld().getBlockState(pos).getBlock(), true);
        }
    }

    public abstract void onReady();

    protected abstract boolean readPacketData(NBTTagCompound nbttagcompound);

    protected abstract void getPacketData(NBTTagCompound nbttagcompound);

}
