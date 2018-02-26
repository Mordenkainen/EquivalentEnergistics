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
    
    public AEEMCStack(double emcValue) {
        this(emcValue, EMCStackType.VALUE);
    }
    
    public AEEMCStack(double emcValue, EMCStackType type) {
        this.emcValue = (long) (emcValue * 1000);
        this.type = type;
    }
    
    public AEEMCStack(IAEEMCStack input) {
        Preconditions.checkNotNull(input);
        emcValue = input.getStackSize();
        type = input.getType();
    }
    
    @Override
    public void add(IAEEMCStack stack) {
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
    public void decCountRequestable(long amount) {}

    @Override
    public void decStackSize(long amount) {
        emcValue -= amount;
    }

    @Override
    public IAEEMCStack empty() {
        IAEEMCStack dup = this.copy();
        dup.reset();
        return dup;
    }
    
    @Override
    public boolean fuzzyComparison(Object arg0, FuzzyMode arg1) {
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
    public void incCountRequestable(long amount) {}

    @Override
    public void incStackSize(long amount) {
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
        emcValue = 0;;
        return this;
    }

    @Override
    public IAEEMCStack setCountRequestable(long countRequestable) {
        return this;
    }

    @Override
    public IAEEMCStack setCraftable(boolean isCraftable) {
        return this;
    }

    @Override
    public IAEEMCStack setStackSize(long size) {
        emcValue = size;
        return this;
    }

    @Override
    public void writeToNBT(NBTTagCompound arg0) {}

    @Override
    public void writeToPacket(ByteBuf buff) throws IOException {
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
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof AEEMCStack)) {
            return false;
        }
        AEEMCStack other = (AEEMCStack) obj;
        return emcValue == other.emcValue && type.equals(other.type);
    }    

    @Override
    public double getEMCValue() {
        return emcValue / 1000D;
    }
    
    public static AEEMCStack fromPacket(final ByteBuf data) {
        int type = data.readByte();
        return new AEEMCStack(data.readLong(), EMCStackType.values()[type]);
    }
    
}
