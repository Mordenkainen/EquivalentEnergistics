package com.mordenkainen.equivalentenergistics.integration.ae2.grid;

import java.util.Collections;
import java.util.EnumSet;

import com.mordenkainen.equivalentenergistics.blocks.base.tile.EqETileBase;
import com.mordenkainen.equivalentenergistics.core.EventHandler;
import com.mordenkainen.equivalentenergistics.util.CommonUtils;

import appeng.api.AEApi;
import appeng.api.networking.GridFlags;
import appeng.api.networking.GridNotification;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridBlock;
import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
import appeng.api.networking.events.MENetworkPowerIdleChange;
import appeng.api.util.AEColor;
import appeng.api.util.DimensionalCoord;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class AEProxy implements IGridBlock {

    private final IAEProxyHost proxy;
    private IGridNode node;
    private double idleDraw;
    private EnumSet<GridFlags> flags = EnumSet.noneOf(GridFlags.class);
    private final boolean worldNode;
    private AEColor gridColor = AEColor.TRANSPARENT;
    private EnumSet<EnumFacing> validSides;
    private ItemStack displayStack;
    private final String nbtName;
    private NBTTagCompound data;
    private boolean ready;
    private EntityPlayer owner;

    public AEProxy(final IAEProxyHost proxy, final String name, final ItemStack displayStack, final boolean inWorld) {
        this.proxy = proxy;
        nbtName = name;
        worldNode = inWorld;
        this.displayStack = displayStack;
        validSides = EnumSet.allOf(EnumFacing.class);
    }

    @Override
    public double getIdlePowerUsage() {
        return idleDraw;
    }

    public void setIdlePowerUsage(final double idle) {
        idleDraw = idle;

        if (node != null) {
            try {
                getGrid().postEvent(new MENetworkPowerIdleChange(node));
            } catch (final GridAccessException e) {
                CommonUtils.debugLog("AEProxy:setIdlePowerUsage: Error accessing grid:", e);
            }
        }
    }

    @Override
    public EnumSet<GridFlags> getFlags() {
        return flags;
    }

    public void setFlags(final GridFlags... gridFlags) {
        final EnumSet<GridFlags> newFlags = EnumSet.noneOf(GridFlags.class);

        Collections.addAll(newFlags, gridFlags);

        flags = newFlags;
    }

    @Override
    public boolean isWorldAccessible() {
        return worldNode;
    }

    @Override
    public DimensionalCoord getLocation() {
        return proxy.getLocation();
    }

    @Override
    public AEColor getGridColor() {
        return gridColor;
    }

    public void setColor(final AEColor newColor) {
        gridColor = newColor;
    }

    @Override
    public void onGridNotification(final GridNotification notification) {}

    @Override
    public void setNetworkStatus(final IGrid grid, final int channelsInUse) {}

    @Override
    public EnumSet<EnumFacing> getConnectableSides() {
        return validSides;
    }

    public void setConnectableSides(final EnumSet<EnumFacing> validSides) {
        this.validSides = validSides;
        if (node != null) {
            node.updateState();
        }
    }

    @Override
    public IGridHost getMachine() {
        return proxy;
    }

    @Override
    public void gridChanged() {
        proxy.gridChanged();
    }

    @Override
    public ItemStack getMachineRepresentation() {
        return displayStack;
    }

    public void setMachineRepresentation(final ItemStack stack) {
        displayStack = stack;
    }

    public IGrid getGrid() throws GridAccessException {
        if (node != null) {
            final IGrid grid = node.getGrid();

            if (grid != null) {
                return grid;
            }
        }

        throw new GridAccessException();
    }

    public IGridNode getNode() {
        if (node == null && FMLCommonHandler.instance().getEffectiveSide().isServer() && ready) {
            node = AEApi.instance().createGridNode(this);
            readFromNBT(data);
            node.updateState();
        }
        return node;
    }

    public void invalidate() {
        ready = false;
        if (node != null) {
            node.destroy();
            node = null;
        }
    }

    public void onReady() {
        ready = true;
        getNode();
    }

    public boolean isActive() {
        return node != null && node.isActive();
    }

    public boolean isPowered() {
        try {
            return GridUtils.getEnergy(this).isNetworkPowered();
        } catch (final GridAccessException e) {
            CommonUtils.debugLog("AEProxy:isPowered: Error accessing grid:", e);
            return false;
        }
    }

    public boolean isReady() {
        return ready;
    }

    public void onChunkUnload() {
        invalidate();
    }

    public void readFromNBT(final NBTTagCompound tag) {
        data = tag;
        if(node != null && data != null) { // NOPMD
            node.loadFromNBT(nbtName, data);
            data = null;
        } else if(node != null && owner != null) {
            node.setPlayerID(AEApi.instance().registries().players().getID(owner));
            owner = null;
        }
    }

    public void setOwner(final EntityPlayer player) {
        owner = player;
    }

    public void validate() {
        if (proxy instanceof EqETileBase) {
            EventHandler.addInit((EqETileBase) proxy);
        }
    }

    public void writeToNBT(final NBTTagCompound tag) {
        if (node != null) {
            node.saveToNBT(nbtName, tag);
        }
    }

    public boolean meetsChannelRequirements() {
        if (node == null) {
            return false;
        }

        return node.meetsChannelRequirements();
    }

}
