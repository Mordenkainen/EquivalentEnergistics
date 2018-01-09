package com.mordenkainen.equivalentenergistics.integration.ae2.cache.crafting;

import java.util.List;

import net.minecraft.item.ItemStack;

public interface ITransProvider {

    List<ItemStack> getTransmutations();

    String getPlayerUUID();
    
}
