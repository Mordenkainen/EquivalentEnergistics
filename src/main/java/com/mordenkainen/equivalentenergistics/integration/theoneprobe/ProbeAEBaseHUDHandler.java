package com.mordenkainen.equivalentenergistics.integration.theoneprobe;

import com.mordenkainen.equivalentenergistics.core.Reference;
import com.mordenkainen.equivalentenergistics.integration.ae2.tiles.TileAEBase;

import appeng.api.implementations.IPowerChannelState;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;

@SuppressWarnings("deprecation")
public class ProbeAEBaseHUDHandler implements IProbeInfoProvider {

    @Override
    public String getID() {
        return Reference.MOD_ID + ":eqeaehandler";
    }

    @Override
    public void addProbeInfo(final ProbeMode mode, final IProbeInfo probeInfo, final EntityPlayer player, final World world, final IBlockState blockState, final IProbeHitData data) {
        final TileEntity te = world.getTileEntity(data.getPos());
        if(te instanceof TileAEBase && te instanceof IPowerChannelState) {
            final IPowerChannelState state = (IPowerChannelState) te;
            final boolean isActive = state.isActive();
            final boolean isPowered = state.isPowered();
            
            if(isActive && isPowered) {
                probeInfo.horizontal().text(I18n.translateToLocal("tooltip.deviceonline"));
            } else if(!isPowered) { // NOPMD
                probeInfo.horizontal().text(I18n.translateToLocal("tooltip.deviceoffline"));
            } else {
                probeInfo.horizontal().text(I18n.translateToLocal("tooltip.devicemissingchannel"));
            }
        }
    }

}
