package com.mordenkainen.equivalentenergistics.integration.waila;

import java.util.List;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public abstract class WailaHUDBase implements IWailaDataProvider {

	protected static final String TAG_NAME = "TileData";

	@Override
	public ItemStack getWailaStack(final IWailaDataAccessor accessor, final IWailaConfigHandler config) {
	    return null;
	}

	@Override
	public List<String> getWailaHead(final ItemStack itemStack, final List<String> currenttip, final IWailaDataAccessor accessor, final IWailaConfigHandler config) {
	    return null;
	}

	@Override
	public List<String> getWailaTail(final ItemStack itemStack, final List<String> currenttip, final IWailaDataAccessor accessor, final IWailaConfigHandler config) {
	    return null;
	}

	@Override
	public NBTTagCompound getNBTData(final EntityPlayerMP player, final TileEntity tile, final NBTTagCompound tag, final World world, final int x, final int y, final int z) {
	    if (tile instanceof IWailaNBTProvider) {
	        tag.setTag(TAG_NAME, ((IWailaNBTProvider) tile).getWailaTag(new NBTTagCompound()));
	    }
	    return tag;
	}

}
