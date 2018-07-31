package com.mordenkainen.equivalentenergistics.core.proxy;

import com.mordenkainen.equivalentenergistics.core.FixTE;
import com.mordenkainen.equivalentenergistics.core.Reference;

import net.minecraft.item.Item;
import net.minecraft.util.datafix.FixTypes;
import net.minecraftforge.common.util.CompoundDataFixer;
import net.minecraftforge.common.util.ModFixs;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class CommonProxy {

    public void preInit() {
        final FixTE tileFixable = new FixTE();
        final CompoundDataFixer fixer = FMLCommonHandler.instance().getDataFixer();
        final ModFixs modFixs = fixer.init(Reference.MOD_ID, tileFixable.getFixVersion());    //is there a current save format version?
        modFixs.registerFix(FixTypes.BLOCK_ENTITY, tileFixable);
    }

    public void init() {
        initRenderers();
        registerModelBakeryVariants();
    }

    public void postInit() {}

    public boolean isClient() {
        return false;
    }

    public boolean isServer() {
        return true;
    }

    public void initRenderers() {}

    public void registerItemRenderer(final Item item, final int meta, final String name) {}

    public void registerItemRenderer(final Item item, final int meta, final String name, final String variant) {}

    public void registerModelBakeryVariants() {}

}
