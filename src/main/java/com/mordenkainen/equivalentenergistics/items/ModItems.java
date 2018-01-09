package com.mordenkainen.equivalentenergistics.items;

import com.mordenkainen.equivalentenergistics.core.Names;
import com.mordenkainen.equivalentenergistics.core.Reference;

import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.registries.IForgeRegistry;

public class ModItems {

	@ObjectHolder(Reference.MOD_ID + ":" + Names.CRYSTAL)
	public static final ItemEMCCrystal CRYSTAL = null;
	@ObjectHolder(Reference.MOD_ID + ":" + Names.COMPONENT)
	public static final ItemStorageComponent COMPONENT = null;
	@ObjectHolder(Reference.MOD_ID + ":" + Names.BOOK)
	public static final ItemEMCBook EMC_BOOK = null;
	@ObjectHolder(Reference.MOD_ID + ":" + Names.MISC)
	public static final ItemMisc MISC = null;
	@ObjectHolder(Reference.MOD_ID + ":" + Names.CELL)
	public static final ItemEMCCell CELL = null;
	@ObjectHolder(Reference.MOD_ID + ":" + Names.CELL_CREATIVE)
	public static final ItemCellCreative CELL_CREATIVE = null;
	@ObjectHolder(Reference.MOD_ID + ":" + Names.EMC_PATTERN)
	public static final ItemPattern EMC_PATTERN = null;
	
	public static void register(IForgeRegistry<Item> registry) {
		registry.register(new ItemEMCCrystal());
		registry.register(new ItemStorageComponent());
		registry.register(new ItemEMCBook());
		registry.register(new ItemMisc());
		registry.register(new ItemEMCCell());
		registry.register(new ItemCellCreative());
		registry.register(new ItemPattern());
	}

	public static void registerModels() {
		CRYSTAL.registerItemModel();
		COMPONENT.registerItemModel();
		EMC_BOOK.registerItemModel();
		MISC.registerItemModel();
		CELL_CREATIVE.registerItemModel();
		CELL.registerItemModel();
		EMC_PATTERN.registerItemModel();
	}
	
}
