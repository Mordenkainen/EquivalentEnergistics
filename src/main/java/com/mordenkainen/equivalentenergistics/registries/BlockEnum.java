package com.mordenkainen.equivalentenergistics.registries;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import com.mordenkainen.equivalentenergistics.EquivalentEnergistics;
import com.mordenkainen.equivalentenergistics.blocks.BlockEMCCondenser;
import com.mordenkainen.equivalentenergistics.blocks.BlockEMCCrafter;
import com.mordenkainen.equivalentenergistics.config.IConfigurable;
import com.mordenkainen.equivalentenergistics.integration.Integration;
import com.mordenkainen.equivalentenergistics.lib.Reference;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.StatCollector;

import net.minecraftforge.common.config.Configuration;

public enum BlockEnum {

	EMCCONDENSER("EMCCondenser", new BlockEMCCondenser(), Predicates.and(Integration.Mods.AE2.getTest(), Predicates.or(Integration.Mods.EE3.getTest(), Integration.Mods.PROJECTE.getTest())), "EMCCondenser"),
	EMCCRAFTER("EMCCrafter", new BlockEMCCrafter(), Predicates.and(Integration.Mods.AE2.getTest(), Predicates.or(Integration.Mods.EE3.getTest(), Integration.Mods.PROJECTE.getTest())), "EMCCrafter");

	private final String internalName;
	
	private Block block;
	
	private Class<? extends ItemBlock> itemBlockClass;

	private boolean enabled = true;
	
	private boolean hidden;
	
	private String configKey;
	
	private Predicate<?> requirements;
	
	BlockEnum(final String _internalName, final Block _block) {
		this(_internalName, _block, ItemBlock.class, Predicates.alwaysTrue(), null, false);
	}
	
	BlockEnum(final String _internalName, final Block _block, final Predicate<?> _requirements) {
		this(_internalName, _block, ItemBlock.class, _requirements, null, false);
	}
	
	BlockEnum(final String _internalName, final Block _block, final Predicate<?> _requirements, final String _configKey) {
		this(_internalName, _block, ItemBlock.class, _requirements, _configKey, false);
	}
	
	BlockEnum(final String _internalName, final Block _block, final String _configKey) {
		this(_internalName, _block, ItemBlock.class, Predicates.alwaysTrue(), _configKey, false);
	}
	
	BlockEnum(final String _internalName, final Block _block, final boolean _hidden) {
		this(_internalName, _block, ItemBlock.class, Predicates.alwaysTrue(), null, _hidden);
	}
	
	BlockEnum(final String _internalName, final Block _block, final Predicate<?> _requirements, final boolean _hidden) {
		this(_internalName, _block, ItemBlock.class, _requirements, null, _hidden);
	}
	
	BlockEnum(final String _internalName, final Block _block, final String _configKey, final boolean _hidden) {
		this(_internalName, _block, ItemBlock.class, Predicates.alwaysTrue(), _configKey, _hidden);
	}

	BlockEnum(final String _internalName, final Block _block, final Class<? extends ItemBlock> _itemBlockClass, final Predicate<?> _requirements, final String _configKey, final boolean _hidden) {
		internalName = _internalName;
		block = _block;
		block.setBlockName(Reference.MOD_ID + ":" + internalName);
		itemBlockClass = _itemBlockClass;
		requirements = _requirements;
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
	
	public Predicate<?> getReqs() {
		return requirements;
	}
	
	public boolean isEnabled() {
		return enabled && requirements.apply(null);
	}
	
	public boolean isHidden() {
		return hidden;
	}
	
	public void loadConfig(final Configuration config) {
		if (configKey != null) {
			enabled = config.get("Blocks", configKey, true).getBoolean(true);
		}
		if (block instanceof IConfigurable) {
			((IConfigurable)block).loadConfig(config);
		}
		if (isEnabled() && !isHidden()) {
			block.setCreativeTab(EquivalentEnergistics.tabEE);
		}
	}
	
}
