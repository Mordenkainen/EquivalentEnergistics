package com.mordenkainen.equivalentenergistics.registries;

import java.lang.reflect.Method;

import com.mordenkainen.equivalentenergistics.EquivalentEnergistics;
import com.mordenkainen.equivalentenergistics.blocks.BlockEMCCondenser;
import com.mordenkainen.equivalentenergistics.blocks.BlockEMCCrafter;
import com.mordenkainen.equivalentenergistics.integration.Integration;
import com.mordenkainen.equivalentenergistics.lib.Reference;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.StatCollector;

import net.minecraftforge.common.config.Configuration;

public enum BlockEnum {

	EMCCONDENSER("EMCCondenser", new BlockEMCCondenser(), "EMCCondenser"),
	EMCCRAFTER("EMCCrafter", new BlockEMCCrafter(), "EMCCrafter");

	private final String internalName;
	
	private Block block;
	
	private Class<? extends ItemBlock> itemBlockClass;

	private boolean enabled = true;
	
	private boolean hidden;
	
	private String configKey;
	
	private Integration.Mods mod;
	
	BlockEnum(final String _internalName, final Block _block) {
		this(_internalName, _block, ItemBlock.class, null, null, false);
	}
	
	BlockEnum(final String _internalName, final Block _block, final Integration.Mods _mod) {
		this(_internalName, _block, ItemBlock.class, _mod, null, false);
	}
	
	BlockEnum(final String _internalName, final Block _block, final String _configKey) {
		this(_internalName, _block, ItemBlock.class, null, _configKey, false);
	}
	
	BlockEnum(final String _internalName, final Block _block, final boolean _hidden) {
		this(_internalName, _block, ItemBlock.class, null, null, _hidden);
	}
	
	BlockEnum(final String _internalName, final Block _block, final Integration.Mods _mod, final boolean _hidden) {
		this(_internalName, _block, ItemBlock.class, _mod, null, _hidden);
	}
	
	BlockEnum(final String _internalName, final Block _block, final String _configKey, final boolean _hidden) {
		this(_internalName, _block, ItemBlock.class, null, _configKey, _hidden);
	}

	BlockEnum(final String _internalName, final Block _block, final Class<? extends ItemBlock> _itemBlockClass, final Integration.Mods _mod, final String _configKey, final boolean _hidden) {
		internalName = _internalName;
		block = _block;
		block.setBlockName(Reference.MOD_ID + ":" + internalName);
		itemBlockClass = _itemBlockClass;
		mod = _mod;
		configKey = _configKey;
		hidden = _hidden;
	}

	public Block getBlock() {
		return this.block;
	}

	public String getInternalName() {
		return this.internalName;
	}

	public Class<? extends ItemBlock> getItemBlockClass() {
		return this.itemBlockClass;
	}

	public String getStatName() {
		return StatCollector.translateToLocal(this.block.getUnlocalizedName() + ".name");
	}
	
	public Integration.Mods getMod() {
		return mod;
	}
	
	public boolean isEnabled() {
		return enabled && (mod == null || mod.isEnabled());
	}
	
	public boolean isHidden() {
		return hidden;
	}
	
	public void loadConfig(final Configuration config) {
		if(configKey != null) {
			enabled = config.get("Blocks", configKey, true).getBoolean(true);
		}
		try {
			final Method loadConfig = block.getClass().getDeclaredMethod("loadConfig", Configuration.class);
			loadConfig.invoke(null, config);
		} catch (Exception e) {}
		if (isEnabled() && !isHidden()) {
			block.setCreativeTab(EquivalentEnergistics.tabEE);
		}
	}
}
