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
    public double getCurrentEMC() {
        return ItemEMCCellCreative.capacity / 2;
    }
    
    @Override
    public void setCurrentEMC(final double currentEMC) {}

    @Override
    public double getMaxEMC() {
        return ItemEMCCellCreative.capacity;
    }
    
    @Override
    public void setMaxEMC(final double maxEMC) {}
    
    @Override
    public double getAvail() {
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
    public double addEMC(final double amount) {
        return amount;
    }

    @Override
    public double extractEMC(final double amount) {
        return amount;
    }

}
