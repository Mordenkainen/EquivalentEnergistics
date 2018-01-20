package com.mordenkainen.equivalentenergistics.util;

public interface IEMCStorage {

    float getCurrentEMC();

    void setCurrentEMC(float currentEMC);

    float getMaxEMC();

    void setMaxEMC(float maxEMC);

    float getAvail();

    boolean isFull();

    boolean isEmpty();

    float addEMC(float emc);

    float extractEMC(float emc);

}