package com.mordenkainen.equivalentenergistics.util;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface IDropItems {

    void getDrops(final World world, final int x, final int y, final int z, final List<ItemStack> drops);

}
