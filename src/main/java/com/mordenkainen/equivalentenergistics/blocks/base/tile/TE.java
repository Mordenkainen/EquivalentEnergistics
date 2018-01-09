package com.mordenkainen.equivalentenergistics.blocks.base.tile;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import net.minecraft.tileentity.TileEntity;

@Retention(RUNTIME)
@Target(TYPE)
public @interface TE {
	
	Class<? extends TileEntity> tileEntityClass();
	String registryName();
	
}
