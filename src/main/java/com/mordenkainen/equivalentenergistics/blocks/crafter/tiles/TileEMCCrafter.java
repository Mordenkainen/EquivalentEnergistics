package com.mordenkainen.equivalentenergistics.blocks.crafter.tiles;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import com.mordenkainen.equivalentenergistics.blocks.ModBlocks;
import com.mordenkainen.equivalentenergistics.blocks.base.tile.TileAEBase;
import com.mordenkainen.equivalentenergistics.blocks.crafter.CraftingManager;
import com.mordenkainen.equivalentenergistics.core.config.EqEConfig;
import com.mordenkainen.equivalentenergistics.integration.ae2.EMCCraftingPattern;
import com.mordenkainen.equivalentenergistics.integration.ae2.cache.crafting.ITransProvider;
import com.mordenkainen.equivalentenergistics.integration.ae2.grid.GridUtils;
import com.mordenkainen.equivalentenergistics.items.ModItems;
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
import moze_intel.projecte.api.ProjectEAPI;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TileEMCCrafter extends TileAEBase implements IGridTickable, IDropItems, ITransProvider, ICraftingProvider, ICraftingMonitor {

    private static final String TOME_TAG = "Tome";
    private static final String EMC_TAG = "CurrentEMC";
    private static final String CRAFTING_TAG = "Crafting";
    private static final String DISPLAY_TAG = "DisplayStacks";
    private static final String STACK_TAG = "Stack";
    private static final String ERROR_TAG = "Errored";

    private NonNullList<ItemStack> displayStacks;
    private ItemStack transmutationItem = ItemStack.EMPTY;
    private boolean doDrops = true;
    private float currentEMC;
    private final CraftingManager manager;
    private boolean crafting;
    private boolean errored;


    public TileEMCCrafter() {
        this(1, EqEConfig.emcAssembler.craftingTime, 0);
    }

    public TileEMCCrafter(final int jobs, final double time, final int meta) {
        super(new ItemStack(Item.getItemFromBlock(ModBlocks.CRAFTER), 1, meta));
        gridProxy.setIdlePowerUsage(EqEConfig.emcAssembler.idlePower);
        gridProxy.setFlags(GridFlags.REQUIRE_CHANNEL);
        displayStacks = NonNullList.withSize(jobs, ItemStack.EMPTY);
        manager = new CraftingManager(time, jobs, this, getProxy(), mySource);
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

        return TickRateModulation.SAME;
    }

    public NonNullList<ItemStack> getDisplayStacks() {
        return displayStacks;
    }

    public boolean canPlayerInteract(final EntityPlayer player) {
        return checkPermissions(player) && !manager.isCrafting();
    }

    public ItemStack getCurrentTome() {
        return transmutationItem;
    }

    public void setCurrentTome(final ItemStack heldItem) {
        transmutationItem = heldItem;
        GridUtils.updatePatterns(getProxy());
        markForUpdate();
    }

    @Override
    public void getDrops(final World world, final BlockPos pos, final List<ItemStack> drops) {
        if (doDrops && !transmutationItem.isEmpty()) {
            drops.add(transmutationItem);
        }
    }

    @Override
    public void disableDrops() {
        doDrops = false;
    }

    @Override
    public String getPlayerUUID() {
        return transmutationItem.isEmpty() ? null : UUID.fromString(transmutationItem.getTagCompound().getString("OwnerUUID")).toString();
    }

    @Override
    public List<ItemStack> getTransmutations() {
        List<ItemStack> transmutations = new ArrayList<ItemStack>();
        if (!transmutationItem.isEmpty()) {
            transmutations = ProjectEAPI.getTransmutationProxy().getKnowledgeProviderFor(UUID.fromString(transmutationItem.getTagCompound().getString("OwnerUUID"))).getKnowledge();

            final Iterator<ItemStack> iter = transmutations.iterator();
            while (iter.hasNext()) {
                final ItemStack currentItem = iter.next();
                if (currentItem.getItem() == ModItems.CRYSTAL) {
                    iter.remove();
                }
            }
        }
        return transmutations;
    }

    @Override
    public boolean isBusy() {
        return manager.isBusy();
    }

    @Override
    public boolean pushPattern(final ICraftingPatternDetails patternDetails, final InventoryCrafting invCrafting) {
        if (isActive() && patternDetails instanceof EMCCraftingPattern && manager.addJob(patternDetails.getOutputs()[0].createItemStack(), ((EMCCraftingPattern) patternDetails).outputEMC, EqEConfig.emcAssembler.powerPerEMC)) {
            currentEMC += ((EMCCraftingPattern) patternDetails).inputEMC - ((EMCCraftingPattern) patternDetails).outputEMC;
            displayStacks = manager.getCurrentJobs();
            markForUpdate();
            return true;
        }
        return false;
    }

    @Override
    public void provideCrafting(final ICraftingProviderHelper craftingProvider) {
        GridUtils.addPatterns(getProxy(), this, craftingProvider);
    }

    @Override
    public void readFromNBT(final NBTTagCompound data) {
        super.readFromNBT(data);
        currentEMC = data.getFloat(EMC_TAG);
        transmutationItem = data.hasKey(TOME_TAG) ? new ItemStack((NBTTagCompound) data.getTag(TOME_TAG)) : ItemStack.EMPTY;
        manager.readFromNBT(data);
        displayStacks = manager.getCurrentJobs();
        markForUpdate();
    }

    @Override
    public NBTTagCompound writeToNBT(final NBTTagCompound data) {
        super.writeToNBT(data);
        if (currentEMC > 0) {
            data.setFloat(EMC_TAG, currentEMC);
        }
        if (transmutationItem != null) {
            data.setTag(TOME_TAG, transmutationItem.writeToNBT(new NBTTagCompound()));
        }
        manager.writeToNBT(data);
        return data;
    }

    private void injectEMC() {
        currentEMC -= GridUtils.injectEMC(getProxy(), currentEMC, Actionable.MODULATE);
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

        if (nbttagcompound.hasKey(DISPLAY_TAG)) {
            final NBTTagCompound invList = nbttagcompound.getCompoundTag(DISPLAY_TAG);
            for (int i = 0; i < displayStacks.size(); i++) {
                if (invList.hasKey(STACK_TAG + i)) {
                    displayStacks.set(i, new ItemStack(invList.getCompoundTag(STACK_TAG + i)));
                } else {
                    displayStacks.set(i, ItemStack.EMPTY);
                }
            }
        }

        transmutationItem = nbttagcompound.hasKey(TOME_TAG) ? new ItemStack(nbttagcompound.getCompoundTag(TOME_TAG)) : ItemStack.EMPTY;

        return super.readPacketData(nbttagcompound);
    }

    public boolean isCrafting() {
        return crafting;
    }

    public boolean isErrored() {
        return errored;
    }

    @Override
    public void craftingFinished(final ItemStack outputStack) {
        displayStacks = manager.getCurrentJobs();
        markForUpdate();
    }

}
