package com.mordenkainen.equivalentenergistics.integration.theoneprobe;

import com.mordenkainen.equivalentenergistics.blocks.condenser.CondenserState;
import com.mordenkainen.equivalentenergistics.blocks.condenser.tiles.TileEMCCondenser;
import com.mordenkainen.equivalentenergistics.core.Reference;

import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;

@SuppressWarnings("deprecation")
public class ProbeCondenserHUDHandler implements IProbeInfoProvider {

    @Override
    public String getID() {
        return Reference.MOD_ID + ":eqecondenserhandler";
    }

    @Override
    public void addProbeInfo(final ProbeMode mode, final IProbeInfo probeInfo, final EntityPlayer player, final World world, final IBlockState blockState, final IProbeHitData data) {
        final TileEntity te = world.getTileEntity(data.getPos());
        if(te instanceof TileEMCCondenser) {
            final CondenserState state = ((TileEMCCondenser) te).getState();

            probeInfo.horizontal().text(I18n.translateToLocal("tooltip.status.name") + " " + (state == CondenserState.ACTIVE || state == CondenserState.IDLE ? TextFormatting.GREEN : TextFormatting.RED) + state.getStateName() + TextFormatting.RESET);
        }
    }

}
