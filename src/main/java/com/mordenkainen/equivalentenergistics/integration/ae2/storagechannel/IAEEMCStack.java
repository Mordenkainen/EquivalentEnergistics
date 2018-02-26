package com.mordenkainen.equivalentenergistics.integration.ae2.storagechannel;

import appeng.api.storage.data.IAEStack;

public interface IAEEMCStack extends IAEStack<IAEEMCStack> {

    EMCStackType getType();
    
    double getEMCValue();
    
}
