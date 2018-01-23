package com.mordenkainen.equivalentenergistics.integration;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.mordenkainen.equivalentenergistics.integration.ae2.cache.crafting.EMCCraftingGrid;
import com.mordenkainen.equivalentenergistics.integration.ae2.cache.crafting.IEMCCraftingGrid;
import com.mordenkainen.equivalentenergistics.integration.ae2.cache.storage.EMCStorageGrid;
import com.mordenkainen.equivalentenergistics.integration.ae2.cache.storage.IEMCStorageGrid;
import com.mordenkainen.equivalentenergistics.items.ItemEMCCrystal;
import com.mordenkainen.equivalentenergistics.items.ModItems;
import com.mordenkainen.equivalentenergistics.util.CommonUtils;

import appeng.api.AEApi;
import moze_intel.projecte.api.ProjectEAPI;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.oredict.OreDictionary;

public final class Integration {

    private Integration() {}

    public static void init() {
        AEApi.instance().registries().gridCache().registerGridCache(IEMCStorageGrid.class, EMCStorageGrid.class);
        AEApi.instance().registries().gridCache().registerGridCache(IEMCCraftingGrid.class, EMCCraftingGrid.class);
        AEApi.instance().registries().cell().addCellHandler(ModItems.CELL);
        AEApi.instance().registries().cell().addCellHandler(ModItems.CELL_CREATIVE);
        FMLInterModComms.sendMessage("waila", "register", "com.mordenkainen.equivalentenergistics.integration.hwyla.Hwyla.register");
    }

    public static void postInit() {
        ProjectEAPI.getEMCProxy().registerCustomEMC(new ItemStack(ModItems.CRYSTAL, 1, 0), (int) ItemEMCCrystal.CRYSTAL_VALUES[0]);
        ProjectEAPI.getEMCProxy().registerCustomEMC(new ItemStack(ModItems.CRYSTAL, 1, 1), (int) ItemEMCCrystal.CRYSTAL_VALUES[1]);
        ProjectEAPI.getEMCProxy().registerCustomEMC(new ItemStack(ModItems.CRYSTAL, 1, 2), (int) ItemEMCCrystal.CRYSTAL_VALUES[2]);
        ProjectEAPI.getEMCProxy().registerCustomEMC(new ItemStack(ModItems.CRYSTAL, 1, 3), (int) ItemEMCCrystal.CRYSTAL_VALUES[3]);
        ProjectEAPI.getEMCProxy().registerCustomEMC(new ItemStack(ModItems.CRYSTAL, 1, 4), (int) ItemEMCCrystal.CRYSTAL_VALUES[4]);

        try {
            Class<?> cellInv;
            cellInv = Class.forName("appeng.me.storage.CellInventory");
            Method blackList;
            blackList = cellInv.getDeclaredMethod("addBasicBlackList", int.class, int.class);
            blackList.invoke(null, Item.getIdFromItem(ModItems.CRYSTAL), OreDictionary.WILDCARD_VALUE);
        } catch (NoSuchMethodException | SecurityException | ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            CommonUtils.debugLog("Failed to blacklist EMC Crystals from AE Cells", e);
        }
    }

}
