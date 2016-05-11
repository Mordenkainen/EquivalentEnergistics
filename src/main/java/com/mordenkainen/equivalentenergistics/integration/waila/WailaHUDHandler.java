package com.mordenkainen.equivalentenergistics.integration.waila;

import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;

public class WailaHUDHandler implements IWailaDataProvider {
	private final static String TAG_NAME = "TileData";
	@Override
	public List<String> getWailaBody(final ItemStack itemStack, final List<String> currenttip, final IWailaDataAccessor accessor, final IWailaConfigHandler config) {
		final NBTTagCompound tag = accessor.getNBTData();
		if(tag.hasKey(TAG_NAME)) {
			currenttip.add("Stored EMC: " + tag.getCompoundTag(TAG_NAME).getFloat("currentEMC"));
			if(tag.getCompoundTag(TAG_NAME).hasKey("owner")) {
				currenttip.add("Owner: " + tag.getCompoundTag(TAG_NAME).getString("owner"));
			}
		}
		return currenttip;
	}
	
	@Override
	public ItemStack getWailaStack(final IWailaDataAccessor accessor, final IWailaConfigHandler config) {
		return null;
	}

	@Override
	public List<String> getWailaHead(final ItemStack itemStack, final List<String> currenttip, final IWailaDataAccessor accessor,	final IWailaConfigHandler config) {
		return null;
	}

	@Override
	public List<String> getWailaTail(final ItemStack itemStack, final List<String> currenttip, final IWailaDataAccessor accessor,	final IWailaConfigHandler config) {
		return null;
	}

	@Override
	public NBTTagCompound getNBTData(final EntityPlayerMP player, final TileEntity tile, final NBTTagCompound tag, final World world, final int x, final int y, final int z) {
		if(tile instanceof IWailaNBTProvider) {
        	tag.setTag(TAG_NAME, ((IWailaNBTProvider)tile).getWailaTag(new NBTTagCompound()));
        }
        return tag;
	}
}
