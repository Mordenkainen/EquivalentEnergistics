package com.mordenkainen.equivalentenergistics.integration.ae2;

import java.util.ArrayList;

import com.mordenkainen.equivalentenergistics.core.config.EqEConfig;
import com.mordenkainen.equivalentenergistics.items.ItemEMCCrystal;
import com.mordenkainen.equivalentenergistics.items.ItemPattern;
import com.mordenkainen.equivalentenergistics.items.ModItems;

import appeng.api.AEApi;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.storage.data.IAEItemStack;
import moze_intel.projecte.api.ProjectEAPI;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemHandlerHelper;

public class EMCCraftingPattern implements ICraftingPatternDetails {

    private IAEItemStack[] ingredients;
    private final IAEItemStack[] result = new IAEItemStack[1];
    public double outputEMC;
    public double inputEMC;
    public boolean valid = true;

    public EMCCraftingPattern(final ItemStack craftingResult) {
        buildPattern(craftingResult);
    }
    
    @Override
    public ItemStack getPattern() {
        return ItemPattern.getItemForPattern(result[0].getItemStack());
    }

    @Override
    public boolean isValidItemForSlot(final int slot, final ItemStack stack, final World world) {
        return false;
    }
    
    @Override
    public boolean isCraftable() {
        return false;
    }

    @Override
    public IAEItemStack[] getInputs() {
        return ingredients.clone();
    }
    
    @Override
    public IAEItemStack[] getCondensedInputs() {
        return getInputs();
    }
    
    @Override
    public ItemStack getOutput(final InventoryCrafting crafting, final World world) {
        return null;
    }

    @Override
    public IAEItemStack[] getOutputs() {
        return result.clone();
    }
    
    @Override
    public IAEItemStack[] getCondensedOutputs() {
        return getOutputs();
    }

    @Override
    public boolean canSubstitute() {
        return false;
    }

    @Override
    public int getPriority() {
        return -1;
    }

    @Override
    public void setPriority(final int priority) {}

    private void buildPattern(final ItemStack craftingResult) {
        if (craftingResult.getItem() == ModItems.CRYSTAL) {
            createCrystalPattern(craftingResult.getItemDamage());
        } else {
            createItemPattern(craftingResult);
        }
    }

    public void rebuildPattern() {
        buildPattern(this.result[0].getItemStack());
    }

    private void createCrystalPattern(final int tier) {
        valid = true;
        outputEMC = inputEMC = ItemEMCCrystal.CRYSTAL_VALUES[tier + 1];
        result[0] = AEApi.instance().storage().createItemStack(new ItemStack(ModItems.CRYSTAL, 64, tier));
        ingredients = new IAEItemStack[] { AEApi.instance().storage().createItemStack(new ItemStack(ModItems.CRYSTAL, 1, tier + 1)) };
    }

    private void createItemPattern(final ItemStack craftingResult) {
        int stackSize = 1;
        final double singleItemValue = ProjectEAPI.getEMCProxy().getValue(ItemHandlerHelper.copyStackWithSize(craftingResult, 1));
        if (singleItemValue <= EqEConfig.emcAssembler.maxStackEMC) {
            stackSize = (int) Math.min(64, EqEConfig.emcAssembler.maxStackEMC / singleItemValue);
        }
        result[0] = AEApi.instance().storage().createItemStack(craftingResult).setStackSize(stackSize);
        double remainingEMC = outputEMC = singleItemValue * stackSize;
        inputEMC = 0;
        valid = false;
        final ArrayList<IAEItemStack> crystals = new ArrayList<IAEItemStack>();
        for (int x = 4; x >= 0 && remainingEMC > 0; x--) {
            final double crystalEMC = ItemEMCCrystal.CRYSTAL_VALUES[x];
            int numCrystals = (int) (remainingEMC / crystalEMC);
            while (numCrystals > 0) {
                crystals.add(AEApi.instance().storage().createItemStack(new ItemStack(ModItems.CRYSTAL, 1, x)).setStackSize(numCrystals));
                final double totalEMC = crystalEMC * numCrystals;
                remainingEMC -= totalEMC;
                inputEMC += totalEMC;
                numCrystals = (int) (remainingEMC / crystalEMC);
            }
        }

        if (remainingEMC > 0) {
            if (!crystals.isEmpty() && crystals.get(crystals.size() - 1).getItemDamage() == 0) {
                crystals.get(crystals.size() - 1).setStackSize(crystals.get(crystals.size() - 1).getStackSize() + 1);
            } else {
                crystals.add(AEApi.instance().storage().createItemStack(new ItemStack(ModItems.CRYSTAL, 1, 0)));
            }
            inputEMC++;
        }

        ingredients = crystals.toArray(new IAEItemStack[crystals.size()]);

        if (crystals.size() <= 9) {
            valid = true;
        }
    }

}
