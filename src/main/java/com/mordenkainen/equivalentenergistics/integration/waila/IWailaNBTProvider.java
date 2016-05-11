package com.mordenkainen.equivalentenergistics.integration.waila;

import net.minecraft.nbt.NBTTagCompound;

public interface IWailaNBTProvider {
	
	NBTTagCompound getWailaTag(NBTTagCompound tag);
	
}
