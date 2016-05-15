package com.mordenkainen.equivalentenergistics.integration.ee3;

import com.mordenkainen.equivalentenergistics.integration.IEMCHandler;
import com.mordenkainen.equivalentenergistics.integration.Integration;
import com.mordenkainen.equivalentenergistics.registries.ItemEnum;
import com.mordenkainen.equivalentenergistics.tiles.TileEMCCrafter;
import com.mordenkainen.equivalentenergistics.util.DimensionalLocation;
import com.mordenkainen.equivalentenergistics.util.EMCCraftingPattern;
import com.pahimar.ee3.api.event.EnergyValueEvent;
import com.pahimar.ee3.api.event.PlayerKnowledgeEvent;
import com.pahimar.ee3.api.exchange.EnergyValue;
import com.pahimar.ee3.api.exchange.EnergyValueRegistryProxy;
import com.pahimar.ee3.api.knowledge.TransmutationKnowledgeRegistryProxy;
import com.pahimar.ee3.util.ItemHelper;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class EquivExchange3 implements IEMCHandler {
	private static Item tomeItem;
	private float[] crystalValues = {0.0F, 0.0F, 0.0F};

	@Override
	public void relearnCrystals() {
		crystalValues = new float[] {0.0F, 0.0F, 0.0F};
		for (int i = 0; i <= 2; i++) {
			crystalValues[i] = getCrystalEMC(i);
		}
	}

	@Override
	public boolean hasEMC(final ItemStack itemStack) {
		return EnergyValueRegistryProxy.hasEnergyValue(itemStack);
	}

	@Override
	public float getEnergyValue(final ItemStack itemStack) {
		final EnergyValue val = EnergyValueRegistryProxy.getEnergyValue(itemStack);
		return val == null ? 0.0F : val.getValue();
	}

	@Override
	public float getCrystalEMC() {
		return getCrystalEMC(0);
	}

	@Override
	public float getCrystalEMC(final int tier) {
		if (this.crystalValues[tier] == 0.0F) {
			crystalValues[tier] = EnergyValueRegistryProxy.getEnergyValue(new ItemStack(ItemEnum.EMCCRYSTAL.getItem(), 1, tier)).getValue();
		}
		return crystalValues[tier];
	}

	@Override
	public List<ItemStack> getTransmutations(final TileEMCCrafter tile) {
		List<ItemStack> transmutations;

		transmutations = new ArrayList<ItemStack>(TransmutationKnowledgeRegistryProxy.getPlayerKnownTransmutations(ItemHelper.getOwnerUUID(tile.getCurrentTome())));
		
		final Iterator<ItemStack> iter = transmutations.iterator();
		while (iter.hasNext()) {
			final ItemStack currentItem = (ItemStack)iter.next();
			if (currentItem == null || currentItem.getItem() == ItemEnum.EMCCRYSTAL.getItem()) {
				iter.remove();
			}
		}
		return transmutations;
	}

	@Override
	public boolean isValidTome(final ItemStack itemStack)	{
		if (tomeItem == null) { 
			tomeItem = GameRegistry.findItem("EE3", "alchemicalTome");
		}
		return itemStack != null && itemStack.getItem() == tomeItem && ItemHelper.hasOwnerUUID(itemStack);
	}

	@Override
	public void setCrystalEMC(final float emc) {
		EnergyValueRegistryProxy.addPreAssignedEnergyValue(new ItemStack(ItemEnum.EMCCRYSTAL.getItem(), 1, 0), emc);
		EnergyValueRegistryProxy.addPreAssignedEnergyValue(new ItemStack(ItemEnum.EMCCRYSTAL.getItem(), 1, 1), emc * 576.0F);
		EnergyValueRegistryProxy.addPreAssignedEnergyValue(new ItemStack(ItemEnum.EMCCRYSTAL.getItem(), 1, 2), (float)(emc * Math.pow(576.0D, 2.0D)));
	}

	@Override
	public UUID getTomeUUID(final ItemStack currentTome) {
		return ItemHelper.getOwnerUUID(currentTome);
	}

	@Override
	public String getTomeOwner(final ItemStack currentTome) {
		return ItemHelper.getOwnerName(currentTome);
	}

	@Override
	public float getSingleEnergyValue(final ItemStack stack) {
		final ItemStack singleStack = stack.copy();
		singleStack.stackSize = 1;
		return getEnergyValue(singleStack);
	}
	
	@SubscribeEvent
	public void onPlayerKnowledgeChange(final PlayerKnowledgeEvent event)	{
		if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
			final Iterator<DimensionalLocation> iter = TileEMCCrafter.crafterTiles.iterator();
			while (iter.hasNext()) {
				final DimensionalLocation currentLoc = (DimensionalLocation)iter.next();
				final TileEntity crafter = currentLoc.getTE();
				if (crafter instanceof TileEMCCrafter) {
					((TileEMCCrafter)crafter).playerKnowledgeChange(event.playerUUID);
				} else {
					iter.remove();
				}
			}
		}
	}

	@SubscribeEvent
	public void onEnergyValueChange(final EnergyValueEvent event)	{
		if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
			Integration.emcHandler.relearnCrystals();
			EMCCraftingPattern.relearnPatterns();
			final Iterator<DimensionalLocation> iter = TileEMCCrafter.crafterTiles.iterator();
			while (iter.hasNext()) {
				final DimensionalLocation currentLoc = (DimensionalLocation)iter.next();
				final TileEntity crafter = currentLoc.getTE();
				if (crafter instanceof TileEMCCrafter) {
					((TileEMCCrafter)crafter).energyValueEvent();
				} else {
					iter.remove();
				}
			}
		}
	}
}