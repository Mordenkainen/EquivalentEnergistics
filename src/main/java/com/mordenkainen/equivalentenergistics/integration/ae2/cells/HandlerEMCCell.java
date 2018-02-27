package com.mordenkainen.equivalentenergistics.integration.ae2.cells;

import com.mordenkainen.equivalentenergistics.integration.ae2.storagechannel.IAEEMCStack;
import com.mordenkainen.equivalentenergistics.integration.ae2.storagechannel.IEMCStorageChannel;
import com.mordenkainen.equivalentenergistics.util.EMCPool;

import appeng.api.AEApi;
import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.storage.ISaveProvider;
import appeng.api.storage.data.IItemList;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class HandlerEMCCell extends HandlerEMCCellBase {

    private static final String EMC_TAG = "emc";

    private final NBTTagCompound cellData;
    private final EMCPool pool = new EMCPool();

    public HandlerEMCCell(final ItemStack storageStack, final ISaveProvider saveProvider, final double cellCapacities) {
        super(saveProvider);
        if (!storageStack.hasTagCompound()) {
            storageStack.setTagCompound(new NBTTagCompound());
        }

        pool.setMaxEMC(cellCapacities);
        cellData = storageStack.getTagCompound();
        if (cellData.hasKey(EMC_TAG)) {
            pool.setCurrentEMC(cellData.getLong(EMC_TAG));
        }
    }

    @Override
    public int getCellStatus() {
        if (pool.getCurrentEMC() >= pool.getMaxEMC()) {
            return 3;
        }
        if (pool.getCurrentEMC() >= pool.getMaxEMC() * 0.75) {
            return 2;
        }
        return 1;
    }

    private void updateEMC() {
        cellData.setDouble(EMC_TAG, pool.getCurrentEMC());
        if (saveProvider != null) {
            saveProvider.saveChanges(this);
        }
    }

    @Override
    public IAEEMCStack extractItems(final IAEEMCStack request, final Actionable mode, final IActionSource src) {
        if (request == null) {
            return null;
        }
        
        final double toExtract = Math.min(pool.getCurrentEMC(), request.getEMCValue());
        if (mode == Actionable.MODULATE) {
            pool.extractEMC(toExtract);
            updateEMC();
        }
        
        if (toExtract > 0) {
            final IAEEMCStack extracted = request.copy();
            extracted.setStackSize((long) (toExtract * 1000));
            return extracted;
        }
        
        return null;
    }

    @Override
    public IItemList<IAEEMCStack> getAvailableItems(final IItemList<IAEEMCStack> stacks) {
        if(pool.getCurrentEMC() > 0) {
            final IAEEMCStack current = AEApi.instance().storage().getStorageChannel(IEMCStorageChannel.class).createStack(pool.getCurrentEMC());
            stacks.add(current);
        }
        
        return stacks;
    }

    @Override
    public IAEEMCStack injectItems(final IAEEMCStack input, final Actionable mode, final IActionSource src) {
        if (input == null || input.getStackSize() == 0) {
            return null;
        }
        
        final double toStore = Math.min(input.getEMCValue(), pool.getAvail());
        if (mode == Actionable.MODULATE) {
            pool.addEMC(toStore);
            updateEMC();
        }
        
        if (toStore >= input.getEMCValue()) {
            return null;
        }
        
        final IAEEMCStack remainder = input.copy();
        remainder.setStackSize(input.getStackSize() - (long) (toStore * 1000));
        
        return remainder;
    }
    
    /*private void updateProvider() {
        if(saveProvider instanceof TileEntity) {
            final TileEntity tile = (TileEntity) saveProvider;
            tile.getWorld().notifyBlockUpdate(tile.getPos(), tile.getWorld().getBlockState(tile.getPos()), tile.getWorld().getBlockState(tile.getPos()), 1);
        }
        
    }*/

}
