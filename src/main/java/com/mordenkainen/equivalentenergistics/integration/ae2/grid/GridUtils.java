package com.mordenkainen.equivalentenergistics.integration.ae2.grid;

import com.mordenkainen.equivalentenergistics.integration.ae2.EMCCraftingPattern;
import com.mordenkainen.equivalentenergistics.integration.ae2.cache.crafting.IEMCCraftingGrid;
import com.mordenkainen.equivalentenergistics.integration.ae2.cache.storage.IEMCStorageGrid;
import com.mordenkainen.equivalentenergistics.util.CommonUtils;

import appeng.api.AEApi;
import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridCache;
import appeng.api.networking.IGridNode;
import appeng.api.networking.crafting.ICraftingGrid;
import appeng.api.networking.crafting.ICraftingMedium;
import appeng.api.networking.crafting.ICraftingProviderHelper;
import appeng.api.networking.energy.IEnergyGrid;
import appeng.api.networking.pathing.IPathingGrid;
import appeng.api.networking.security.ISecurityGrid;
import appeng.api.networking.security.MachineSource;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.networking.ticking.ITickManager;
import appeng.api.storage.data.IAEItemStack;
import net.minecraft.item.ItemStack;

public final class GridUtils {

    private GridUtils() {}

    private static <T extends IGridCache> T getCache(final Class<T> cacheType, final AEProxy proxy) throws GridAccessException {
        final IGrid grid = proxy.getGrid();
        if (grid == null) {
            throw new GridAccessException();
        }

        final T cache = grid.getCache(cacheType);

        if (cache == null) {
            throw new GridAccessException();
        }

        return cache;
    }

    public static IPathingGrid getPath(final AEProxy proxy) throws GridAccessException {
        return getCache(IPathingGrid.class, proxy);
    }

    public static IStorageGrid getStorage(final AEProxy proxy) throws GridAccessException {
        return getCache(IStorageGrid.class, proxy);
    }

    public static ISecurityGrid getSecurity(final AEProxy proxy) throws GridAccessException {
        return getCache(ISecurityGrid.class, proxy);
    }

    public static ICraftingGrid getCrafting(final AEProxy proxy) throws GridAccessException {
        return getCache(ICraftingGrid.class, proxy);
    }

    public static IEnergyGrid getEnergy(final AEProxy proxy) throws GridAccessException {
        return getCache(IEnergyGrid.class, proxy);
    }

    public static ITickManager getTick(final AEProxy proxy) throws GridAccessException {
        return getCache(ITickManager.class, proxy);
    }

    public static IEMCStorageGrid getEMCStorage(final AEProxy proxy) throws GridAccessException {
        return getCache(IEMCStorageGrid.class, proxy);
    }

    public static IEMCCraftingGrid getEMCCrafting(final AEProxy proxy) throws GridAccessException {
        return getCache(IEMCCraftingGrid.class, proxy);
    }

    public static double getAEMaxEnergy(final AEProxy proxy) {
        try {
            return getEnergy(proxy).getMaxStoredPower();
        } catch (final GridAccessException e) {
            CommonUtils.debugLog("GridUtils:getAEMaxEnergy: Error accessing grid:", e);
        }
        return 0.0;
    }

    public static double getAECurrentEnergy(final AEProxy proxy) {
        try {
            return getEnergy(proxy).getStoredPower();
        } catch (final GridAccessException e) {
            CommonUtils.debugLog("GridUtils:getAECurrentEnergy: Error accessing grid:", e);
        }
        return 0.0;
    }

    public static double getAEDemand(final AEProxy proxy, final double amount) {
        try {
            return getEnergy(proxy).getEnergyDemand(amount);
        } catch (final GridAccessException e) {
            CommonUtils.debugLog("GridUtils:getAEDemand: Error accessing grid:", e);
        }
        return 0.0;
    }

    public static double sendAEToNet(final AEProxy proxy, final double amount, final Actionable mode) {
        try {
            final double overflow = getEnergy(proxy).injectPower(amount, mode);
            return mode == Actionable.SIMULATE ? overflow : 0.0;
        } catch (final GridAccessException e) {
            CommonUtils.debugLog("GridUtils:sendAEToNet: Error accessing grid:", e);
        }
        return 0.0;
    }

    public static double extractAEPower(final AEProxy proxy, final double amount, final Actionable mode, final PowerMultiplier multiplier) {
        try {
            return getEnergy(proxy).extractAEPower(amount, mode, multiplier);
        } catch (final GridAccessException e) {
            CommonUtils.debugLog("GridUtils:extractAEPower: Error accessing grid:", e);
        }
        return 0.0;
    }

    public static ItemStack injectItemsForPower(final AEProxy proxy, final ItemStack stack, final MachineSource source) {
        final IAEItemStack toInject = AEApi.instance().storage().createItemStack(stack.copy());
        try {
            final IStorageGrid storageGrid = getStorage(proxy);

            IAEItemStack rejected = storageGrid.getItemInventory().injectItems(toInject, Actionable.SIMULATE, source);

            long stored = toInject.getStackSize();
            if (rejected != null) {
                stored -= rejected.getStackSize();
            }

            final IEnergyGrid eGrid = getEnergy(proxy);
            final double availablePower = eGrid.extractAEPower(stored, Actionable.SIMULATE, PowerMultiplier.CONFIG);

            final long itemToAdd = Math.min((long) (availablePower + 0.9), stored);
            if (itemToAdd <= 0) {
                return stack;
            }
            eGrid.extractAEPower(stored, Actionable.MODULATE, PowerMultiplier.CONFIG);

            if (itemToAdd < toInject.getStackSize()) {
                final IAEItemStack split = toInject.copy();
                split.decStackSize(itemToAdd);
                toInject.setStackSize(itemToAdd);
                split.add(storageGrid.getItemInventory().injectItems(toInject, Actionable.MODULATE, source));

                return split.getItemStack();
            }

            rejected = storageGrid.getItemInventory().injectItems(toInject, Actionable.MODULATE, source);

            return rejected == null ? null : rejected.getItemStack();
        } catch (final GridAccessException e) {
            CommonUtils.debugLog("GridUtils:injectItemsForPower: Error accessing grid:", e);
        }

        return stack;
    }

    public static ItemStack injectItems(final AEProxy proxy, final ItemStack stack, final Actionable mode, final MachineSource source) {
        try {
            final IAEItemStack rejected = getStorage(proxy).getItemInventory().injectItems(AEApi.instance().storage().createItemStack(stack), mode, source);
            return rejected == null || rejected.getStackSize() == 0 ? null : rejected.getItemStack();
        } catch (GridAccessException e) {
            CommonUtils.debugLog("GridUtils:injectItems: Error accessing grid:", e);
            return stack;
        }
    }

    public static void alertDevice(final AEProxy proxy, final IGridNode device) {
        try {
            getTick(proxy).alertDevice(device);
        } catch (GridAccessException e) {
            CommonUtils.debugLog("GridUtils:alertDevice: Error accessing grid:", e);
        }
    }
    
    public static float injectEMC(final AEProxy proxy, final float emc, final Actionable mode) {
        try {
            if (emc > 0) {
                return getEMCStorage(proxy).injectEMC(emc, mode);
            }
        } catch (final GridAccessException e) {
            CommonUtils.debugLog("GridUtils:injectEMC: Error accessing grid:", e);
        }
        return 0;
    }
    
    public static void addPatterns(final AEProxy proxy, final ICraftingMedium medium, final ICraftingProviderHelper tracker) {
        try {
            for (final EMCCraftingPattern pattern : getEMCCrafting(proxy).getPatterns()) {
                tracker.addCraftingOption(medium, pattern);
            }
        } catch (GridAccessException e) {
            CommonUtils.debugLog("GridUtils:addPatterns: Error accessing grid:", e);
        }
    }
    
    public static void updatePatterns(final AEProxy proxy) {
        try {
            GridUtils.getEMCCrafting(proxy).updatePatterns();
        } catch (GridAccessException e) {
            CommonUtils.debugLog("GridUtils:updatePatterns: Error accessing grid:", e);
        }
    }
}
