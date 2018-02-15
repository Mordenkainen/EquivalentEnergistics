package com.mordenkainen.equivalentenergistics.integration.theoneprobe;

import com.mordenkainen.equivalentenergistics.blocks.crafter.tiles.TileEMCCrafter;
import com.mordenkainen.equivalentenergistics.core.Reference;
import com.mordenkainen.equivalentenergistics.util.CommonUtils;

import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;

@SuppressWarnings("deprecation")
public class ProbeCrafterHUDHandler implements IProbeInfoProvider {

    private final static String EMC_TAG = "CurrentEMC";
    private final static String OWNER_TAG = "Owner";
    
    @Override
    public String getID() {
        return Reference.MOD_ID + ":eqecrafterhandler";
    }

    @Override
    public void addProbeInfo(final ProbeMode mode, final IProbeInfo probeInfo, final EntityPlayer player, final World world, final IBlockState blockState, final IProbeHitData data) {
        final TileEntity te = world.getTileEntity(data.getPos());
        if(te instanceof TileEMCCrafter) {
            final NBTTagCompound tag = ((TileEMCCrafter) te).getWailaTag(new NBTTagCompound());
            
            if (tag.hasKey(EMC_TAG)) {
                probeInfo.horizontal().text(I18n.translateToLocal("tooltip.emc.name") + " " + CommonUtils.formatEMC(tag.getDouble(EMC_TAG)));
            }
            if (tag.hasKey(OWNER_TAG)) {
                probeInfo.horizontal().text(I18n.translateToLocal("tooltip.owner.name") + " " + tag.getString(OWNER_TAG));
            }
        }
    }

}
