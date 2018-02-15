package com.mordenkainen.equivalentenergistics.integration.hwyla;

import java.util.List;

import com.mordenkainen.equivalentenergistics.util.CommonUtils;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.translation.I18n;

@SuppressWarnings("deprecation")
public class WailaCrafterHUDHandler extends WailaHUDBase {

    private final static String EMC_TAG = "CurrentEMC";
    private final static String OWNER_TAG = "Owner";

    @Override
    public List<String> getWailaBody(final ItemStack itemStack, final List<String> currenttip, final IWailaDataAccessor accessor, final IWailaConfigHandler config) {
        if (accessor.getNBTData().hasKey(TAG_NAME)) {
            final NBTTagCompound tag = accessor.getNBTData().getCompoundTag(TAG_NAME);
            if (tag.hasKey(EMC_TAG)) {
                currenttip.add(I18n.translateToLocal("tooltip.emc.name") + " " + CommonUtils.formatEMC(tag.getDouble(EMC_TAG)));
            }
            if (tag.hasKey(OWNER_TAG)) {
                currenttip.add(I18n.translateToLocal("tooltip.owner.name") + " " + tag.getString(OWNER_TAG));
            }
        }
        return currenttip;
    }

}
