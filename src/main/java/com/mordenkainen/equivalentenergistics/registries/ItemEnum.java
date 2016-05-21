package com.mordenkainen.equivalentenergistics.registries;

import com.mordenkainen.equivalentenergistics.EquivalentEnergistics;
import com.mordenkainen.equivalentenergistics.config.IConfigurable;
import com.mordenkainen.equivalentenergistics.integration.Integration;
import com.mordenkainen.equivalentenergistics.items.ItemEMCBook;
import com.mordenkainen.equivalentenergistics.items.ItemEMCCell;
import com.mordenkainen.equivalentenergistics.items.ItemEMCCrystal;
import com.mordenkainen.equivalentenergistics.items.ItemEMCTotal;
import com.mordenkainen.equivalentenergistics.items.ItemPattern;
import com.mordenkainen.equivalentenergistics.lib.Reference;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import net.minecraftforge.common.config.Configuration;

public enum ItemEnum {
	
	EMCBOOK("EMCBook", new ItemEMCBook(), Integration.Mods.PROJECTE),
	EMCPATTERN("EMCPattern", new ItemPattern(), true),
	EMCCRYSTAL("EMCCrystal", new ItemEMCCrystal()),
	EMCTOTITEM("EMCTotal", new ItemEMCTotal()),
	EMCCELL("EMCCell", new ItemEMCCell());

	private final String internalName;
	
	private Item item;
	
	private Integration.Mods mod;
	
	private boolean enabled = true;

	private boolean hidden;
	
	private String configKey;

	ItemEnum(final String _internalName, final Item _item) {
		this(_internalName, _item, null, false, null);
	}
	
	ItemEnum(final String _internalName, final Item _item, final String _configKey) {
		this(_internalName, _item, _configKey, false, null);
	}
	
	ItemEnum(final String _internalName, final Item _item, final boolean _hidden) {
		this(_internalName, _item, null, _hidden, null);
	}
	
	ItemEnum(final String _internalName, final Item _item, final Integration.Mods _mod) {
		this(_internalName, _item, null, false, _mod);
	}
	
	ItemEnum(final String _internalName, final Item _item, final String _configKey, final Integration.Mods _mod) {
		this(_internalName, _item, _configKey, false, _mod);
	}
	
	ItemEnum(final String _internalName, final Item _item, final boolean _hidden, final Integration.Mods _mod) {
		this(_internalName, _item, null, _hidden, _mod);
	}
	
	ItemEnum(final String _internalName, final Item _item, final String _configKey, final boolean _hidden) {
		this(_internalName, _item, _configKey, _hidden, null);
	}
	
	ItemEnum(final String _internalName, final Item _item, final String _configKey, final boolean _hidden, final Integration.Mods _mod) {
		internalName = _internalName;
		item = _item;
		item.setUnlocalizedName(Reference.MOD_ID + ":" + internalName);
		configKey = _configKey;
		hidden = _hidden;
		mod = _mod;
	}

	public ItemStack getDamagedStack(final int damage) {
		return new ItemStack(item, 1, damage);
	}

	public String getInternalName() {
		return internalName;
	}

	public Item getItem() {
		return item;
	}

	public ItemStack getSizedStack(final int size) {
		return new ItemStack(item, size);
	}

	public String getStatName() {
		return StatCollector.translateToLocal(item.getUnlocalizedName());
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
			enabled = config.get("Items", configKey, true).getBoolean(true);
		}
		if (item instanceof IConfigurable) {
			((IConfigurable)item).loadConfig(config);
		}
		if (isEnabled() && !isHidden()) {
			item.setCreativeTab(EquivalentEnergistics.tabEE);
		}
	}
}
