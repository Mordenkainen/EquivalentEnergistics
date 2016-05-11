package com.mordenkainen.equivalentenergistics.integration.ae2.grid;

import appeng.api.networking.IGridHost;
import appeng.api.util.DimensionalCoord;

public interface IGridProxyable extends IGridHost {
	
	IGridProxy getProxy();

	DimensionalCoord getLocation();

	void gridChanged();
}
