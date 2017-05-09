package com.mordenkainen.equivalentenergistics.util;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface IDropItems {

    void getDrops(World world, int x, int y, int z, List<ItemStack> drops);

    void disableDrops();
    
}
