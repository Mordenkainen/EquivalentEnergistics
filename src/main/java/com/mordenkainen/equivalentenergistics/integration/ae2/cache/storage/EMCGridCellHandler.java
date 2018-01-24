package com.mordenkainen.equivalentenergistics.integration.ae2.cache.storage;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.mordenkainen.equivalentenergistics.integration.ae2.cells.HandlerEMCCellBase;
import com.mordenkainen.equivalentenergistics.util.CommonUtils;

import appeng.api.config.Actionable;
import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
import appeng.api.networking.events.MENetworkCellArrayUpdate;
import appeng.api.storage.ICellProvider;
import appeng.api.storage.IMEInventoryHandler;
import appeng.api.storage.StorageChannel;
import appeng.api.storage.data.IAEItemStack;

public class EMCGridCellHandler {

    private static Field intHandler;
    private static Field extHandler;

    private final EMCStorageGrid hostGrid;

    private final List<ICellProvider> driveBays = new ArrayList<ICellProvider>();

    public EMCGridCellHandler(final EMCStorageGrid hostGrid) {
        this.hostGrid = hostGrid;
    }

    public void addNode(final IGridNode gridNode, final IGridHost machine) {
        if (machine instanceof ICellProvider) {
            driveBays.add((ICellProvider) machine);
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" }) // NOPMD
    public void cellUpdate(final MENetworkCellArrayUpdate cellUpdate) {
        double newEMC = 0;
        double newMax = 0;
        for (final ICellProvider provider : driveBays) {
            final List<IMEInventoryHandler> cells = provider.getCellArray(StorageChannel.ITEMS);
            for (final IMEInventoryHandler cell : cells) {
                final HandlerEMCCellBase handler = getHandler(cell);
                if (handler != null) {
                    newEMC += handler.getCurrentEMC();
                    newMax += handler.getMaxEMC();
                }
            }
        }

        updatePool(newMax, newEMC);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void removeNode(final IGridNode gridNode, final IGridHost machine) {
        if (machine instanceof ICellProvider && driveBays.remove((ICellProvider) machine)) {
            double newEMC = hostGrid.getCurrentEMC();
            double newMax = hostGrid.getMaxEMC();
            final List<IMEInventoryHandler> cells = ((ICellProvider) machine).getCellArray(StorageChannel.ITEMS);
            for (final IMEInventoryHandler cell : cells) {
                final HandlerEMCCellBase handler = getHandler(cell);
                if (handler != null) {
                    newEMC -= handler.getCurrentEMC();
                    newMax -= handler.getMaxEMC();
                }
            }
            updatePool(newMax, newEMC);
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public double injectEMC(final double emc, final Actionable mode) {
        final double toAdd = Math.min(emc, hostGrid.getAvail());
        if (mode != Actionable.MODULATE) {
            return toAdd;
        }

        double added = 0;
        for (final ICellProvider provider : driveBays) {
            final List<IMEInventoryHandler> cells = provider.getCellArray(StorageChannel.ITEMS);
            for (final IMEInventoryHandler cell : cells) {
                final HandlerEMCCellBase handler = getHandler(cell);
                if (handler != null) {
                    added += handler.addEMC(toAdd - added);
                    hostGrid.markDirty();
                    if (added == toAdd) {
                        break;
                    }
                }
            }
        }

        hostGrid.addEMC(added);
        return added;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public double extractEMC(final double emc, final Actionable mode) {
        final double toExtract = Math.min(emc, hostGrid.getCurrentEMC());
        if (mode != Actionable.MODULATE) {
            return toExtract;
        }

        double extracted = 0;
        for (final ICellProvider provider : driveBays) {
            final List<IMEInventoryHandler> cells = provider.getCellArray(StorageChannel.ITEMS);
            for (final IMEInventoryHandler cell : cells) {
                final HandlerEMCCellBase handler = getHandler(cell);
                if (handler != null) {
                    extracted += handler.extractEMC(toExtract - extracted);
                    hostGrid.markDirty();
                    if (extracted == toExtract) {
                        break;
                    }
                }
            }
        }

        hostGrid.extractEMC(extracted);
        return extracted;
    }

    @SuppressWarnings("unchecked")
    private HandlerEMCCellBase getHandler(final IMEInventoryHandler<IAEItemStack> cell) {
        if (cell instanceof HandlerEMCCellBase) {
            return (HandlerEMCCellBase) cell;
        }

        if (cell == null || intHandler == null && !reflectFields()) {
            return null;
        }

        IMEInventoryHandler<IAEItemStack> realHandler = null;
        try {
            final String className = cell.getClass().getSimpleName();
            if ("DriveWatcher".equals(className)) {
                realHandler = (IMEInventoryHandler<IAEItemStack>) intHandler.get(cell);
            } else if ("ChestMonitorHandler".equals(className)) {
                final IMEInventoryHandler<IAEItemStack> monHandler = (IMEInventoryHandler<IAEItemStack>) extHandler.get(cell);
                realHandler = (IMEInventoryHandler<IAEItemStack>) intHandler.get(monHandler);
            }
        } catch (IllegalArgumentException | IllegalAccessException e) {
            CommonUtils.debugLog("Failed to reflect into AE", e);
        }

        if (realHandler instanceof HandlerEMCCellBase) {
            return (HandlerEMCCellBase) realHandler;
        }

        return null;
    }
    
    private void updatePool(final double newMax, final double newCurrent) {
        if (newMax != hostGrid.getMaxEMC() || newCurrent != hostGrid.getCurrentEMC()) {
            hostGrid.setMaxEMC(newMax);
            hostGrid.setCurrentEMC(newCurrent);
            hostGrid.markDirty();
        }
    }

    private static boolean reflectFields() {
        try {
            Class<?> clazz;
            clazz = Class.forName("appeng.me.storage.MEInventoryHandler");
            intHandler = clazz.getDeclaredField("internal");
            intHandler.setAccessible(true);
            clazz = Class.forName("appeng.api.storage.MEMonitorHandler");
            extHandler = clazz.getDeclaredField("internalHandler");
            extHandler.setAccessible(true);
        } catch (ClassNotFoundException | NoSuchFieldException | SecurityException e) {
            CommonUtils.debugLog("Failed to reflect into AE", e);
            return false;
        }

        return true;
    }

}
