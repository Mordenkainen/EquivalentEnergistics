package com.mordenkainen.equivalentenergistics.registries;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.mordenkainen.equivalentenergistics.EquivalentEnergistics;
import com.mordenkainen.equivalentenergistics.blocks.BlockEMCCondenser;
import com.mordenkainen.equivalentenergistics.blocks.BlockEMCCrafter;
import com.mordenkainen.equivalentenergistics.config.IConfigurable;
import com.mordenkainen.equivalentenergistics.integration.Integration;
import com.mordenkainen.equivalentenergistics.items.itemblocks.ItemBlockCondenser;
import com.mordenkainen.equivalentenergistics.lib.Reference;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.config.Configuration;

public enum BlockEnum {

    EMCCONDENSER("EMCCondenser", new BlockEMCCondenser(), ItemBlockCondenser.class, Predicates.and(Integration.Mods.AE2.getTest(), Predicates.or(Integration.Mods.EE3.getTest(), Integration.Mods.PROJECTE.getTest())), "EMCCondenser", false),
    EMCCRAFTER("EMCCrafter", new BlockEMCCrafter(), Predicates.and(Integration.Mods.AE2.getTest(), Predicates.or(Integration.Mods.EE3.getTest(), Integration.Mods.PROJECTE.getTest())), "EMCCrafter");

    private final String internalName;

    private Block block;

    private Class<? extends ItemBlock> itemBlockClass;

    private boolean enabled = true;

    private boolean hidden;

    private String configKey;

    private Predicate<?> requirements;

    BlockEnum(final String internalName, final Block block) {
        this(internalName, block, ItemBlock.class, Predicates.alwaysTrue(), null, false);
    }

    BlockEnum(final String internalName, final Block block, final Predicate<?> requirements) {
        this(internalName, block, ItemBlock.class, requirements, null, false);
    }

    BlockEnum(final String internalName, final Block block, final Predicate<?> requirements, final String configKey) {
        this(internalName, block, ItemBlock.class, requirements, configKey, false);
    }

    BlockEnum(final String internalName, final Block block, final String configKey) {
        this(internalName, block, ItemBlock.class, Predicates.alwaysTrue(), configKey, false);
    }

    BlockEnum(final String internalName, final Block block, final boolean hidden) {
        this(internalName, block, ItemBlock.class, Predicates.alwaysTrue(), null, hidden);
    }

    BlockEnum(final String internalName, final Block block, final Predicate<?> requirements, final boolean hidden) {
        this(internalName, block, ItemBlock.class, requirements, null, hidden);
    }

    BlockEnum(final String internalName, final Block block, final String configKey, final boolean hidden) {
        this(internalName, block, ItemBlock.class, Predicates.alwaysTrue(), configKey, hidden);
    }

    BlockEnum(final String internalName, final Block block, final Class<? extends ItemBlock> itemBlockClass, final Predicate<?> requirements, final String configKey, final boolean hidden) {
        this.internalName = internalName;
        this.block = block;
        this.block.setBlockName(Reference.MOD_ID + ":" + internalName);
        this.itemBlockClass = itemBlockClass;
        this.requirements = requirements;
        this.configKey = configKey;
        this.hidden = hidden;
    }

    public Block getBlock() {
        return block;
    }

    public String getInternalName() {
        return internalName;
    }

    public Class<? extends ItemBlock> getItemBlockClass() {
        return itemBlockClass;
    }

    public String getStatName() {
        return StatCollector.translateToLocal(block.getUnlocalizedName() + ".name");
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

    public static void loadConfig(final Configuration config) {
        for (final BlockEnum current : BlockEnum.values()) {
            if (current.configKey != null) {
                current.enabled = config.get("Blocks", current.configKey, true).getBoolean(true);
            }
            if (current.block instanceof IConfigurable) {
                ((IConfigurable) current.block).loadConfig(config);
            }
            if (current.isEnabled() && !current.isHidden()) {
                current.block.setCreativeTab(EquivalentEnergistics.tabEE);
            }
        }
    }

    public static void registerBlocks() {
        for (final BlockEnum current : BlockEnum.values()) {
            if (current.isEnabled()) {
                GameRegistry.registerBlock(current.getBlock(), current.getItemBlockClass(), current.getInternalName());
            }
        }
    }

}
