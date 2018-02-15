package com.mordenkainen.equivalentenergistics.integration.ae2.tiles;

import com.mordenkainen.equivalentenergistics.blocks.base.tile.EqETileBase;
import com.mordenkainen.equivalentenergistics.integration.ae2.grid.AEProxy;
import com.mordenkainen.equivalentenergistics.integration.ae2.grid.GridAccessException;
import com.mordenkainen.equivalentenergistics.integration.ae2.grid.GridUtils;
import com.mordenkainen.equivalentenergistics.integration.ae2.grid.IAEProxyHost;
import com.mordenkainen.equivalentenergistics.util.CommonUtils;

import appeng.api.config.SecurityPermissions;
import appeng.api.networking.security.ISecurityGrid;
import appeng.api.networking.security.MachineSource;
import appeng.api.util.DimensionalCoord;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.FMLCommonHandler;

public abstract class TileAEBase extends EqETileBase implements IAEProxyHost {

    private final static String POWERED_TAG = "powered";
    private final static String ACTIVE_TAG = "active";

    protected final AEProxy gridProxy;
    protected MachineSource mySource;
    protected boolean active;
    protected boolean powered;

    public TileAEBase(final ItemStack repItem) {
        super();
        mySource = new MachineSource(this);
        gridProxy = new AEProxy(this, "node0", repItem, true);
    }

    @Override
    public void onChunkUnload() {
        super.onChunkUnload();
        IAEProxyHost.super.onChunkUnload();
    }

    @Override
    public void invalidate() {
        super.invalidate();
        IAEProxyHost.super.invalidate();
    }

    @Override
    public void validate() {
        super.validate();
        IAEProxyHost.super.validate();
    }

    @Override
    public void onReady() {
        IAEProxyHost.super.onReady();
    }

    @Override
    public void readFromNBT(final NBTTagCompound data) {
        super.readFromNBT(data);
        IAEProxyHost.super.readFromNBT(data);
    }

    @Override
    public NBTTagCompound writeToNBT(final NBTTagCompound data) {
        super.writeToNBT(data);
        IAEProxyHost.super.writeToNBT(data);
        return data;
    }

    @Override
    public AEProxy getProxy() {
        return gridProxy;
    }

    @Override
    public DimensionalCoord getLocation() {
        return new DimensionalCoord(this);
    }

    @Override
    public void securityBreak() {
        CommonUtils.destroyAndDrop(getWorld(), pos);
    }

    protected boolean checkPermissions(final EntityPlayer player) {
        try {
            final ISecurityGrid sGrid = GridUtils.getSecurity(getProxy());

            return sGrid.hasPermission(player, SecurityPermissions.INJECT) && sGrid.hasPermission(player, SecurityPermissions.EXTRACT) && sGrid.hasPermission(player, SecurityPermissions.BUILD);
        } catch (final GridAccessException e) {
            CommonUtils.debugLog("TileAEBase:checkPermissions: Error accessing grid:", e);
        }
        return true;
    }

    @Override
    public boolean isActive() {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            return active;
        } else {
            return gridProxy.isReady() && gridProxy.isActive();
        }
    }

    @Override
    public boolean isPowered() {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            return powered;
        } else {
            return gridProxy.isReady() && gridProxy.isPowered();
        }
    }

    @Override
    protected void getPacketData(final NBTTagCompound nbttagcompound) {
        nbttagcompound.setBoolean(POWERED_TAG, isPowered());
        nbttagcompound.setBoolean(ACTIVE_TAG, isActive());
    }

    @Override
    protected boolean readPacketData(final NBTTagCompound nbttagcompound) {
        boolean flag = false;
        boolean newState = nbttagcompound.getBoolean(POWERED_TAG);
        if(newState != powered) {
            powered = newState;
            flag = true;
        }
        newState = nbttagcompound.getBoolean(ACTIVE_TAG);
        if(newState != active) {
            active = newState;
            flag = true;
        }
        return flag;
    }

    protected boolean refreshNetworkState() {
        boolean flag = false;
        boolean newState = isPowered();
        if(newState != powered) {
            powered = newState;
            flag = true;
        }
        newState = isActive();
        if(newState != active) {
            active = newState;
            flag = true;
        }
        return flag;
    }

}
