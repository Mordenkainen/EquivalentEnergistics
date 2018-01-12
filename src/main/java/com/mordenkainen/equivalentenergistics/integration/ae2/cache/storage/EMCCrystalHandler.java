package com.mordenkainen.equivalentenergistics.integration.ae2.cache.storage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mordenkainen.equivalentenergistics.items.ItemEMCCrystal;
import com.mordenkainen.equivalentenergistics.items.ModItems;

import appeng.api.AEApi;
import appeng.api.config.AccessRestriction;
import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.storage.ICellProvider;
import appeng.api.storage.IMEInventoryHandler;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IItemList;
import appeng.me.helpers.BaseActionSource;
import net.minecraft.item.ItemStack;

public class EMCCrystalHandler implements ICellProvider, IMEInventoryHandler<IAEItemStack> {

    private final EMCStorageGrid hostGrid;
    private final IItemStorageChannel storageChannel = AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class);
    private IItemList<IAEItemStack> cachedList = storageChannel.createList();
    private boolean dirty = true;

    public EMCCrystalHandler(final EMCStorageGrid host) {
        this.hostGrid = host;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public List<IMEInventoryHandler> getCellArray(final IStorageChannel<?> channel) {
        if (channel.equals(storageChannel)) {
            return new ArrayList<IMEInventoryHandler>(Arrays.asList(new IMEInventoryHandler[] {this}));
        }

        return new ArrayList<IMEInventoryHandler>();
    }

    @Override
    public int getPriority() {
        return Integer.MAX_VALUE - 1;
    }

    @Override
    public IAEItemStack extractItems(final IAEItemStack stack, final Actionable mode, final IActionSource src) {
        if(stack.getItem() == ModItems.CRYSTAL) {
            final int toRemove = (int) Math.min(stack.getStackSize(), hostGrid.getCurrentEMC() / ItemEMCCrystal.CRYSTAL_VALUES[stack.getItemDamage()]);
            if (toRemove > 0) {
                hostGrid.extractEMC(toRemove * ItemEMCCrystal.CRYSTAL_VALUES[stack.getItemDamage()], mode);
                return stack.copy().setStackSize(toRemove);
            }
        }

        return null;
    }

    @Override
    public IItemList<IAEItemStack> getAvailableItems(final IItemList<IAEItemStack> items) {
        for (final IAEItemStack stack : cachedList) {
            items.add(stack);
        }

        return items;
    }

    @Override
    public IStorageChannel<IAEItemStack> getChannel() {
        return AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class);
    }

    @Override
    public IAEItemStack injectItems(final IAEItemStack stack, final Actionable mode, final IActionSource src) {
        if(stack.getItem() == ModItems.CRYSTAL) {
            final int toAdd = (int) Math.min(stack.getStackSize(), (hostGrid.getAvail()) / ItemEMCCrystal.CRYSTAL_VALUES[stack.getItemDamage()]);
            if (toAdd > 0) {
                hostGrid.addEMC(toAdd * ItemEMCCrystal.CRYSTAL_VALUES[stack.getItemDamage()], mode);
                return toAdd == stack.getStackSize() ? null : stack.copy().setStackSize(stack.getStackSize() - toAdd);
            }
        }

        return stack;
    }

    @Override
    public boolean canAccept(final IAEItemStack stack) {
        return ModItems.CRYSTAL == stack.getItem();
    }

    @Override
    public AccessRestriction getAccess() {
        return AccessRestriction.READ_WRITE;
    }

    @Override
    public int getSlot() {
        return 0;
    }

    @Override
    public boolean isPrioritized(final IAEItemStack stack) {
        return ModItems.CRYSTAL == stack.getItem();
    }

    @Override
    public boolean validForPass(final int pass) {
        return pass == 1;
    }

    public void markDirty() {
        dirty = true;
    }

    public void updateDisplay() {
        if (!dirty) {
            return;
        }

        dirty = false;
        final IStorageGrid storageGrid = (IStorageGrid) hostGrid.getGrid().getCache(IStorageGrid.class);

        for (final IAEItemStack stack : cachedList) {
            stack.setStackSize(-stack.getStackSize());
        }
        storageGrid.postAlterationOfStoredItems(storageChannel, cachedList, new BaseActionSource());

        cachedList = storageChannel.createList();
        if (hostGrid.getCurrentEMC() > 0) {
            float remainingEMC = hostGrid.getCurrentEMC();
            for (int i = 4; i >= 0; i--) {
                final float crystalEMC = ItemEMCCrystal.CRYSTAL_VALUES[i];
                final long crystalcount = (long) (remainingEMC / crystalEMC);
                if (crystalcount > 0) {
                    cachedList.add(storageChannel.createStack(new ItemStack(ModItems.CRYSTAL, 1, i)).setStackSize(crystalcount));
                    remainingEMC -= crystalcount * crystalEMC;
                }
            }
        }

        cachedList.add(storageChannel.createStack(new ItemStack(ModItems.MISC, 1, 1)).setStackSize((long) hostGrid.getCurrentEMC()));
        storageGrid.postAlterationOfStoredItems(storageChannel, cachedList, new BaseActionSource());
    }

}
