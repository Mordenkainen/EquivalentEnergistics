package com.mordenkainen.equivalentenergistics.util;

public interface IEMCStorage {

    double getCurrentEMC();

    void setCurrentEMC(double currentEMC);

    double getMaxEMC();

    void setMaxEMC(double maxEMC);

    double getAvail();

    boolean isFull();

    boolean isEmpty();

    double addEMC(double emc);

    double extractEMC(double emc);

}