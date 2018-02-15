package com.mordenkainen.equivalentenergistics.items;

import java.util.List;

import com.mordenkainen.equivalentenergistics.core.Names;
import com.mordenkainen.equivalentenergistics.integration.ae2.cache.crafting.EMCCraftingGrid;
import com.mordenkainen.equivalentenergistics.items.base.ItemBase;

import appeng.api.implementations.ICraftingPatternItem;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class ItemPattern extends ItemBase implements ICraftingPatternItem {

    public ItemPattern() {
        super(Names.EMC_PATTERN);
        setMaxStackSize(1);
        setCreativeTab(CreativeTabs.SEARCH);
    }

    @Override
    public ICraftingPatternDetails getPatternForItem(final ItemStack stack, final World world) {
        return EMCCraftingGrid.getPattern(ItemStack.loadItemStackFromNBT(stack.getTagCompound()));
    }

    public static ItemStack getItemForPattern(final ItemStack target) {
        final ItemStack pattern = new ItemStack(ModItems.EMC_PATTERN);
        pattern.setTagCompound(new NBTTagCompound());
        target.writeToNBT(pattern.getTagCompound());
        return pattern;
    }
    
    @Override
    public void getSubItems(final Item item, final CreativeTabs tab, final List<ItemStack> items) {}

}
