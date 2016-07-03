package com.mordenkainen.equivalentenergistics.integration;

import java.util.List;
import java.util.UUID;

import com.mordenkainen.equivalentenergistics.tiles.TileEMCCrafter;

import net.minecraft.item.ItemStack;

public interface IEMCHandler {

	void relearnCrystals();

	boolean hasEMC(ItemStack itemStack);

	float getEnergyValue(ItemStack itemStack);

	float getCrystalEMC();

	float getCrystalEMC(int tier);

	List<ItemStack> getTransmutations(TileEMCCrafter tile);

	boolean isValidTome(ItemStack itemStack);

	void setCrystalEMC(float emc);

	UUID getTomeUUID(ItemStack currentTome);

	String getTomeOwner(ItemStack currentTome);

	float getSingleEnergyValue(ItemStack stack);
	
	boolean isEMCStorage(ItemStack stack);

	float getStoredEMC(ItemStack stack);

	float extractEMC(ItemStack stack, float toStore);
	
}
