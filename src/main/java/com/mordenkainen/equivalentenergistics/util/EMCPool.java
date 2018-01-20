package com.mordenkainen.equivalentenergistics.util;

public class EMCPool implements IEMCStorage {

    private float currentEMC;
    private float maxEMC;
    private boolean overflow;

    public EMCPool(final float currentEMC, final float maxEMC, final boolean overflow) {
        super();
        this.currentEMC = currentEMC;
        this.maxEMC = maxEMC;
        this.overflow = overflow;
    }

    public EMCPool() {
        this(0, 0, false);
    }

    public EMCPool(final float currentEMC) {
        this(currentEMC, currentEMC, true);
    }

    public EMCPool(final float currentEMC, final float maxEMC) {
        this(currentEMC, maxEMC, true);
    }

    @Override
    public float getCurrentEMC() {
        return currentEMC;
    }

    @Override
    public void setCurrentEMC(final float currentEMC) {
        if (!overflow && currentEMC > maxEMC) {
            this.currentEMC = maxEMC;
        } else if (currentEMC < 0) {
            this.currentEMC = 0;
        } else {
            this.currentEMC = currentEMC;
        }
    }

    @Override
    public float getMaxEMC() {
        return maxEMC;
    }

    @Override
    public void setMaxEMC(final float maxEMC) {
        if (!overflow && currentEMC > maxEMC) {
            this.currentEMC = maxEMC;
        }
        this.maxEMC = maxEMC;
    }

    public boolean canOverflow() {
        return overflow;
    }

    public void canOverflow(final boolean overflow) {
        if (!overflow && currentEMC > maxEMC) {
            currentEMC = maxEMC;
        }
        this.overflow = overflow;
    }

    public float getExcess() {
        if (overflow && currentEMC > maxEMC) {
            return currentEMC - maxEMC;
        }
        return 0;
    }

    @Override
    public float getAvail() {
        if (currentEMC >= maxEMC) {
            return 0;
        }
        return maxEMC - currentEMC;
    }

    @Override
    public boolean isFull() {
        return currentEMC >= maxEMC;
    }

    @Override
    public boolean isEmpty() {
        return currentEMC == 0;
    }

    @Override
    public float addEMC(final float emc) {
        float toAdd = emc;
        if (!overflow) {
            toAdd = Math.min(toAdd, getAvail());
        }
        currentEMC += toAdd;
        return toAdd;
    }

    @Override
    public float extractEMC(final float emc) {
        final float toExtract = Math.min(emc, currentEMC);
        currentEMC -= toExtract;
        return toExtract;
    }

}
