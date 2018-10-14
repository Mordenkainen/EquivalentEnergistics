package com.mordenkainen.equivalentenergistics.integration.ae2.storagechannel;

import java.io.IOException;

import com.google.common.base.Preconditions;

import appeng.api.storage.data.IItemList;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;

public class EMCStorageChannel implements IEMCStorageChannel {

    @Override
    public IItemList<IAEEMCStack> createList() {
        return new EMCItemList();
    }

    @Override
    public IAEEMCStack createStack(final Object input) {
        Preconditions.checkNotNull(input);
        
        if (input instanceof Number) {
            return new AEEMCStack(((Number) input).doubleValue());
        }
        
        return null;
    }

    @Override
    public IAEEMCStack readFromPacket(final ByteBuf input) throws IOException {
        Preconditions.checkNotNull(input);
        
        return AEEMCStack.fromPacket(input);
    }

    @Override
    public IAEEMCStack createFromNBT(final NBTTagCompound arg0) {
        Preconditions.checkNotNull(arg0);
        
        return AEEMCStack.fromNBT(arg0);
    }

}
