package com.mordenkainen.equivalentenergistics.items;

import java.util.List;

import com.mordenkainen.equivalentenergistics.registries.TextureEnum;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class ItemStorageComponent extends ItemBase {

    private static final int NUM_CELLS = 8;

    public ItemStorageComponent() {
        super();

        setMaxStackSize(1);
        setHasSubtypes(true);
    }

    // Item Overrides
    // ------------------------
    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(final int damage) {
        return TextureEnum.EMCSTORAGECOMPONENT.getTexture(damage);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" }) // NOPMD
    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(final Item item, final CreativeTabs tab, final List list) {
        for (int i = 0; i < NUM_CELLS; i++) {
            list.add(new ItemStack(item, 1, i));
        }
    }

    @Override
    public String getUnlocalizedName(final ItemStack stack) {
        return super.getUnlocalizedName() + "." + stack.getItemDamage();
    }
    // ------------------------

}
