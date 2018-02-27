package com.mordenkainen.equivalentenergistics.integration.ae2.storagechannel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import appeng.api.config.FuzzyMode;
import appeng.api.storage.data.IItemList;

public class EMCItemList implements IItemList<IAEEMCStack> {

    final private List<IAEEMCStack> items = new ArrayList<IAEEMCStack>();

    @Override
    public void add(final IAEEMCStack toAdd) {
        addStorage(toAdd);
    }

    @Override
    public Collection<IAEEMCStack> findFuzzy(final IAEEMCStack find, final FuzzyMode mode) {
        if(find == null) {
            return Collections.emptyList();
        }
        
        if (isEmpty()) {
            return Collections.emptyList();
        } else {
            return Arrays.asList(getFirstItem());
        }
    }

    @Override
    public IAEEMCStack findPrecise(final IAEEMCStack find) {
        if (find == null) {
            return null;
        }
        
        if (isEmpty()) {
            return null;
        } else {
            return getFirstItem();
        }
    }

    @Override
    public boolean isEmpty() {
        return items.isEmpty();
    }

    @Override
    public void addCrafting(final IAEEMCStack arg0) {}

    @Override
    public void addRequestable(final IAEEMCStack arg0) {}

    @Override
    public void addStorage(final IAEEMCStack toAdd) {
        if (toAdd == null) {
            return;
        }
        
        if (isEmpty()) {
            items.add(toAdd);
        } else {
            getFirstItem().incStackSize(toAdd.getStackSize());
        }
    }

    @Override
    public IAEEMCStack getFirstItem() {
        return items.isEmpty() ? null : items.get(0);
    }

    @Override
    public Iterator<IAEEMCStack> iterator() {
        return items.iterator();
    }

    @Override
    public void resetStatus() {
        for (final IAEEMCStack stack : this) {
            stack.reset();
        }
    }

    @Override
    public int size() {
        return items.size();
    }

}
