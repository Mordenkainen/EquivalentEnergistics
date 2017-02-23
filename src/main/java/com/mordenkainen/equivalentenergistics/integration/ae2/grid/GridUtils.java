package com.mordenkainen.equivalentenergistics.integration.ae2.grid;

import com.mordenkainen.equivalentenergistics.integration.ae2.cache.EMCStorageGrid;
import com.mordenkainen.equivalentenergistics.integration.ae2.cache.IEMCStorageGrid;
import com.mordenkainen.equivalentenergistics.util.CommonUtils;

import appeng.api.AEApi;
import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.networking.IGrid;
import appeng.api.networking.crafting.ICraftingGrid;
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
	
	public static IPathingGrid getPath(final IGridProxy proxy) throws GridAccessException {
		final IGrid grid = proxy.getGrid();
        if (grid == null) {
            throw new GridAccessException();
        }

        final IPathingGrid pathingGrid = grid.getCache(IPathingGrid.class);

        if (pathingGrid == null) {
            throw new GridAccessException();
        }

        return pathingGrid;
	}

	public static IStorageGrid getStorage(final IGridProxy proxy) throws GridAccessException {
		final IGrid grid = proxy.getGrid();
        if (grid == null) {
            throw new GridAccessException();
        }

        final IStorageGrid storageGrid = grid.getCache(IStorageGrid.class);

        if (storageGrid == null) {
            throw new GridAccessException();
        }

        return storageGrid;
	}

	public static ISecurityGrid getSecurity(final IGridProxy proxy) throws GridAccessException {
		final IGrid grid = proxy.getGrid();
        if (grid == null) {
            throw new GridAccessException();
        }

        final ISecurityGrid securityGrid = grid.getCache(ISecurityGrid.class);

        if (securityGrid == null) {
            throw new GridAccessException();
        }

        return securityGrid;
	}

	public static ICraftingGrid getCrafting(final IGridProxy proxy) throws GridAccessException {
		final IGrid grid = proxy.getGrid();
        if (grid == null) {
            throw new GridAccessException();
        }

        final ICraftingGrid craftingGrid = grid.getCache(ICraftingGrid.class);

        if (craftingGrid == null) {
            throw new GridAccessException();
        }

        return craftingGrid;
	}

	public static IEnergyGrid getEnergy(final IGridProxy proxy) throws GridAccessException {
		final IGrid grid = proxy.getGrid();
        if (grid == null) {
            throw new GridAccessException();
        }

        final IEnergyGrid energyGrid = grid.getCache(IEnergyGrid.class);

        if (energyGrid == null) {
            throw new GridAccessException();
        }

        return energyGrid;
	}

	public static ITickManager getTick(final IGridProxy proxy) throws GridAccessException {
		final IGrid grid = proxy.getGrid();
        if (grid == null) {
            throw new GridAccessException();
        }

        final ITickManager tickGrid = grid.getCache(ITickManager.class);

        if (tickGrid == null) {
            throw new GridAccessException();
        }

        return tickGrid;
	}

	public static IEMCStorageGrid getEMCStorage(final IGridProxy proxy) throws GridAccessException {
		final IGrid grid = proxy.getGrid();
        if (grid == null) {
            throw new GridAccessException();
        }

        final IEMCStorageGrid emcGrid = grid.getCache(EMCStorageGrid.class);

        if (emcGrid == null) {
            throw new GridAccessException();
        }

        return emcGrid;
	}
	
	public static double getAEMaxEnergy(final IGridProxy proxy) {
        try {
            return getEnergy(proxy).getMaxStoredPower();
        } catch (final GridAccessException e) {
            CommonUtils.debugLog("GridUtils:getAEMaxEnergy: Error accessing grid:", e);
        }
        return 0.0;
    }

    public static double getAECurrentEnergy(final IGridProxy proxy) {
        try {
            return getEnergy(proxy).getStoredPower();
        } catch (final GridAccessException e) {
            CommonUtils.debugLog("GridUtils:getAECurrentEnergy: Error accessing grid:", e);
        }
        return 0.0;
    }
    
    public static double getAEDemand(final IGridProxy proxy, final double amount) {
        try {
            return getEnergy(proxy).getEnergyDemand(amount);
        } catch (final GridAccessException e) {
            CommonUtils.debugLog("GridUtils:getAEDemand: Error accessing grid:", e);
        }
        return 0.0;
    }

    public static double sendAEToNet(final IGridProxy proxy, final double amount, final Actionable mode) {
        try {
            final double overflow = getEnergy(proxy).injectPower(amount, mode);
            return mode == Actionable.SIMULATE ? overflow : 0.0;
        } catch (final GridAccessException e) {
            CommonUtils.debugLog("GridUtils:sendAEToNet: Error accessing grid:", e);
        }
        return 0.0;
    }
    
    public static double extractAEPower(final IGridProxy proxy, final double amount, final Actionable mode, final PowerMultiplier multiplier) {
        try {
            return getEnergy(proxy).extractAEPower(amount, mode, multiplier);
        } catch (final GridAccessException e) {
            CommonUtils.debugLog("GridUtils:extractAEPower: Error accessing grid:", e);
        }
        return 0.0;
    }
    
    public static ItemStack injectItemsForPower(final IGridProxy proxy, final ItemStack stack, final MachineSource source) {
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
}
