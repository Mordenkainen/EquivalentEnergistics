package com.mordenkainen.equivalentenergistics.integration.ae2.cells;

import com.mordenkainen.equivalentenergistics.items.ItemEMCCellCreative;

import appeng.api.storage.ISaveProvider;

public class HandlerEMCCellCreative extends HandlerEMCCellBase {

    public HandlerEMCCellCreative(final ISaveProvider saveProvider) {
        super(saveProvider);
    }
    
    @Override
    public int getCellStatus() {
        return 1;
    }
    
    @Override
    public float getCurrentEMC() {
        return ItemEMCCellCreative.capacity / 2;
    }
    
    @Override
    public void setCurrentEMC(final float currentEMC) {}

    @Override
    public float getMaxEMC() {
        return ItemEMCCellCreative.capacity;
    }
    
    @Override
    public void setMaxEMC(final float maxEMC) {}
    
    @Override
    public float getAvail() {
        return ItemEMCCellCreative.capacity / 2;
    }
    
    @Override
    public boolean isFull() {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
    
    @Override
    public float addEMC(final float amount) {
        return amount;
    }

    @Override
    public float extractEMC(final float amount) {
        return amount;
    }

}
