package com.mordenkainen.equivalentenergistics.util;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class DimensionalLocation {
	
	private static final int PRIME = 31;
	
	public int x;
	public int y;
	public int z;
	public World world;

	public DimensionalLocation(final int x, final int y, final int z, final World world) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.world = world;
	}

	public TileEntity getTE() {
		return world.getTileEntity(x, y, z);
	}

	public int hashCode() {
		int result = 1;
		result = PRIME * result + (world == null ? 0 : world.hashCode());
		result = PRIME * result + x;
		result = PRIME * result + y;
		result = PRIME * result + z;
		return result;
	}

	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final DimensionalLocation other = (DimensionalLocation)obj;
		if (world == null) {
			if (other.world != null) {
				return false;
			}
		} else if (!world.equals(other.world)) {
			return false;
		}
		
		return x == other.x && y == other.y && z == other.z;
	}
	
}
