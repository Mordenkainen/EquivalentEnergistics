package com.mordenkainen.equivalentenergistics.lib;

public final class Ref {
	public static final String MOD_ID = "equivalentenergistics";
	public static final String MOD_VERSION = "0.1";
	public static final String MOD_NAME = "Equivalent Energistics";
	public static final String MOD_DEPENDENCIES = "required-after:appliedenergistics2;required-after:EE3";
	
	public static final String TEXTURE_PREFIX = MOD_ID + ":";
	
	private Ref () {}
	
	public static String getId(final String str) {
		return MOD_ID + ":" + str;
	}
}
