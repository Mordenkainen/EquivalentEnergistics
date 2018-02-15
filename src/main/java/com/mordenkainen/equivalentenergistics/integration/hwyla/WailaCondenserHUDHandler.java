package com.mordenkainen.equivalentenergistics.integration.hwyla;

import java.util.List;

import com.mordenkainen.equivalentenergistics.blocks.condenser.CondenserState;
import com.mordenkainen.equivalentenergistics.blocks.condenser.tiles.TileEMCCondenser;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.util.text.TextFormatting;

@SuppressWarnings("deprecation")
public class WailaCondenserHUDHandler extends WailaHUDBase {

    @Override
    public List<String> getWailaBody(final ItemStack itemStack, final List<String> currenttip, final IWailaDataAccessor accessor, final IWailaConfigHandler config) {

        final CondenserState state = ((TileEMCCondenser) accessor.getTileEntity()).getState();
        currenttip.add(I18n.translateToLocal("tooltip.status.name") + " " + (state == CondenserState.ACTIVE || state == CondenserState.IDLE ? TextFormatting.GREEN : TextFormatting.RED) + state.getStateName() + TextFormatting.RESET);

        return currenttip;
    }

}
