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
	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		NBTTagCompound tag = accessor.getNBTData();
		if(tag.hasKey("TileData")) {
			currenttip.add("Stored EMC: " + tag.getCompoundTag("TileData").getFloat("currentEMC"));
			if(tag.getCompoundTag("TileData").hasKey("owner")) {
				currenttip.add("Owner: " + tag.getCompoundTag("TileData").getString("owner"));
			}
		}
		return currenttip;
	}
	
	@Override
	public ItemStack getWailaStack(IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		return null;
	}

	@Override
	public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		return null;
	}

	@Override
	public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		return null;
	}

	@Override
	public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, int x, int y, int z) {
		if(te instanceof IWailaNBTProvider) {
        	tag.setTag("TileData", ((IWailaNBTProvider)te).getWailaTag(new NBTTagCompound()));
        }
        return tag;
	}
}
