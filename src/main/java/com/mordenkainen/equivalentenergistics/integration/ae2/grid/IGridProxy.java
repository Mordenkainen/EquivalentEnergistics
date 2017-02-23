package com.mordenkainen.equivalentenergistics.integration.ae2.grid;

import java.util.EnumSet;

import appeng.api.networking.GridFlags;
import appeng.api.networking.GridNotification;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridBlock;
import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
import appeng.api.util.AEColor;
import appeng.api.util.DimensionalCoord;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

public interface IGridProxy extends IGridBlock {

    @Override
    double getIdlePowerUsage();

    void setIdlePowerUsage(double idle);

    @Override
    EnumSet<GridFlags> getFlags();

    void setFlags(GridFlags... gridFlags);

    @Override
    boolean isWorldAccessible();

    @Override
    DimensionalCoord getLocation();

    @Override
    AEColor getGridColor();

    void setGridColor(AEColor color);

    @Override
    default void onGridNotification(GridNotification gridNotification) {}

    @Override
    default void setNetworkStatus(IGrid grid, int usedChannels) {}

    @Override
    EnumSet<ForgeDirection> getConnectableSides();

    void setConnectableSides(EnumSet<ForgeDirection> validSides);

    @Override
    IGridHost getMachine();

    @Override
    void gridChanged();

    @Override
    ItemStack getMachineRepresentation();

    void setMachineRepresentation(ItemStack stack);

    void writeToNBT(NBTTagCompound tag);

    void readFromNBT(NBTTagCompound tag);

    void onChunkUnload();

    void invalidate();

    boolean onReady();

    boolean isReady();

    boolean isPowered();
    
    boolean meetsChannelRequirements();

    void setOwner(EntityPlayer player);

    IGridNode getNode();

    IGrid getGrid() throws GridAccessException;
    
    boolean isActive();

    static IGridProxy getDefaultProxy(final ItemStack repItem, final IGridProxyable host) {
        return FMLCommonHandler.instance().getEffectiveSide().isClient() ? new NullProxy(host) : new NetworkProxy(host, "node0", repItem, true);
    }

}
