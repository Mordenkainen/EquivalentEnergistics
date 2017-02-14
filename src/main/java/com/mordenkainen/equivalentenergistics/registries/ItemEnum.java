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

import cpw.mods.fml.common.registry.GameRegistry;
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

    ItemEnum(final String internalName, final Item item) {
        this(internalName, item, null, false, Predicates.alwaysTrue());
    }

    ItemEnum(final String internalName, final Item item, final String configKey) {
        this(internalName, item, configKey, false, Predicates.alwaysTrue());
    }

    ItemEnum(final String internalName, final Item item, final boolean hidden) {
        this(internalName, item, null, hidden, Predicates.alwaysTrue());
    }

    ItemEnum(final String internalName, final Item item, final Predicate<?> requirements) {
        this(internalName, item, null, false, requirements);
    }

    ItemEnum(final String internalName, final Item item, final String configKey, final Predicate<?> requirements) {
        this(internalName, item, configKey, false, requirements);
    }

    ItemEnum(final String internalName, final Item item, final boolean hidden, final Predicate<?> requirements) {
        this(internalName, item, null, hidden, requirements);
    }

    ItemEnum(final String internalName, final Item item, final String configKey, final boolean hidden) {
        this(internalName, item, configKey, hidden, Predicates.alwaysTrue());
    }

    ItemEnum(final String internalName, final Item item, final String configKey, final boolean hidden, final Predicate<?> requirements) {
        this.internalName = internalName;
        this.item = item;
        this.item.setUnlocalizedName(Reference.MOD_ID + ":" + internalName);
        this.configKey = configKey;
        this.hidden = hidden;
        this.requirements = requirements;
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
    
    public boolean isSameItem(final ItemStack stack) {
    	if (stack == null || stack.getItem() == null) {
    		return false;
    	}
    	return stack.getItem() == item;
    }

    public static void loadConfig(final Configuration config) {
    	for (final ItemEnum current : ItemEnum.values()) {
    		if (current.configKey != null) {
    			current.enabled = config.get("Items", current.configKey, true).getBoolean(true);
            }
            if (current.item instanceof IConfigurable) {
                ((IConfigurable) current.item).loadConfig(config);
            }
            if (current.isEnabled() && !current.isHidden()) {
            	current.item.setCreativeTab(EquivalentEnergistics.tabEE);
            }
    	}
    }

    public static void registerItems() {
        for (final ItemEnum current : ItemEnum.values()) {
            if (current.isEnabled()) {
                GameRegistry.registerItem(current.getItem(), current.getInternalName());
            }
        }
    }
    
    public static boolean isCrystal(final ItemStack stack) {
        return EMCCRYSTAL.isSameItem(stack) || EMCCRYSTALOLD.isSameItem(stack);
    }
    
}
