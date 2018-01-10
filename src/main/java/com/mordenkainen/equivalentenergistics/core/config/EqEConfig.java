package com.mordenkainen.equivalentenergistics.core.config;

import com.mordenkainen.equivalentenergistics.core.Reference;

import net.minecraftforge.common.config.Config;

@Config(modid = Reference.MOD_ID, name = Reference.MOD_ID)
public final class EqEConfig {

    public static MiscSettings misc = new MiscSettings();

    public static EMCAssembler emcAssembler = new EMCAssembler();

    public static EMCCondenser emcCondenser = new EMCCondenser();

    @Config.Comment("EMC Cell capacities.")
    public static EMCCellCaps cellCapacities = new EMCCellCaps();

    @Config.Comment("EMC Cell power drain.")
    public static EMCCellDrain cellPowerDrain = new EMCCellDrain();

    private EqEConfig() {}

    public static class MiscSettings {

        @Config.Comment("Enable debug logging.")
        public boolean debug;

    }

    public static class EMCAssembler {

        @Config.RangeDouble(min = 0, max = Integer.MAX_VALUE)
        @Config.Comment("The minimum amount of EMC the EMC Assembler will attempt to use. Any items that have an EMC value less than this will craft in stacks of up to this value.")
        public float maxStackEMC = 131072;

        @Config.RangeDouble(min = 0, max = Integer.MAX_VALUE)
        @Config.Comment("The amount of power the EMC Assembler will consume while sitting idle in AE.")
        public double idlePower;

        @Config.RangeDouble(min = 0, max = Integer.MAX_VALUE)
        @Config.Comment("The amount of power the EMC Assembler will consume per EMC of items crafted in AE.")
        public double powerPerEMC = 0.01;

        @Config.RangeInt(min = 0, max = 256)
        @Config.Comment("Number of ticks it takes the basic EMC Assembler to craft an item.")
        public int craftingTime = 20;

        @Config.RangeInt(min = 0, max = Integer.MAX_VALUE)
        @Config.Comment("Number of seconds in between checks to see if any new transmutaions have been added to the network. Low values may consume a large amount of CPU and cause TPS issues.")
        public int refreshTime = 10;

    }

    public static class EMCCondenser {

        @Config.RangeDouble(min = 0, max = Integer.MAX_VALUE)
        @Config.Comment("The amount of power the EMC Condenser will consume while sitting idle in AE.")
        public double idlePower;

        @Config.RangeDouble(min = 0, max = Integer.MAX_VALUE)
        @Config.Comment("The amount of power the EMC Condenser will consume per EMC condensed in AE.")
        public double powerPerEMC = 0.01;

        @Config.RangeDouble(min = 0, max = Integer.MAX_VALUE)
        @Config.Comment("The amount of EMC the basic EMC Condenser can convert per tick. This value is scaled by x10 for each tier of Condenser.")
        public float emcPerTick = 8192;

    }

    public static class EMCCellCaps {

        public float creativeCell = 16384000000F;
        public float tier1Cell = 1000000F;
        public float tier2Cell = 4000000F;
        public float tier3Cell = 16000000F;
        public float tier4Cell = 64000000F;
        public float tier5Cell = 256000000F;
        public float tier6Cell = 1024000000F;
        public float tier7Cell = 4096000000F;
        public float tier8Cell = 16384000000F;

    }

    public static class EMCCellDrain {

        public double tier1Cell = 0.1;
        public double tier2Cell = 0.2;
        public double tier3Cell = 0.4;
        public double tier4Cell = 0.8;
        public double tier5Cell = 1.6;
        public double tier6Cell = 3.2;
        public double tier7Cell = 6.4;
        public double tier8Cell = 12.8;

    }

}
