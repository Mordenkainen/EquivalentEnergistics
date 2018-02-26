package com.mordenkainen.equivalentenergistics.integration.ae2.storagechannel;

import java.io.IOException;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.base.Preconditions;

import appeng.api.config.Actionable;
import appeng.api.networking.energy.IEnergySource;
import appeng.api.networking.security.IActionSource;
import appeng.api.storage.IMEInventory;
import appeng.api.storage.data.IItemList;
import io.netty.buffer.ByteBuf;

public class EMCStorageChannel implements IEMCStorageChannel {

    @Override
    public IItemList<IAEEMCStack> createList() {
        return new EMCItemList();
    }

    @Override
    public IAEEMCStack createStack(Object input) {
        Preconditions.checkNotNull(input);
        
        if (input instanceof Number) {
            return new AEEMCStack(((Number) input).doubleValue());
        }
        
        if (input instanceof Pair && ((Pair<?,?>) input).getLeft() instanceof Double && ((Pair<?,?>) input).getRight() instanceof EMCStackType) {
            @SuppressWarnings("unchecked")
            Pair<Double, EMCStackType> pair = (Pair<Double, EMCStackType>) input;

            return new AEEMCStack(pair.getLeft(), pair.getRight());
        }
        
        return null;
    }

    @Override
    public IAEEMCStack poweredExtraction(IEnergySource energy, IMEInventory<IAEEMCStack> cell, IAEEMCStack request, IActionSource src) {
        //TODO: FIX!!!!!!
        Preconditions.checkNotNull(energy);
        Preconditions.checkNotNull(cell);
        Preconditions.checkNotNull(request);
        Preconditions.checkNotNull(src);
        
        final IAEEMCStack possible = cell.extractItems(request.copy(), Actionable.SIMULATE, src);

        long retrieved = 0;
        if( possible != null )
        {
            retrieved = possible.getStackSize();
        }
        
        final long itemToExtract = retrieved;
        
        if( itemToExtract > 0 )
        {
            possible.setStackSize( itemToExtract );
            final IAEEMCStack ret = cell.extractItems( possible, Actionable.MODULATE, src );

            return ret;
        }
        
        return null;
    }

    @Override
    public IAEEMCStack poweredInsert(IEnergySource energy, IMEInventory<IAEEMCStack> cell, IAEEMCStack input, IActionSource src) {
        //TODO: FIX!!!!
        Preconditions.checkNotNull(energy);
        Preconditions.checkNotNull(cell);
        Preconditions.checkNotNull(input);
        Preconditions.checkNotNull(src);
        
        final IAEEMCStack possible = cell.injectItems(input.copy(), Actionable.SIMULATE, src);

        long stored = input.getStackSize();
        if( possible != null )
        {
            stored -= possible.getStackSize();
        }

        final long itemToAdd = stored;

        if( itemToAdd > 0 )
        {
            if( itemToAdd < input.getStackSize() )
            {
                final IAEEMCStack split = input.copy();
                split.decStackSize( itemToAdd );
                input.setStackSize( itemToAdd );
                split.add( cell.injectItems( input, Actionable.MODULATE, src ) );

                return split;
            }

            final IAEEMCStack ret = cell.injectItems( input, Actionable.MODULATE, src );

            return ret;
        }

        return input;
    }

    @Override
    public IAEEMCStack readFromPacket(ByteBuf input) throws IOException {
        Preconditions.checkNotNull(input);
        
        return AEEMCStack.fromPacket(input);
    }

}
