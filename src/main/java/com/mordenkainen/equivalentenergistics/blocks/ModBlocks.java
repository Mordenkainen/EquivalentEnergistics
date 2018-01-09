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
import net.minecraftforge.registries.IForgeRegistry;

public class ModBlocks {

	@ObjectHolder(Reference.MOD_ID + ":" + Names.CRAFTER)
	public static final BlockEMCCrafter CRAFTER = null;
	@ObjectHolder(Reference.MOD_ID + ":" + Names.CONDENSER)
	public static final BlockEMCCondenser CONDENSER = null;
	
	public static void register(IForgeRegistry<Block> registry) {
		registerBlock(registry, new BlockEMCCrafter());
		registerBlock(registry, new BlockEMCCondenser());
	}
	
	public static void registerItemBlocks(IForgeRegistry<Item> registry) {
		registry.register(CRAFTER.createItemBlock());
		registry.register(CONDENSER.createItemBlock());
	}
	
	public static void registerModels() {
		CRAFTER.registerItemModel(Item.getItemFromBlock(CRAFTER));
		CONDENSER.registerItemModel(Item.getItemFromBlock(CONDENSER));
	}

	public static void registerBlock(IForgeRegistry<Block> registry, Block block) {
    	registry.register(block);
    	if (block instanceof ITileEntityProvider) {
    		TE teInfo = block.getClass().getDeclaredAnnotation(TE.class);
    		if (teInfo != null) {
    			GameRegistry.registerTileEntity(teInfo.tileEntityClass(), teInfo.registryName());
    		} else {
    			TEList teList = block.getClass().getDeclaredAnnotation(TEList.class);
    			if (teList != null) {
    				for (TE te : teList.value()) {
    					GameRegistry.registerTileEntity(te.tileEntityClass(), te.registryName());
        			}
    			}
    		}
    	}
    }
	
}
