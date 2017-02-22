package com.mordenkainen.equivalentenergistics.asm;

import com.mordenkainen.equivalentenergistics.lib.Reference;

import cpw.mods.fml.common.DummyModContainer;
import cpw.mods.fml.common.ModMetadata;

public class EqECore extends DummyModContainer {
    public EqECore() {
        super(new ModMetadata());
        final ModMetadata metadata = getMetadata();
        metadata.modId = Reference.MOD_ID + "Core";
        metadata.name = Reference.MOD_NAME + " Core";
        metadata.version = Reference.MOD_VERSION;
        metadata.authorList.add("Mordenkainen");
    }
}
