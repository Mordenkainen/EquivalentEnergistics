package com.mordenkainen.equivalentenergistics.registries;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.mordenkainen.equivalentenergistics.EquivalentEnergistics;
import com.mordenkainen.equivalentenergistics.config.IConfigurable;
import com.mordenkainen.equivalentenergistics.integration.Integration;
import com.mordenkainen.equivalentenergistics.items.ItemEMCBook;
import com.mordenkainen.equivalentenergistics.items.ItemEMCCell;
import com.mordenkainen.equivalentenergistics.items.ItemEMCCrystal;
import com.mordenkainen.equivalentenergistics.items.ItemEMCCrystalOld;
import com.mordenkainen.equivalentenergistics.items.ItemMisc;
import com.mordenkainen.equivalentenergistics.items.ItemPattern;
import com.mordenkainen.equivalentenergistics.items.ItemStorageComponent;
import com.mordenkainen.equivalentenergistics.lib.Reference;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.config.Configuration;

public enum ItemEnum {

    EMCBOOK("EMCBook", new ItemEMCBook(), Integration.Mods.PROJECTE.getTest()),
    EMCPATTERN("EMCPattern", new ItemPattern(), true),
    EMCCRYSTAL("EMCStorageItem", new ItemEMCCrystal()),
    EMCCRYSTALOLD("EMCCrystal", new ItemEMCCrystalOld()),
    EMCCELL("EMCCell", new ItemEMCCell()),
    MISCITEM("ItemMisc", new ItemMisc()),
    CELLCOMPONENT("EMCStorageComponent", new ItemStorageComponent());

    private final String internalName;

    private Item item;

    private Predicate<?> requirements;

    private boolean enabled = true;

    private boolean hidden;

    private String configKey;

    ItemEnum(final String _internalName, final Item _item) {
        this(_internalName, _item, null, false, Predicates.alwaysTrue());
    }

    ItemEnum(final String _internalName, final Item _item, final String _configKey) {
        this(_internalName, _item, _configKey, false, Predicates.alwaysTrue());
    }

    ItemEnum(final String _internalName, final Item _item, final boolean _hidden) {
        this(_internalName, _item, null, _hidden, Predicates.alwaysTrue());
    }

    ItemEnum(final String _internalName, final Item _item, final Predicate<?> _requirements) {
        this(_internalName, _item, null, false, _requirements);
    }

    ItemEnum(final String _internalName, final Item _item, final String _configKey, final Predicate<?> _requirements) {
        this(_internalName, _item, _configKey, false, _requirements);
    }

    ItemEnum(final String _internalName, final Item _item, final boolean _hidden, final Predicate<?> _requirements) {
        this(_internalName, _item, null, _hidden, _requirements);
    }

    ItemEnum(final String _internalName, final Item _item, final String _configKey, final boolean _hidden) {
        this(_internalName, _item, _configKey, _hidden, Predicates.alwaysTrue());
    }

    ItemEnum(final String _internalName, final Item _item, final String _configKey, final boolean _hidden, final Predicate<?> _requirements) {
        internalName = _internalName;
        item = _item;
        item.setUnlocalizedName(Reference.MOD_ID + ":" + internalName);
        configKey = _configKey;
        hidden = _hidden;
        requirements = _requirements;
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

    public ItemStack getDamagedStack(final int damage) {
        return new ItemStack(item, 1, damage);
    }

    public ItemStack getFullStack() {
        return new ItemStack(item, 64);
    }

    public ItemStack getStack(final int size, final int damage) {
        return new ItemStack(item, size, damage);
    }

    public String getStatName() {
        return StatCollector.translateToLocal(item.getUnlocalizedName());
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
            enabled = config.get("Items", configKey, true).getBoolean(true);
        }
        if (item instanceof IConfigurable) {
            ((IConfigurable) item).loadConfig(config);
        }
        if (isEnabled() && !isHidden()) {
            item.setCreativeTab(EquivalentEnergistics.tabEE);
        }
    }

}
