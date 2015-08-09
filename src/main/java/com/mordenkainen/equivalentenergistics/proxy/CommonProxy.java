package com.mordenkainen.equivalentenergistics.proxy;

public class CommonProxy {
	public int EMCCrafterRenderer;

	public void initRenderers() {}
	
	public void unmetDependency() {
		throw new RuntimeException("Equivalent Energistics requires either Equivalent Exchange 3 or ProjectE to be installed!");
	}
}
