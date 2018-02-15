package com.mordenkainen.equivalentenergistics.util;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IDropItems {

    void getDrops(World world, BlockPos pos, List<ItemStack> drops);

    void disableDrops();

}
