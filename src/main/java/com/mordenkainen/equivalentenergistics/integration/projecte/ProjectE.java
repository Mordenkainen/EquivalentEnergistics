package com.mordenkainen.equivalentenergistics.integration.projecte;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import com.mordenkainen.equivalentenergistics.blocks.crafter.tiles.TileEMCCrafterBase;
import com.mordenkainen.equivalentenergistics.integration.IEMCHandler;
import com.mordenkainen.equivalentenergistics.integration.ae2.cache.crafting.EMCCraftingGrid;
import com.mordenkainen.equivalentenergistics.items.ItemEMCCrystal;
import com.mordenkainen.equivalentenergistics.items.ItemEMCCrystalOld;
import com.mordenkainen.equivalentenergistics.items.ItemEnum;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.event.EMCRemapEvent;
import moze_intel.projecte.api.event.PlayerKnowledgeChangeEvent;
import moze_intel.projecte.api.item.IItemEmc;
import net.minecraft.item.ItemStack;

public class ProjectE implements IEMCHandler {

    @Override
    public boolean hasEMC(final ItemStack itemStack) {
        return ProjectEAPI.getEMCProxy().getValue(itemStack) > 0;
    }

    @Override
    public double getEnergyValue(final ItemStack itemStack) {
        return ProjectEAPI.getEMCProxy().getValue(itemStack);
    }

    @Override
    public double getCrystalEMC(final int tier) {
        return ItemEMCCrystal.CRYSTAL_VALUES[tier];
    }

    @Override
    public List<ItemStack> getTransmutations(final TileEMCCrafterBase tile) {
        List<ItemStack> transmutations;

        transmutations = ProjectEAPI.getTransmutationProxy().getKnowledge(UUID.fromString(tile.getCurrentTome().getTagCompound().getString("OwnerUUID")));

        if (transmutations == null) {
            transmutations = new ArrayList<ItemStack>();
        }

        final Iterator<ItemStack> iter = transmutations.iterator();
        while (iter.hasNext()) {
            final ItemStack currentItem = iter.next();
            if (currentItem == null || ItemEnum.isCrystal(currentItem)) {
                iter.remove();
            }
        }
        return transmutations;
    }

    @Override
    public boolean isValidTome(final ItemStack itemStack) {
        return itemStack != null && ItemEnum.EMCBOOK.isSameItem(itemStack) && itemStack.hasTagCompound() && itemStack.getTagCompound().hasKey("OwnerUUID");
    }

    @Override
    public void setCrystalEMC() {
        ProjectEAPI.getEMCProxy().registerCustomEMC(new ItemStack(ItemEnum.EMCCRYSTAL.getItem(), 1, 0), (int) ItemEMCCrystal.CRYSTAL_VALUES[0]);
        ProjectEAPI.getEMCProxy().registerCustomEMC(new ItemStack(ItemEnum.EMCCRYSTAL.getItem(), 1, 1), (int) ItemEMCCrystal.CRYSTAL_VALUES[1]);
        ProjectEAPI.getEMCProxy().registerCustomEMC(new ItemStack(ItemEnum.EMCCRYSTAL.getItem(), 1, 2), (int) ItemEMCCrystal.CRYSTAL_VALUES[2]);
        ProjectEAPI.getEMCProxy().registerCustomEMC(new ItemStack(ItemEnum.EMCCRYSTAL.getItem(), 1, 3), (int) ItemEMCCrystal.CRYSTAL_VALUES[3]);
        ProjectEAPI.getEMCProxy().registerCustomEMC(new ItemStack(ItemEnum.EMCCRYSTAL.getItem(), 1, 4), (int) ItemEMCCrystal.CRYSTAL_VALUES[4]);

        ProjectEAPI.getEMCProxy().registerCustomEMC(new ItemStack(ItemEnum.EMCCRYSTALOLD.getItem(), 1, 0), (int) ItemEMCCrystalOld.CRYSTAL_VALUES[0]);
        ProjectEAPI.getEMCProxy().registerCustomEMC(new ItemStack(ItemEnum.EMCCRYSTALOLD.getItem(), 1, 1), (int) ItemEMCCrystalOld.CRYSTAL_VALUES[1]);
        ProjectEAPI.getEMCProxy().registerCustomEMC(new ItemStack(ItemEnum.EMCCRYSTALOLD.getItem(), 1, 2), (int) ItemEMCCrystalOld.CRYSTAL_VALUES[2]);
    }

    @Override
    public UUID getTomeUUID(final ItemStack currentTome) {
        return UUID.fromString(currentTome.getTagCompound().getString("OwnerUUID"));
    }

    @Override
    public String getTomeOwner(final ItemStack currentTome) {
        return currentTome.getTagCompound().getString("Owner");
    }

    @Override
    public double getSingleEnergyValue(final ItemStack stack) {
        final ItemStack singleStack = stack.copy();
        singleStack.stackSize = 1;
        return getEnergyValue(singleStack);
    }

    @SubscribeEvent
    public void onPlayerKnowledgeChange(final PlayerKnowledgeChangeEvent event) {
        EMCCraftingGrid.knowledgeEvent(event.playerUUID);
    }

    @SubscribeEvent
    public void onEnergyValueChange(final EMCRemapEvent event) {
        EMCCraftingGrid.energyEvent();
    }

    @Override
    public boolean isEMCStorage(final ItemStack stack) {
        return stack.getItem() instanceof IItemEmc;
    }

    @Override
    public double getStoredEMC(final ItemStack stack) {
        return stack.getItem() instanceof IItemEmc ? ((IItemEmc) stack.getItem()).getStoredEmc(stack) : 0;
    }

    @Override
    public double extractEMC(final ItemStack stack, final double toStore) {
        return stack.getItem() instanceof IItemEmc ? ((IItemEmc) stack.getItem()).extractEmc(stack, toStore) : 0;
    }

}
