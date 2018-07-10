package com.mordenkainen.equivalentenergistics.blocks.provider.tile;

import com.mordenkainen.equivalentenergistics.blocks.BlockEnum;
import com.mordenkainen.equivalentenergistics.integration.Integration;
import com.mordenkainen.equivalentenergistics.integration.ae2.EMCCraftingPattern;
import com.mordenkainen.equivalentenergistics.integration.ae2.grid.GridAccessException;
import com.mordenkainen.equivalentenergistics.integration.ae2.grid.GridUtils;
import com.mordenkainen.equivalentenergistics.integration.ae2.tiles.TileAEInv;
import com.mordenkainen.equivalentenergistics.util.CommonUtils;
import com.mordenkainen.equivalentenergistics.util.inventory.InternalInventory;

import appeng.api.networking.IGridNode;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.networking.crafting.ICraftingProvider;
import appeng.api.networking.crafting.ICraftingProviderHelper;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class TileEMCPatternProvider extends TileAEInv implements IGridTickable, ICraftingProvider {

    public TileEMCPatternProvider() {
        super(new ItemStack(Item.getItemFromBlock(BlockEnum.EMCPROVIDER.getBlock()), 1));
        internalInventory = new ProviderInventory();
    }
    
    @Override
    protected void getPacketData(final NBTTagCompound nbttagcompound) {
        super.getPacketData(nbttagcompound);
        super.writeToNBT(nbttagcompound);
    }

    @Override
    protected boolean readPacketData(final NBTTagCompound nbttagcompound) {
        super.readPacketData(nbttagcompound);
        super.readFromNBT(nbttagcompound);
        return true;
    }

    @Override
    public TickingRequest getTickingRequest(final IGridNode node) {
        return new TickingRequest(1, 20, false, true);
    }

    @Override
    public TickRateModulation tickingRequest(final IGridNode node, final int ticksSinceLast) {
        if (refreshNetworkState()) {
            markForUpdate();
        }
        return TickRateModulation.SAME;
    }

    @Override
    public boolean pushPattern(final ICraftingPatternDetails patternDetails, final InventoryCrafting table) {
        if(patternDetails instanceof EMCCraftingPattern) {
            final EMCCraftingPattern pattern = (EMCCraftingPattern) patternDetails;
            try {
                return GridUtils.getEMCCrafting(getProxy()).addJob(pattern.getOutputs()[0].getItemStack(), pattern.inputEMC, pattern.outputEMC);
            } catch (GridAccessException e) {
                CommonUtils.debugLog("pushPattern: Error accessing grid:", e);
            }
        }
        return false;
    }

    @Override
    public boolean isBusy() {
        try {
            return GridUtils.getEMCCrafting(getProxy()).allCraftersBusy();
        } catch (GridAccessException e) {
            return true;
        }
    }

    @Override
    public void provideCrafting(final ICraftingProviderHelper craftingTracker) {
        GridUtils.addPatterns(getProxy(), this, craftingTracker);
    }
    
    public boolean canPlayerInteract(final EntityPlayer player) {
        return checkPermissions(player);
    }    
    
    protected class ProviderInventory extends InternalInventory {

        ProviderInventory() {
            super("EMCProviderInventory", 8, 1);
        }

        @Override
        public boolean isItemValidForSlot(final int slotId, final ItemStack itemStack) {
            return Integration.emcHandler.isValidTome(itemStack);
        }
        
    }

    public boolean addTome(final ItemStack copy) {
        for(int i = 0; i < internalInventory.getSizeInventory(); i++) {
            if(internalInventory.getStackInSlot(i) == null) {
                internalInventory.setInventorySlotContents(i, copy);
                GridUtils.updatePatterns(getProxy());
                markForUpdate();
                return true;
            }
        }
        
        return false;
    }
    
}
