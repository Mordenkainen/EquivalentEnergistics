package com.mordenkainen.equivalentenergistics.integration.ae2.storagechannel;

import java.io.IOException;

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
    public IAEEMCStack createStack(final Object input) {
        Preconditions.checkNotNull(input);
        
        if (input instanceof Number) {
            return new AEEMCStack(((Number) input).doubleValue());
        }
        
        return null;
    }

    @Override
    public IAEEMCStack poweredExtraction(final IEnergySource energy, final IMEInventory<IAEEMCStack> cell, final IAEEMCStack request, final IActionSource src) {
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
            return cell.extractItems( possible, Actionable.MODULATE, src );
        }
        
        return null;
    }

    @Override
    public IAEEMCStack poweredInsert(final IEnergySource energy, final IMEInventory<IAEEMCStack> cell, final IAEEMCStack input, final IActionSource src) {
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

            return cell.injectItems( input, Actionable.MODULATE, src );
        }

        return input;
    }

    @Override
    public IAEEMCStack readFromPacket(final ByteBuf input) throws IOException {
        Preconditions.checkNotNull(input);
        
        return AEEMCStack.fromPacket(input);
    }

}
