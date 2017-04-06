package com.mordenkainen.equivalentenergistics.integration.waila;

import java.util.List;

import appeng.api.implementations.IPowerChannelState;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;

public class WailaAEBaseHUDHandler extends WailaHUDBase {


    @Override
    public List<String> getWailaBody(final ItemStack itemStack, final List<String> currenttip, final IWailaDataAccessor accessor, final IWailaConfigHandler config) {
        final TileEntity te = accessor.getTileEntity();

        if (te instanceof IPowerChannelState) {
            final IPowerChannelState state = (IPowerChannelState) te;

            final boolean isActive = state.isActive();
            final boolean isPowered = state.isPowered();
            
            if(isActive && isPowered) {
                currenttip.add(StatCollector.translateToLocal("tooltip.deviceonline"));
            } else if(!isPowered) { // NOPMD
                currenttip.add(StatCollector.translateToLocal("tooltip.deviceoffline"));
            } else {
                currenttip.add(StatCollector.translateToLocal("tooltip.devicemissingchannel"));
            }
        }

        return currenttip;
    }

}
