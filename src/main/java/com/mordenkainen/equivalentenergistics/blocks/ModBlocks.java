package com.mordenkainen.equivalentenergistics.blocks;

import com.mordenkainen.equivalentenergistics.blocks.base.tile.TE;
import com.mordenkainen.equivalentenergistics.blocks.base.tile.TEList;
import com.mordenkainen.equivalentenergistics.blocks.condenser.BlockEMCCondenser;
import com.mordenkainen.equivalentenergistics.blocks.crafter.BlockEMCCrafter;
import com.mordenkainen.equivalentenergistics.core.Names;
import com.mordenkainen.equivalentenergistics.core.Reference;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.fml.common.registry.IForgeRegistry;

public final class ModBlocks {

    @ObjectHolder(Reference.MOD_ID + ":" + Names.CRAFTER)
    public static final BlockEMCCrafter CRAFTER = null;
    @ObjectHolder(Reference.MOD_ID + ":" + Names.CONDENSER)
    public static final BlockEMCCondenser CONDENSER = null;

    private ModBlocks() {}

    public static void register(final IForgeRegistry<Block> registry) {
        registerBlock(registry, new BlockEMCCrafter());
        registerBlock(registry, new BlockEMCCondenser());
    }

    public static void registerItemBlocks(final IForgeRegistry<Item> registry) {
        registry.register(CRAFTER.createItemBlock());
        registry.register(CONDENSER.createItemBlock());
    }

    public static void registerModels() {
        CRAFTER.registerItemModel(Item.getItemFromBlock(CRAFTER));
        CONDENSER.registerItemModel(Item.getItemFromBlock(CONDENSER));
    }

    public static void registerBlock(final IForgeRegistry<Block> registry, final Block block) {
        registry.register(block);
        if (block instanceof ITileEntityProvider) {
            final TE teInfo = block.getClass().getDeclaredAnnotation(TE.class);
            if (teInfo == null) {
                final TEList teList = block.getClass().getDeclaredAnnotation(TEList.class);
                if (teList != null) {
                    for (final TE te : teList.value()) {
                        GameRegistry.registerTileEntity(te.tileEntityClass(), te.registryName());
                    }
                }
            } else {
                GameRegistry.registerTileEntity(teInfo.tileEntityClass(), teInfo.registryName());
            }
        }
    }

}
