package com.mordenkainen.equivalentenergistics.integration.waila;

import java.util.List;

import com.mordenkainen.equivalentenergistics.util.CommonUtils;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class WailaCrafterHUDHandler extends WailaHUDBase {

    private final static String TAG_NAME = "TileData";

    @Override
    public List<String> getWailaBody(final ItemStack itemStack, final List<String> currenttip, final IWailaDataAccessor accessor, final IWailaConfigHandler config) {
        final NBTTagCompound tag = accessor.getNBTData();
        if (tag.hasKey(TAG_NAME)) {
            currenttip.add("Stored EMC: " + CommonUtils.formatEMC(tag.getCompoundTag(TAG_NAME).getFloat("currentEMC")));
            if (tag.getCompoundTag(TAG_NAME).hasKey("owner")) {
                currenttip.add("Owner: " + tag.getCompoundTag(TAG_NAME).getString("owner"));
            }
        }
        return currenttip;
    }

}
