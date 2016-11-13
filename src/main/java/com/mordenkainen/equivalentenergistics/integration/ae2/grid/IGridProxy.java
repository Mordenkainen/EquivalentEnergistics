package com.mordenkainen.equivalentenergistics.integration.ae2.grid;

import java.util.EnumSet;

import com.mordenkainen.equivalentenergistics.integration.ae2.cache.EMCStorageGrid;

import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.networking.GridFlags;
import appeng.api.networking.GridNotification;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridBlock;
import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
import appeng.api.networking.crafting.ICraftingGrid;
import appeng.api.networking.energy.IEnergyGrid;
import appeng.api.networking.pathing.IPathingGrid;
import appeng.api.networking.security.ISecurityGrid;
import appeng.api.networking.security.MachineSource;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.networking.ticking.ITickManager;
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
    void onGridNotification(GridNotification gridNotification);

    @Override
    void setNetworkStatus(IGrid grid, int usedChannels);

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

    void setOwner(EntityPlayer player);

    IGridNode getNode();

    boolean isActive();

    IGrid getGrid() throws GridAccessException;

    IPathingGrid getPath() throws GridAccessException;

    IStorageGrid getStorage() throws GridAccessException;

    ISecurityGrid getSecurity() throws GridAccessException;

    ICraftingGrid getCrafting() throws GridAccessException;

    IEnergyGrid getEnergy() throws GridAccessException;

    ITickManager getTick() throws GridAccessException;

    EMCStorageGrid getEMCStorage() throws GridAccessException;

    double getAEDemand(double amount);

    double sendAEToNet(double amount, Actionable mode);

    double extractAEPower(double amount, Actionable mode, PowerMultiplier multiplier);

    double getAEMaxEnergy();

    double getAECurrentEnergy();

    static IGridProxy getDefaultProxy(final ItemStack repItem, final IGridProxyable host) {
        return FMLCommonHandler.instance().getEffectiveSide().isClient() ? new NullProxy(host) : new NetworkProxy(host, "node0", repItem, true);
    }

    ItemStack injectItemsForPower(ItemStack stack, MachineSource source);

}
