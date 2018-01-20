package com.mordenkainen.equivalentenergistics.items;

import com.mordenkainen.equivalentenergistics.items.base.ItemMultiBase;

import appeng.api.implementations.tiles.IChestOrDrive;
import appeng.api.storage.ICellHandler;
import appeng.api.storage.IMEInventoryHandler;
import appeng.api.storage.StorageChannel;
import cpw.mods.fml.common.Optional;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;

@Optional.Interface(iface = "appeng.api.storage.ICellHandler", modid = "appliedenergistics2") // NOPMD
public abstract class ItemEMCCellBase extends ItemMultiBase implements ICellHandler {

    public ItemEMCCellBase(final int itemCount) {
        super(itemCount);
        setMaxStackSize(1);
    }

    @Optional.Method(modid = "appliedenergistics2")
    @Override
    public boolean isCell(final ItemStack stack) {
        return stack != null && stack.getItem() == this;
    }

    @Optional.Method(modid = "appliedenergistics2")
    @Override
    public IIcon getTopTexture_Light() {
        return null;
    }

    @Optional.Method(modid = "appliedenergistics2")
    @Override
    public IIcon getTopTexture_Medium() {
        return null;
    }

    @Optional.Method(modid = "appliedenergistics2")
    @Override
    public IIcon getTopTexture_Dark() {
        return null;
    }

    @Optional.Method(modid = "appliedenergistics2")
    @SuppressWarnings("rawtypes")
    @Override
    public void openChestGui(final EntityPlayer player, final IChestOrDrive chest, final ICellHandler handler, final IMEInventoryHandler inv, final ItemStack stack, final StorageChannel channel) {
        player.addChatMessage(new ChatComponentText(StatCollector.translateToLocal("message.cell.chestwarning")));
    }

}
