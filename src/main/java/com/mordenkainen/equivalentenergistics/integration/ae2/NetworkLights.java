package com.mordenkainen.equivalentenergistics.integration.ae2;

import net.minecraft.util.IStringSerializable;

public enum NetworkLights implements IStringSerializable {
	NONE(0,"none"),
	ERROR(1, "error"),
	POWERED(2, "powered");
	
	private int ID;
    private String name;
    
    private NetworkLights(int ID, String name) {
        this.ID = ID;
        this.name = name;
    }
    
    @Override
    public String getName() {
        return name;
    }

    public int getID() {
        return ID;
    }
    
}
