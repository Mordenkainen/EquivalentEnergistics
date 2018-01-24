package com.mordenkainen.equivalentenergistics.blocks.crafter.tiles;

import java.util.ArrayList;
import java.util.List;

import com.mordenkainen.equivalentenergistics.blocks.BlockEnum;
import com.mordenkainen.equivalentenergistics.blocks.crafter.BlockEMCCrafter;
import com.mordenkainen.equivalentenergistics.integration.Integration;
import com.mordenkainen.equivalentenergistics.integration.ae2.EMCCraftingPattern;
import com.mordenkainen.equivalentenergistics.integration.ae2.cache.crafting.ITransProvider;
import com.mordenkainen.equivalentenergistics.integration.ae2.grid.GridUtils;
import com.mordenkainen.equivalentenergistics.integration.ae2.tiles.TileAEBase;
import com.mordenkainen.equivalentenergistics.integration.waila.IWailaNBTProvider;
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
import net.minecraft.world.World;

public abstract class TileEMCCrafterBase extends TileAEBase implements ICraftingProvider, IGridTickable, IWailaNBTProvider, IDropItems, ICraftingMonitor, ITransProvider {

    private static final String TOME_TAG = "Tome";
    private static final String OWNER_TAG = "Owner";
    private static final String EMC_TAG = "CurrentEMC";
    private static final String CRAFTING_TAG = "Crafting";
    private static final String DISPLAY_TAG = "DisplayStacks";
    private static final String STACK_TAG = "Stack";
    private static final String ERROR_TAG = "Errored";
    
    private ItemStack transmutationItem;
    private double currentEMC;
    private List<ItemStack> displayStacks = new ArrayList<ItemStack>();
    private final CraftingManager manager;
    private boolean crafting;
    private boolean errored;
    private boolean doDrops = true;
    
    public final int maxJobs;

    public TileEMCCrafterBase(final int jobs, final double time, final int meta) {
        super(new ItemStack(Item.getItemFromBlock(BlockEnum.EMCCRAFTER.getBlock()), 1, meta));
        gridProxy.setIdlePowerUsage(BlockEMCCrafter.idlePower);
        gridProxy.setFlags(GridFlags.REQUIRE_CHANNEL);
        maxJobs = jobs;
        manager = new CraftingManager(time, maxJobs, this, getProxy(), mySource);
    }

    @Override
    public NBTTagCompound getWailaTag(final NBTTagCompound tag) {
        if (currentEMC > 0) {
            tag.setDouble(EMC_TAG, currentEMC);
        }
        if (transmutationItem != null) {
            tag.setString(OWNER_TAG, Integration.emcHandler.getTomeOwner(transmutationItem));
        }
        return tag;
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
        
        if (!isActive()) {
            return TickRateModulation.IDLE;
        }
        
        injectEMC();

        if (manager.isCrafting()) {
            final boolean newState = !manager.craftingTick();
            if (newState != errored) {
                errored = newState;
                markForUpdate();
            }
            return TickRateModulation.URGENT;
        }

        return TickRateModulation.IDLE;
    }

    @Override
    public void readFromNBT(final NBTTagCompound data) {
        super.readFromNBT(data);
        currentEMC = data.getDouble(EMC_TAG);
        transmutationItem = data.hasKey(TOME_TAG) ? ItemStack.loadItemStackFromNBT((NBTTagCompound) data.getTag(TOME_TAG)) : null;
        manager.readFromNBT(data);
        displayStacks = manager.getCurrentJobs();
        markForUpdate();
    }

    @Override
    public void writeToNBT(final NBTTagCompound data) {
        super.writeToNBT(data);
        if (currentEMC > 0) {
            data.setDouble(EMC_TAG, currentEMC);
        }
        if (transmutationItem != null) {
            data.setTag(TOME_TAG, transmutationItem.writeToNBT(new NBTTagCompound()));
        }
        manager.writeToNBT(data);
    }

    @Override
    public boolean pushPattern(final ICraftingPatternDetails patternDetails, final InventoryCrafting table) {
        if (isActive() && patternDetails instanceof EMCCraftingPattern && manager.addJob(patternDetails.getOutputs()[0].getItemStack(), ((EMCCraftingPattern) patternDetails).outputEMC, BlockEMCCrafter.powerPerEMC)) {
            currentEMC += ((EMCCraftingPattern) patternDetails).inputEMC - ((EMCCraftingPattern) patternDetails).outputEMC;
            displayStacks = manager.getCurrentJobs();
            markForUpdate();
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
        GridUtils.addPatterns(getProxy(), this, craftingTracker);
    }

    public ItemStack getCurrentTome() {
        return transmutationItem;
    }

    public void setCurrentTome(final ItemStack heldItem) {
        transmutationItem = heldItem;
        GridUtils.updatePatterns(getProxy());
        markForUpdate();
    }

    public boolean canPlayerInteract(final EntityPlayer player) {
        return checkPermissions(player) && !manager.isCrafting();
    }

    private void injectEMC() {
        currentEMC -= GridUtils.injectEMC(getProxy(), currentEMC, Actionable.MODULATE);
    }

    @Override
    public void getDrops(final World world, final int x, final int y, final int z, final List<ItemStack> drops) {
        if (doDrops && transmutationItem != null) {
            drops.add(transmutationItem);
        }
    }
    
    @Override
    public void disableDrops() {
        doDrops = false;
    }

    @Override
    protected void getPacketData(final NBTTagCompound nbttagcompound) {
        super.getPacketData(nbttagcompound);
        nbttagcompound.setBoolean(CRAFTING_TAG, manager.isCrafting());
        nbttagcompound.setBoolean(ERROR_TAG, errored);
        
        if (manager.isCrafting()) {
            final NBTTagCompound displayTags = new NBTTagCompound();
            for (int i = 0; i < displayStacks.size(); i++) {
                if (displayStacks.get(i) != null) {
                    displayTags.setTag(STACK_TAG + i, displayStacks.get(i).writeToNBT(new NBTTagCompound())); 
                }
            }
            nbttagcompound.setTag(DISPLAY_TAG, displayTags);
        }
        
        if (transmutationItem != null) {
            nbttagcompound.setTag(TOME_TAG, transmutationItem.writeToNBT(new NBTTagCompound()));
        }
    }

    @Override
    protected boolean readPacketData(final NBTTagCompound nbttagcompound) {
        crafting = nbttagcompound.getBoolean(CRAFTING_TAG);
        errored = nbttagcompound.getBoolean(ERROR_TAG);
        
        displayStacks = new ArrayList<ItemStack>();
        if (nbttagcompound.hasKey(DISPLAY_TAG)) {
            final NBTTagCompound invList = nbttagcompound.getCompoundTag(DISPLAY_TAG);
            for (int i = 0; i < maxJobs; i++) {
                if (invList.hasKey(STACK_TAG + i)) {
                    displayStacks.add(ItemStack.loadItemStackFromNBT(invList.getCompoundTag(STACK_TAG + i)));
                } else {
                    displayStacks.add(null);
                }
            }
        }
    
        transmutationItem = nbttagcompound.hasKey(TOME_TAG) ? ItemStack.loadItemStackFromNBT(nbttagcompound.getCompoundTag(TOME_TAG)) : null;
        
        return super.readPacketData(nbttagcompound);
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
        displayStacks = manager.getCurrentJobs();
        markForUpdate();
    }
    
    public List<ItemStack> getDisplayStacks() {
        return displayStacks;
    }
    
    public boolean isCrafting() {
        return crafting;
    }
    
    public boolean isErrored() {
        return errored;
    }

}
