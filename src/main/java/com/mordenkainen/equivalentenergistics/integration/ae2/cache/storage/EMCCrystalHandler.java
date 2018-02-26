package com.mordenkainen.equivalentenergistics.integration.ae2.cache.storage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mordenkainen.equivalentenergistics.integration.ae2.storagechannel.IAEEMCStack;
import com.mordenkainen.equivalentenergistics.integration.ae2.storagechannel.IEMCStorageChannel;
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
    private double last_value;

    public EMCCrystalHandler(final EMCStorageGrid host) {
        this.hostGrid = host;
    }
    
    @Override
    public IAEItemStack injectItems(final IAEItemStack stack, final Actionable mode, final IActionSource src) {
        if(stack.getItem() == ModItems.CRYSTAL) {
            final IEMCStorageChannel emcChannel = AEApi.instance().storage().getStorageChannel(IEMCStorageChannel.class);
            final IStorageGrid storageGrid = (IStorageGrid) hostGrid.getGrid().getCache(IStorageGrid.class);
            IAEEMCStack rejected = storageGrid.getInventory(emcChannel).injectItems(emcChannel.createStack(stack.getStackSize() * ItemEMCCrystal.CRYSTAL_VALUES[stack.getItemDamage()]), Actionable.SIMULATE, src);
            double emcAdded = stack.getStackSize() * ItemEMCCrystal.CRYSTAL_VALUES[stack.getItemDamage()] - (rejected == null ? 0 : rejected.getEMCValue());
            if (emcAdded < ItemEMCCrystal.CRYSTAL_VALUES[stack.getItemDamage()]) {
                return stack;
            }
            final long toAdd = (long) (emcAdded / ItemEMCCrystal.CRYSTAL_VALUES[stack.getItemDamage()]);
            if (mode == Actionable.MODULATE) {
                storageGrid.getInventory(emcChannel).injectItems(emcChannel.createStack(toAdd * ItemEMCCrystal.CRYSTAL_VALUES[stack.getItemDamage()]), Actionable.SIMULATE, src);
            }
            
            return toAdd == stack.getStackSize() ? null : stack.copy().setStackSize(stack.getStackSize() - toAdd);
        }

        return stack;
    }
    
    @Override
    public IAEItemStack extractItems(final IAEItemStack stack, final Actionable mode, final IActionSource src) {
        if(stack.getItem() == ModItems.CRYSTAL) {
            final IEMCStorageChannel emcChannel = AEApi.instance().storage().getStorageChannel(IEMCStorageChannel.class);
            final IStorageGrid storageGrid = (IStorageGrid) hostGrid.getGrid().getCache(IStorageGrid.class);
            IAEEMCStack extracted = storageGrid.getInventory(emcChannel).extractItems(emcChannel.createStack(stack.getStackSize() * ItemEMCCrystal.CRYSTAL_VALUES[stack.getItemDamage()]), Actionable.SIMULATE, src);
            if (extracted == null || extracted.getEMCValue() < ItemEMCCrystal.CRYSTAL_VALUES[stack.getItemDamage()]) {
                return null;
            }
            final long toRemove = (long) (extracted.getEMCValue() / ItemEMCCrystal.CRYSTAL_VALUES[stack.getItemDamage()]);
            if (mode == Actionable.MODULATE) {
                storageGrid.getInventory(emcChannel).extractItems(emcChannel.createStack(toRemove * ItemEMCCrystal.CRYSTAL_VALUES[stack.getItemDamage()]), mode, src);
            }
            return stack.copy().setStackSize(toRemove);
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
    public AccessRestriction getAccess() {
        return AccessRestriction.READ_WRITE;
    }
    
    @Override
    public boolean isPrioritized(final IAEItemStack stack) {
        return ModItems.CRYSTAL == stack.getItem();
    }
    
    @Override
    public boolean canAccept(final IAEItemStack stack) {
        return ModItems.CRYSTAL == stack.getItem();
    }
    
    @Override
    public int getSlot() {
        return 0;
    }
    
    @Override
    public boolean validForPass(final int pass) {
        return pass == 1;
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

    public void updateDisplay() {
        final IStorageGrid storageGrid = (IStorageGrid) hostGrid.getGrid().getCache(IStorageGrid.class);
        double currentEMC = getCurrentEMC();
        if (currentEMC != last_value) {
            last_value = currentEMC;
            
            for (final IAEItemStack stack : cachedList) {
                stack.setStackSize(-stack.getStackSize());
            }
            storageGrid.postAlterationOfStoredItems(storageChannel, cachedList, new BaseActionSource());

            cachedList = storageChannel.createList();
            if (currentEMC > 0) {
                for (int i = 4; i >= 0; i--) {
                    final double crystalEMC = ItemEMCCrystal.CRYSTAL_VALUES[i];
                    final long crystalcount = (long) (currentEMC / crystalEMC);
                    if (crystalcount > 0) {
                        cachedList.add(storageChannel.createStack(new ItemStack(ModItems.CRYSTAL, 1, i)).setStackSize(crystalcount));
                        currentEMC -= crystalcount * crystalEMC;
                    }
                }
            }
            
            cachedList.add(storageChannel.createStack(new ItemStack(ModItems.MISC, 1, 1)).setStackSize((long) last_value));
            storageGrid.postAlterationOfStoredItems(storageChannel, cachedList, new BaseActionSource());
        }
    }
    
    private double getCurrentEMC() {
        final IEMCStorageChannel emcChannel = AEApi.instance().storage().getStorageChannel(IEMCStorageChannel.class);
        final IStorageGrid storageGrid = (IStorageGrid) hostGrid.getGrid().getCache(IStorageGrid.class);
        final IAEEMCStack emcStack = storageGrid.getInventory(emcChannel).getStorageList().findPrecise(emcChannel.createStack(1));
        return emcStack == null ? 0 : emcStack.getEMCValue();
    }

}
