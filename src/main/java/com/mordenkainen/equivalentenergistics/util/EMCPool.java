package com.mordenkainen.equivalentenergistics.util;

public class EMCPool implements IEMCStorage {

    private double currentEMC;
    private double maxEMC;
    private boolean overflow;

    public EMCPool(final double currentEMC, final double maxEMC, final boolean overflow) {
        this.currentEMC = currentEMC;
        this.maxEMC = maxEMC;
        this.overflow = overflow;
    }

    public EMCPool() {
        this(0, 0, false);
    }

    public EMCPool(final double currentEMC) {
        this(currentEMC, currentEMC, true);
    }

    public EMCPool(final double currentEMC, final double maxEMC) {
        this(currentEMC, maxEMC, true);
    }

    @Override
    public double getCurrentEMC() {
        return currentEMC;
    }

    @Override
    public void setCurrentEMC(final double currentEMC) {
        if (!overflow && currentEMC > maxEMC) {
            this.currentEMC = maxEMC;
        } else if (currentEMC < 0) {
            this.currentEMC = 0;
        } else {
            this.currentEMC = currentEMC;
        }
    }

    @Override
    public double getMaxEMC() {
        return maxEMC;
    }

    @Override
    public void setMaxEMC(final double maxEMC) {
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

    public double getExcess() {
        if (overflow && currentEMC > maxEMC) {
            return currentEMC - maxEMC;
        }
        return 0;
    }

    @Override
    public double getAvail() {
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
    public double addEMC(final double emc) {
        double toAdd = emc;
        if (!overflow) {
            toAdd = Math.min(toAdd, getAvail());
        }
        currentEMC += toAdd;
        return toAdd;
    }

    @Override
    public double extractEMC(final double emc) {
        final double toExtract = Math.min(emc, currentEMC);
        currentEMC -= toExtract;
        return toExtract;
    }

}
