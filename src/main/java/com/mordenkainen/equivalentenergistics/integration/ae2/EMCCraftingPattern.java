package com.mordenkainen.equivalentenergistics.integration.ae2;

import java.util.ArrayList;

import com.mordenkainen.equivalentenergistics.core.config.ConfigManager;
import com.mordenkainen.equivalentenergistics.integration.Integration;
import com.mordenkainen.equivalentenergistics.items.ItemEMCCrystal;
import com.mordenkainen.equivalentenergistics.items.ItemEnum;
import com.mordenkainen.equivalentenergistics.items.ItemPattern;

import appeng.api.AEApi;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.storage.data.IAEItemStack;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public final class EMCCraftingPattern implements ICraftingPatternDetails {

    private IAEItemStack[] ingredients;
    private final IAEItemStack[] result = new IAEItemStack[1];
    public float outputEMC;
    public float inputEMC;
    public boolean valid = true;

    public EMCCraftingPattern(final ItemStack craftingResult) {
        buildPattern(craftingResult);
    }

    @Override
    public ItemStack getPattern() {
        return ItemPattern.getItemForPattern(result[0].getItemStack());
    }

    @Override
    public boolean isValidItemForSlot(final int slotIndex, final ItemStack itemStack, final World world) {
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
    public ItemStack getOutput(final InventoryCrafting craftingInv, final World world) {
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
        if (craftingResult.getItem() == ItemEnum.EMCCRYSTAL.getItem()) {
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
        result[0] = AEApi.instance().storage().createItemStack(ItemEnum.EMCCRYSTAL.getStack(64, tier));
        ingredients = new IAEItemStack[] { AEApi.instance().storage().createItemStack(ItemEnum.EMCCRYSTAL.getDamagedStack(tier + 1)) };
    }

    private void createItemPattern(final ItemStack craftingResult) {
        int stackSize = 1;
        final float singleItemValue = Integration.emcHandler.getSingleEnergyValue(craftingResult);
        if (singleItemValue <= ConfigManager.maxStackEMC) {
            stackSize = (int) Math.min(64, ConfigManager.maxStackEMC / singleItemValue);
        }
        result[0] = AEApi.instance().storage().createItemStack(craftingResult).setStackSize(stackSize);
        float remainingEMC = outputEMC = singleItemValue * stackSize;
        inputEMC = 0;
        valid = false;
        final ArrayList<IAEItemStack> crystals = new ArrayList<IAEItemStack>();
        for (int x = 4; x >= 0 && remainingEMC > 0; x--) {
            final float crystalEMC = ItemEMCCrystal.CRYSTAL_VALUES[x];
            int numCrystals = (int) (remainingEMC / crystalEMC);
            while (numCrystals > 0) {
                crystals.add(AEApi.instance().storage().createItemStack(ItemEnum.EMCCRYSTAL.getDamagedStack(x)).setStackSize(numCrystals));
                final float totalEMC = crystalEMC * numCrystals;
                remainingEMC -= totalEMC;
                inputEMC += totalEMC;
                numCrystals = (int) (remainingEMC / crystalEMC);
            }
        }

        if (remainingEMC > 0) {
            if (crystals.get(crystals.size() - 1).getItemDamage() == 0) {
                crystals.get(crystals.size() - 1).setStackSize(crystals.get(crystals.size() - 1).getStackSize() + 1);
            } else {
                crystals.add(AEApi.instance().storage().createItemStack(ItemEnum.EMCCRYSTAL.getDamagedStack(0)));
            }
            inputEMC++;
        }

        ingredients = crystals.toArray(new IAEItemStack[crystals.size()]);

        if (crystals.size() <= 9) {
            valid = true;
        }
    }

}
