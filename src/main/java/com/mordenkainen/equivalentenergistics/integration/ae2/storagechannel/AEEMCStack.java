package com.mordenkainen.equivalentenergistics.integration.ae2.storagechannel;

import java.io.IOException;

import com.google.common.base.Preconditions;
import com.mordenkainen.equivalentenergistics.items.ModItems;

import appeng.api.AEApi;
import appeng.api.config.FuzzyMode;
import appeng.api.storage.IStorageChannel;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class AEEMCStack implements IAEEMCStack {

    private long emcValue;
    private EMCStackType type;
    
    public AEEMCStack(final double emcValue) {
        this(emcValue, EMCStackType.VALUE);
    }
    
    public AEEMCStack(final double emcValue, final EMCStackType type) {
        this.emcValue = (long) (emcValue * 1000);
        this.type = type;
    }
    
    public AEEMCStack(final IAEEMCStack input) {
        Preconditions.checkNotNull(input);
        emcValue = input.getStackSize();
        type = input.getType();
    }
    
    @Override
    public void add(final IAEEMCStack stack) {
        if (stack == null) {
            return;
        }
        
        incStackSize(stack.getStackSize());
    }

    @Override
    public ItemStack asItemStackRepresentation() {
        return new ItemStack(ModItems.CRYSTAL, 1, 2);
    }

    @Override
    public IAEEMCStack copy() {
        return new AEEMCStack(this);
    }

    @Override
    public void decCountRequestable(final long amount) {}

    @Override
    public void decStackSize(final long amount) {
        emcValue -= amount;
    }

    @Override
    public IAEEMCStack empty() {
        final IAEEMCStack dup = this.copy();
        dup.reset();
        return dup;
    }
    
    @Override
    public boolean fuzzyComparison(final Object arg0, final FuzzyMode arg1) {
        return false;
    }

    @Override
    public IStorageChannel<IAEEMCStack> getChannel() {
        return AEApi.instance().storage().getStorageChannel(IEMCStorageChannel.class);
    }

    @Override
    public long getCountRequestable() {
        return 0;
    }

    @Override
    public long getStackSize() {
        return emcValue;
    }

    @Override
    public void incCountRequestable(final long amount) {}

    @Override
    public void incStackSize(final long amount) {
        emcValue += amount;
    }

    @Override
    public boolean isCraftable() {
        return false;
    }

    @Override
    public boolean isFluid() {
        return false;
    }

    @Override
    public boolean isItem() {
        return false;
    }

    @Override
    public boolean isMeaningful() {
        return true;
    }

    @Override
    public IAEEMCStack reset() {
        emcValue = 0;
        return this;
    }

    @Override
    public IAEEMCStack setCountRequestable(final long countRequestable) {
        return this;
    }

    @Override
    public IAEEMCStack setCraftable(final boolean isCraftable) {
        return this;
    }

    @Override
    public IAEEMCStack setStackSize(final long size) {
        emcValue = size;
        return this;
    }

    @Override
    public void writeToNBT(final NBTTagCompound arg0) {}

    @Override
    public void writeToPacket(final ByteBuf buff) throws IOException {
        buff.writeByte(type.ordinal());
        buff.writeLong(emcValue);
    }

    @Override
    public EMCStackType getType() {
        return type;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (emcValue ^ (emcValue >>> 32));
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof AEEMCStack)) {
            return false;
        }
        final AEEMCStack other = (AEEMCStack) obj;
        return emcValue == other.emcValue && type.equals(other.type);
    }    

    @Override
    public double getEMCValue() {
        return emcValue / 1000D;
    }
    
    public static AEEMCStack fromPacket(final ByteBuf data) {
        final int type = data.readByte();
        return new AEEMCStack(data.readLong(), EMCStackType.values()[type]);
    }
    
}
