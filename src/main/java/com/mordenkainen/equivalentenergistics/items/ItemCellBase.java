package com.mordenkainen.equivalentenergistics.items;

import com.mordenkainen.equivalentenergistics.items.base.ItemMultiBase;

import appeng.api.storage.ICellHandler;
import net.minecraft.item.ItemStack;

public abstract class ItemCellBase extends ItemMultiBase implements ICellHandler {

    public ItemCellBase(final String name, final int count) {
        super(name, count);
        setMaxStackSize(1);
    }

    @Override
    public boolean isCell(final ItemStack stack) {
        return stack != null && stack.getItem() == this;
    }

}
