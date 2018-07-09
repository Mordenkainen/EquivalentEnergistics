package com.mordenkainen.equivalentenergistics.integration.ae2.cache.crafting;

import net.minecraft.item.ItemStack;

public interface IEMCCrafter {
    
    boolean isBusy();
    
    boolean addJob(ItemStack stack, double inputCost, double outputCost);
    
}
