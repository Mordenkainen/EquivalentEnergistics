package com.mordenkainen.equivalentenergistics.lib;

public class Ref {
	public static final String MOD_ID = "equivalentenergistics";
	public static final String MOD_VERSION = "0.1";
	public static final String MOD_NAME = "Equivalent Energistics";
	public static final String MOD_DEPENDENCIES = "required-after:appliedenergistics2;required-after:EE3";
	
	public static final String TEXTURE_PREFIX = MOD_ID + ":";
	
	public static String getId(String str) {
		return MOD_ID + ":" + str;
	}
}
