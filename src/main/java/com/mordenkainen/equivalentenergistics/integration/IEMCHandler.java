package com.mordenkainen.equivalentenergistics.integration;

import java.util.List;
import java.util.UUID;

import com.mordenkainen.equivalentenergistics.blocks.crafter.tiles.TileEMCCrafter;

import net.minecraft.item.ItemStack;

public interface IEMCHandler {

    boolean hasEMC(ItemStack itemStack);

    float getEnergyValue(ItemStack itemStack);

    float getCrystalEMC(int tier);

    List<ItemStack> getTransmutations(TileEMCCrafter tile);

    boolean isValidTome(ItemStack itemStack);

    void setCrystalEMC();

    UUID getTomeUUID(ItemStack currentTome);

    String getTomeOwner(ItemStack currentTome);

    float getSingleEnergyValue(ItemStack stack);

    boolean isEMCStorage(ItemStack stack);

    float getStoredEMC(ItemStack stack);

    float extractEMC(ItemStack stack, float toStore);

}
