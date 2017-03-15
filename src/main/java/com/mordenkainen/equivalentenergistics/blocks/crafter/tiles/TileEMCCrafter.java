package com.mordenkainen.equivalentenergistics.blocks.crafter.tiles;

import java.util.ArrayList;
import java.util.List;

import com.mordenkainen.equivalentenergistics.blocks.BlockEnum;
import com.mordenkainen.equivalentenergistics.blocks.crafter.BlockEMCCrafter;
import com.mordenkainen.equivalentenergistics.integration.Integration;
import com.mordenkainen.equivalentenergistics.integration.ae2.EMCCraftingPattern;
import com.mordenkainen.equivalentenergistics.integration.ae2.cache.crafting.ITransProvider;
import com.mordenkainen.equivalentenergistics.integration.ae2.grid.GridAccessException;
import com.mordenkainen.equivalentenergistics.integration.ae2.grid.GridUtils;
import com.mordenkainen.equivalentenergistics.integration.ae2.tiles.TileAEBase;
import com.mordenkainen.equivalentenergistics.integration.waila.IWailaNBTProvider;
import com.mordenkainen.equivalentenergistics.util.CommonUtils;
import com.mordenkainen.equivalentenergistics.util.IDropItems;

import appeng.api.config.Actionable;
import appeng.api.networking.GridFlags;
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
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

public class TileEMCCrafter extends TileAEBase implements ICraftingProvider, IGridTickable, IWailaNBTProvider, IDropItems, ICraftingMonitor, ITransProvider {

    private ItemStack transmutationItem;
    public float currentEMC;
    public List<ItemStack> displayStacks = new ArrayList<ItemStack>();
    private boolean sleeping;
    private final CraftingManager manager;

    public TileEMCCrafter() {
        super(new ItemStack(Item.getItemFromBlock(BlockEnum.EMCCRAFTER.getBlock())));
        gridProxy.setIdlePowerUsage(BlockEMCCrafter.idlePower);
        gridProxy.setFlags(GridFlags.REQUIRE_CHANNEL);
        manager = new CraftingManager(BlockEMCCrafter.craftingTime, 1, this, getProxy(), mySource);
    }

    @Override
    public NBTTagCompound getWailaTag(final NBTTagCompound tag) {
        tag.setFloat("currentEMC", currentEMC);
        if (transmutationItem != null) {
            tag.setString("owner", Integration.emcHandler.getTomeOwner(transmutationItem));
        }
        return tag;
    }

    @Override
    public TickingRequest getTickingRequest(final IGridNode node) {
        return new TickingRequest(1, 20, false, true);
    }

    @Override
    public TickRateModulation tickingRequest(final IGridNode node, final int ticksSinceLast) {
        if (nodeDirty && gridProxy.isReady()) {
            gridProxy.getNode().updateState();
            nodeDirty = false;
        }
        sleeping = false;
        if (isActive()) {
            injectEMC();

            if (manager.isCrafting()) {
                manager.craftingTick();
                return TickRateModulation.URGENT;
            }
        }

        sleeping = true;
        return TickRateModulation.SLEEP;
    }

    @Override
    public void readFromNBT(final NBTTagCompound data) {
        super.readFromNBT(data);
        currentEMC = data.getFloat("CurrentEMC");
        transmutationItem = data.hasKey("Tome") ? ItemStack.loadItemStackFromNBT((NBTTagCompound) data.getTag("Tome")) : null;
        sleeping = data.getBoolean("Sleeping");
        manager.readFromNBT(data);
        if (manager.isCrafting()) {
            setDisplayStack(manager.getCurrentJobs().get(0));
        } else {
            setDisplayStack(null);
        }
    }

    @Override
    public void writeToNBT(final NBTTagCompound data) {
        super.writeToNBT(data);
        data.setFloat("CurrentEMC", currentEMC);
        if (transmutationItem != null) {
            data.setTag("Tome", transmutationItem.writeToNBT(new NBTTagCompound()));
        }
        data.setBoolean("Sleeping", sleeping);
        manager.writeToNBT(data);
    }

    @Override
    public boolean pushPattern(final ICraftingPatternDetails patternDetails, final InventoryCrafting table) {
        if (patternDetails instanceof EMCCraftingPattern && manager.addJob(patternDetails.getOutputs()[0].getItemStack())) {
            currentEMC += ((EMCCraftingPattern) patternDetails).inputEMC - ((EMCCraftingPattern) patternDetails).outputEMC;
            GridUtils.alertDevice(getProxy(), getProxy().getNode());
            setDisplayStack(manager.getCurrentJobs().get(0));
            return true;
        }
        return false;
    }

    @Override
    public boolean isBusy() {
        return manager.isBusy();
    }

    @Override
    public void provideCrafting(final ICraftingProviderHelper craftingTracker) {
        try {
            for (final EMCCraftingPattern pattern : GridUtils.getEMCCrafting(getProxy()).getPatterns()) {
                craftingTracker.addCraftingOption(this, pattern);
            }
        } catch (GridAccessException e) {
            CommonUtils.debugLog("provideCrafting: Error accessing grid:", e);
        }
    }

    public ItemStack getCurrentTome() {
        return transmutationItem;
    }

    public void setCurrentTome(final ItemStack heldItem) {
        transmutationItem = heldItem;
        setDisplayStack(null);
        try {
            GridUtils.getEMCCrafting(getProxy()).updatePatterns();
        } catch (GridAccessException e) {
            CommonUtils.debugLog("setCurrentTome: Error accessing grid:", e);
        }
    }

    private void setDisplayStack(final ItemStack stack) {
        if (stack == null) {
            displayStacks.clear();
            if (transmutationItem != null) {
                displayStacks.add(transmutationItem);
            }
        } else {
            displayStacks.clear();
            displayStacks.add(stack.copy());
            displayStacks.get(0).stackSize = 1;
        }
        if (worldObj != null) {
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }
    }

    public boolean canPlayerInteract(final EntityPlayer player) {
        return checkPermissions(player) && !manager.isCrafting();
    }

    private void injectEMC() {
        if (currentEMC > 0) {
            try {
                currentEMC -= GridUtils.getEMCStorage(getProxy()).injectEMC(currentEMC, Actionable.MODULATE);
            } catch (final GridAccessException e) {
                CommonUtils.debugLog("TileEMCCrafter:injectEMC: Error accessing grid.", e);
            }
        }
    }

    @Override
    public void getDrops(final World world, final int x, final int y, final int z, final List<ItemStack> drops) {
        if (transmutationItem != null) {
            drops.add(transmutationItem);
        }
    }

    @Override
    protected void getPacketData(final NBTTagCompound nbttagcompound) {
        if (!displayStacks.isEmpty()) {
            final NBTTagList items = new NBTTagList();
            for (int i = 0; i < displayStacks.size(); i++) {
                items.appendTag(displayStacks.get(i).writeToNBT(new NBTTagCompound()));
            }
            nbttagcompound.setTag("displayStacks", items);
        }
    }

    @Override
    protected void readPacketData(final NBTTagCompound nbttagcompound) {
        displayStacks.clear();
        if (nbttagcompound.hasKey("displayStacks")) {
            final NBTTagList invList = nbttagcompound.getTagList("displayStacks", (byte) 10);
            for (int index = 0; index < invList.tagCount(); index++) {
                displayStacks.add(ItemStack.loadItemStackFromNBT((NBTTagCompound) invList.getCompoundTagAt(index)));
            }
        }
    }

    @Override
    public String getPlayerUUID() {
        return transmutationItem == null ? null : Integration.emcHandler.getTomeUUID(transmutationItem).toString();
    }

    @Override
    public List<ItemStack> getTransmutations() {
        return transmutationItem == null ? new ArrayList<ItemStack>() : Integration.emcHandler.getTransmutations(this);
    }

    @Override
    public void craftingFinished(final ItemStack outputStack) {
        if (!manager.isCrafting()) {
            setDisplayStack(null);
        }
    }

}
