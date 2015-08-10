package com.mordenkainen.equivalentenergistics.lib;

public final class Ref {
	public static final String MOD_ID = "equivalentenergistics";
	public static final String MOD_VERSION = "0.5";
	public static final String MOD_NAME = "Equivalent Energistics";
	public static final String MOD_DEPENDENCIES = "required-after:appliedenergistics2;after:EE3;after:ProjectE";
	
	public static final String TEXTURE_PREFIX = MOD_ID + ":";
	public static final String PROXY_LOC = "com.mordenkainen.equivalentenergistics.proxy.";
	
	private Ref() {}
	
	public static String getId(final String str) {
		return MOD_ID + ":" + str;
	}
}
