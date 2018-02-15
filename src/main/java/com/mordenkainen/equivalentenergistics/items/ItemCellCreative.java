package com.mordenkainen.equivalentenergistics.items;

import com.mordenkainen.equivalentenergistics.core.Names;
import com.mordenkainen.equivalentenergistics.integration.ae2.cells.HandlerEMCCellCreative;

import appeng.api.storage.IMEInventory;
import appeng.api.storage.IMEInventoryHandler;
import appeng.api.storage.ISaveProvider;
import appeng.api.storage.StorageChannel;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;

public class ItemCellCreative extends ItemCellBase {

    public ItemCellCreative() {
        super(Names.CELL_CREATIVE, 1);
    }

    @Override
    public EnumRarity getRarity(final ItemStack stack) {
        return EnumRarity.EPIC;
    }

    @SuppressWarnings({ "rawtypes" })
    @Override
    public IMEInventoryHandler getCellInventory(final ItemStack stack, final ISaveProvider host, final StorageChannel channel) {
        if (channel == StorageChannel.ITEMS && isCell(stack)) {
            return new HandlerEMCCellCreative(host);
        }
        return null;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public int getStatusForCell(final ItemStack stack, final IMEInventory inv) {
        return 1;
    }
    
    @SuppressWarnings("rawtypes")
    @Override
    public double cellIdleDrain(final ItemStack stack, final IMEInventory inv) {
        return 0;
    }


}
