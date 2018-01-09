package com.mordenkainen.equivalentenergistics.integration.ae2.cache.crafting;

import com.mordenkainen.equivalentenergistics.integration.ae2.EMCCraftingPattern;
import com.mordenkainen.equivalentenergistics.integration.ae2.cache.ICacheBase;


public interface IEMCCraftingGrid extends ICacheBase {

    EMCCraftingPattern[] getPatterns();

    void updatePatterns();

}
