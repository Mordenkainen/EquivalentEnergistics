package com.mordenkainen.equivalentenergistics.core.config;

import com.mordenkainen.equivalentenergistics.core.Reference;

import net.minecraftforge.common.config.Config;

@Config(modid = Reference.MOD_ID, name = Reference.MOD_ID)
public final class EqEConfig {
    
    private EqEConfig() {}
    
    public static Misc misc = new Misc();
	public static class Misc {

		@Config.Comment("Enable debug logging.")
		public boolean debug = false;
		
	}
	
	public static EMCAssembler emcAssembler = new EMCAssembler();
	public static class EMCAssembler {

		@Config.RangeDouble(min = 0, max = Integer.MAX_VALUE)
		@Config.Comment("The minimum amount of EMC the EMC Assembler will attempt to use. Any items that have an EMC value less than this will craft in stacks of up to this value.")
		public float maxStackEMC = 131072;
		
		@Config.RangeDouble(min = 0, max = Integer.MAX_VALUE)
		@Config.Comment("The amount of power the EMC Assembler will consume while sitting idle in AE.")
		public double idlePower = 0.0;
		
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
	
	public static EMCCondenser emcCondenser = new EMCCondenser();
	public static class EMCCondenser {

		@Config.RangeDouble(min = 0, max = Integer.MAX_VALUE)
		@Config.Comment("The amount of power the EMC Condenser will consume while sitting idle in AE.")
		public double idlePower = 0.0;
		
		@Config.RangeDouble(min = 0, max = Integer.MAX_VALUE)
		@Config.Comment("The amount of power the EMC Condenser will consume per EMC condensed in AE.")
		public double powerPerEMC = 0.01;
		
		@Config.RangeDouble(min = 0, max = Integer.MAX_VALUE)
		@Config.Comment("The amount of EMC the basic EMC Condenser can convert per tick. This value is scaled by x10 for each tier of Condenser.")
		public float EMCPerTick = 8192;
		
	}
	
	@Config.Comment("EMC Cell capacities.")
	public static EMCCellCaps cellCapacities = new EMCCellCaps();
	public static class EMCCellCaps {
		
		public float creativeCell = 16384000000F;
		public float tier1_Cell = 1000000F;
		public float tier2_Cell = 4000000F;
		public float tier3_Cell = 16000000F;
		public float tier4_Cell = 64000000F;
		public float tier5_Cell = 256000000F;
		public float tier6_Cell = 1024000000F;
		public float tier7_Cell = 4096000000F;
		public float tier8_Cell = 16384000000F;
		
	}
	
	@Config.Comment("EMC Cell power drain.")
	public static EMCCellDrain cellPowerDrain = new EMCCellDrain();
	public static class EMCCellDrain {
		
		public double tier1_Cell = 0.1;
		public double tier2_Cell = 0.2;
		public double tier3_Cell = 0.4;
		public double tier4_Cell = 0.8;
		public double tier5_Cell = 1.6;
		public double tier6_Cell = 3.2;
		public double tier7_Cell = 6.4;
		public double tier8_Cell = 12.8;
		
	}
	
}
