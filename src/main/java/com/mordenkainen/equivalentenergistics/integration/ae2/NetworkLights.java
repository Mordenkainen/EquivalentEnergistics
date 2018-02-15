package com.mordenkainen.equivalentenergistics.integration.ae2;

import net.minecraft.util.IStringSerializable;

public enum NetworkLights implements IStringSerializable {
    NONE(0,"none"),
    ERROR(1, "error"),
    POWERED(2, "powered");

    private int id;
    private String name;

    NetworkLights(final int id, final String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    public int getID() {
        return id;
    }

}
