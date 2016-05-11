package com.mordenkainen.equivalentenergistics.util;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class DimensionalLocation {
	public int x;
	public int y;
	public int z;
	public World world;

	public DimensionalLocation(int x, int y, int z, World world) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.world = world;
	}

	public TileEntity getTE() {
		return world.getTileEntity(x, y, z);
	}

	public int hashCode() {
		int prime = 31;
		int result = 1;
		result = prime * result + (world == null ? 0 : world.hashCode());
		result = prime * result + x;
		result = prime * result + y;
		result = prime * result + z;
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		DimensionalLocation other = (DimensionalLocation)obj;
		if (world == null) {
			if (other.world != null) {
				return false;
			}
		}
		else if (!world.equals(other.world)) {
			return false;
		}
		if ((x != other.x) || (y != other.y) || (z != other.z)) {
			return false;
		}
		return true;
	}
}
