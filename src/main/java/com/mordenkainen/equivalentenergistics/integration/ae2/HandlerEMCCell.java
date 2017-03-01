package com.mordenkainen.equivalentenergistics.integration.ae2;

import appeng.api.storage.ISaveProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class HandlerEMCCell extends HandlerEMCCellBase {

    private static final String EMC_TAG = "emc";

    private final NBTTagCompound cellData;
    private float currentEMC;
    private final float capacity;

    public HandlerEMCCell(final ItemStack storageStack, final ISaveProvider saveProvider, final float capacity) {
        super(saveProvider);
        if (!storageStack.hasTagCompound()) {
            storageStack.setTagCompound(new NBTTagCompound());
        }

        cellData = storageStack.getTagCompound();
        if (cellData.hasKey("emc")) {
            currentEMC = cellData.getLong(EMC_TAG);
        }

        this.capacity = capacity;
    }

    @Override
    public float addEMC(final float amount) {
        if (amount <= 0) {
            return 0;
        }

        final float toAdd = Math.min(amount, getAvail());

        if (toAdd > 0) {
            currentEMC += toAdd;
            updateEMC();
        }

        return toAdd;
    }

    @Override
    public float extractEMC(final float amount) {
        if (amount <= 0) {
            return 0;
        }

        final float toExtract = Math.min(amount, currentEMC);

        if (toExtract > 0) {
            currentEMC -= toExtract;
            updateEMC();
        }

        return toExtract;
    }

    @Override
    public int getCellStatus() {
        if (currentEMC >= capacity) {
            return 3;
        }
        if (currentEMC >= capacity * 0.75) {
            return 2;
        }
        return 1;
    }

    @Override
    public float getCapacity() {
        return capacity;
    }

    @Override
    public float getEMC() {
        return currentEMC;
    }

    @Override
    public float getAvail() {
        return Math.max(capacity - currentEMC, 0);
    }

    private void updateEMC() {
        cellData.setFloat(EMC_TAG, currentEMC);
        if (saveProvider != null) {
            saveProvider.saveChanges(this);
        }
    }

}
