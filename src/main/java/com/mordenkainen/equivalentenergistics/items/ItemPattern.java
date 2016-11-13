package com.mordenkainen.equivalentenergistics.items;

import com.mordenkainen.equivalentenergistics.integration.ae2.EMCCraftingPattern;
import com.mordenkainen.equivalentenergistics.registries.ItemEnum;

import appeng.api.implementations.ICraftingPatternItem;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import cpw.mods.fml.common.Optional;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

@Optional.Interface(iface = "appeng.api.implementations.ICraftingPatternItem", modid = "appliedenergistics2") // NOPMD
public class ItemPattern extends ItemBase implements ICraftingPatternItem {

    public ItemPattern() {
        super();
        setMaxStackSize(1);
    }

    // Item Overrides
    // ------------------------
    @Override
    public EnumRarity getRarity(final ItemStack stack) {
        return EnumRarity.rare;
    }
    // ------------------------

    // ICraftingPatternItem Overrides
    // ------------------------
    @Optional.Method(modid = "appliedenergistics2")
    @Override
    public ICraftingPatternDetails getPatternForItem(final ItemStack stack, final World world) {
        return EMCCraftingPattern.get(ItemStack.loadItemStackFromNBT(stack.getTagCompound()));
    }
    // ------------------------

    public static ItemStack getItemForPattern(final ItemStack target) {
        final ItemStack pattern = new ItemStack(ItemEnum.EMCPATTERN.getItem());
        pattern.setTagCompound(new NBTTagCompound());
        target.writeToNBT(pattern.getTagCompound());
        return pattern;
    }

}
