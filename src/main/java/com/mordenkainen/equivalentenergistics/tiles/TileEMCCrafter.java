package com.mordenkainen.equivalentenergistics.tiles;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import com.mordenkainen.equivalentenergistics.blocks.BlockEMCCrafter;
import com.mordenkainen.equivalentenergistics.integration.Integration;
import com.mordenkainen.equivalentenergistics.integration.ae2.EMCCraftingPattern;
import com.mordenkainen.equivalentenergistics.integration.ae2.grid.GridAccessException;
import com.mordenkainen.equivalentenergistics.integration.ae2.grid.GridUtils;
import com.mordenkainen.equivalentenergistics.integration.ae2.tiles.TileAEBase;
import com.mordenkainen.equivalentenergistics.integration.waila.IWailaNBTProvider;
import com.mordenkainen.equivalentenergistics.registries.BlockEnum;
import com.mordenkainen.equivalentenergistics.registries.ItemEnum;
import com.mordenkainen.equivalentenergistics.util.CommonUtils;
import com.mordenkainen.equivalentenergistics.util.DimensionalLocation;
import com.mordenkainen.equivalentenergistics.util.IDropItems;

import appeng.api.AEApi;
import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.config.SecurityPermissions;
import appeng.api.networking.GridFlags;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.networking.crafting.ICraftingProvider;
import appeng.api.networking.crafting.ICraftingProviderHelper;
import appeng.api.networking.energy.IEnergyGrid;
import appeng.api.networking.events.MENetworkCraftingPatternChange;
import appeng.api.networking.security.ISecurityGrid;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.storage.data.IAEItemStack;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class TileEMCCrafter extends TileAEBase implements ICraftingProvider, IWailaNBTProvider, IDropItems {
    public static List<DimensionalLocation> crafterTiles = new ArrayList<DimensionalLocation>();

    private boolean isCrafting, sentEvent, stalePatterns = true;
    private ItemStack currentTome, outputStack;
    private int craftTickCounter;
    public float currentEMC;
    private List<EMCCraftingPattern> bookPatterns = new ArrayList<EMCCraftingPattern>();
    public ItemStack displayStack;

    public TileEMCCrafter() {
        super(new ItemStack(Item.getItemFromBlock(BlockEnum.EMCCRAFTER.getBlock())));
        gridProxy.setIdlePowerUsage(BlockEMCCrafter.idlePower);
        gridProxy.setFlags(GridFlags.REQUIRE_CHANNEL);
    }

    // IWailaNBTProvider Overrides
    // ------------------------
    @Override
    public NBTTagCompound getWailaTag(final NBTTagCompound tag) {
        tag.setFloat("currentEMC", currentEMC);
        if (currentTome != null) {
            tag.setString("owner", Integration.emcHandler.getTomeOwner(currentTome));
        }
        return tag;
    }
    // ------------------------

    // TileEntity Overrides
    // ------------------------
    @Override
    public void validate() {
        super.validate();
        if (!crafterTiles.contains(new DimensionalLocation(xCoord, yCoord, zCoord, worldObj))) {
            crafterTiles.add(new DimensionalLocation(xCoord, yCoord, zCoord, worldObj));
        }
    }

    @Override
    public void invalidate() {
        super.invalidate();
        crafterTiles.remove(new DimensionalLocation(xCoord, yCoord, zCoord, worldObj));
    }

    @Override
    public void onChunkUnload() {
        super.onChunkUnload();
        crafterTiles.remove(new DimensionalLocation(xCoord, yCoord, zCoord, worldObj));
    }

    @Override
    public Packet getDescriptionPacket() {
        final NBTTagCompound nbttagcompound = new NBTTagCompound();
        if (displayStack != null) {
            final NBTTagCompound stackTags = new NBTTagCompound();
            displayStack.writeToNBT(stackTags);
            nbttagcompound.setTag("displayStack", stackTags);
        }
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, -999, nbttagcompound);
    }

    @Override
    public void onDataPacket(final NetworkManager net, final S35PacketUpdateTileEntity pkt) {
        final NBTTagCompound tag = pkt.func_148857_g();
        if (tag.hasKey("displayStack")) {
            displayStack = ItemStack.loadItemStackFromNBT((NBTTagCompound) tag.getTag("displayStack"));
        } else {
            displayStack = null;
        }
    }

    @Override
    public void updateEntity() {
        if (worldObj.isRemote || !isActive()) {
            return;
        }

        injectEMC();

        if (!stalePatterns) {
            sentEvent = true;
        }

        try {
            if (stalePatterns && !sentEvent && gridProxy.isReady()) {
                gridProxy.getGrid().postEvent(new MENetworkCraftingPatternChange(this, getActionableNode()));
                sentEvent = true;
            }
        } catch (final GridAccessException e) {
            CommonUtils.debugLog("TileEMCCrafter:updateEntity: Error accessing grid:", e);
        }

        if (isCrafting) {
            craftingTick();
        }
    }

    @Override
    public void readFromNBT(final NBTTagCompound data) {
        super.readFromNBT(data);
        isCrafting = data.getBoolean("Crafting");
        currentEMC = data.getFloat("CurrentEMC");
        if (data.hasKey("Tome")) {
            currentTome = ItemStack.loadItemStackFromNBT((NBTTagCompound) data.getTag("Tome"));
        } else {
            currentTome = null;
        }
        if (data.hasKey("Output")) {
            outputStack = ItemStack.loadItemStackFromNBT((NBTTagCompound) data.getTag("Output"));
        } else {
            outputStack = null;
        }
        if (isCrafting) {
            displayStack = outputStack.copy();
            displayStack.stackSize = 1;
        } else {
            displayStack = currentTome;
        }
    }

    @Override
    public void writeToNBT(final NBTTagCompound data) {
        super.writeToNBT(data);
        data.setBoolean("Crafting", isCrafting);
        data.setFloat("CurrentEMC", currentEMC);
        if (currentTome != null) {
            final NBTTagCompound tome = new NBTTagCompound();
            currentTome.writeToNBT(tome);
            data.setTag("Tome", tome);
        }
        if (outputStack != null) {
            final NBTTagCompound output = new NBTTagCompound();
            outputStack.writeToNBT(output);
            data.setTag("Output", output);
        }
    }
    // ------------------------

    // ICraftingMedium Overrides
    // ------------------------
    @Override
    public boolean pushPattern(final ICraftingPatternDetails patternDetails, final InventoryCrafting table) {
        if (!isCrafting && patternDetails instanceof EMCCraftingPattern) {
            isCrafting = true;
            craftTickCounter = 0;
            outputStack = patternDetails.getOutputs()[0].getItemStack();
            currentEMC += ((EMCCraftingPattern) patternDetails).inputEMC - ((EMCCraftingPattern) patternDetails).outputEMC;
            setDisplayStack(outputStack);
            return true;
        }
        return false;
    }

    @Override
    public boolean isBusy() {
        return isCrafting;
    }
    // ------------------------

    // ICraftingProvider Overrides
    // ------------------------
    @Override
    public void provideCrafting(final ICraftingProviderHelper craftingTracker) {
        if (Integration.emcHandler.isValidTome(currentTome)) {
            if (stalePatterns) {
                bookPatterns = getPatterns();
                stalePatterns = false;
            }
            for (final EMCCraftingPattern pattern : bookPatterns) {
                if (pattern.valid) {
                    craftingTracker.addCraftingOption(this, pattern);
                }
            }
        }
        craftingTracker.addCraftingOption(this, EMCCraftingPattern.get(ItemEnum.EMCCRYSTAL.getDamagedStack(0)));
        craftingTracker.addCraftingOption(this, EMCCraftingPattern.get(ItemEnum.EMCCRYSTAL.getDamagedStack(1)));
        craftingTracker.addCraftingOption(this, EMCCraftingPattern.get(ItemEnum.EMCCRYSTAL.getDamagedStack(2)));
        craftingTracker.addCraftingOption(this, EMCCraftingPattern.get(ItemEnum.EMCCRYSTAL.getDamagedStack(3)));
    }
    // ------------------------

    public ItemStack getCurrentTome() {
        return currentTome;
    }

    public void setCurrentTome(final ItemStack heldItem) {
        currentTome = heldItem;
        stalePatterns = true;
        sentEvent = false;
        setDisplayStack(null);
    }

    public void playerKnowledgeChange(final UUID playerUUID) {
        if (currentTome != null) {
            final UUID tomeUUID = Integration.emcHandler.getTomeUUID(currentTome);
            if (tomeUUID.equals(playerUUID)) {
                stalePatterns = true;
                sentEvent = false;
            }
        }
    }

    private List<EMCCraftingPattern> getPatterns() {
        final ArrayList<EMCCraftingPattern> patterns = new ArrayList<EMCCraftingPattern>();
        final List<ItemStack> transmutations = Integration.emcHandler.getTransmutations(this);
        for (final ItemStack curItem : transmutations) {
            final EMCCraftingPattern pattern = EMCCraftingPattern.get(curItem);
            if (pattern != null) {
                patterns.add(pattern);
            }
        }
        return patterns;
    }

    public void energyValueEvent() {
        if (currentTome != null) {
            stalePatterns = true;
            sentEvent = false;
        }
    }

    private void craftingTick() {
        if (outputStack == null) {
            isCrafting = false;
            setDisplayStack(null);
        }

        try {
            if (craftTickCounter >= (outputStack.getItem() == ItemEnum.EMCCRYSTAL.getItem() ? -1 : BlockEMCCrafter.craftingTime)) {
                final IStorageGrid storageGrid = GridUtils.getStorage(getProxy());

                final IAEItemStack rejected = storageGrid.getItemInventory().injectItems(AEApi.instance().storage().createItemStack(outputStack), Actionable.SIMULATE, mySource);

                if (rejected == null || rejected.getStackSize() == 0) {
                    storageGrid.getItemInventory().injectItems(AEApi.instance().storage().createItemStack(outputStack), Actionable.MODULATE, mySource);

                    isCrafting = false;
                    outputStack = null;
                    setDisplayStack(null);
                }
            } else {
                if (BlockEMCCrafter.activePower <= 0) {
                    craftTickCounter++;
                    return;
                }
                final IEnergyGrid eGrid = GridUtils.getEnergy(getProxy());
                final double powerExtracted = eGrid.extractAEPower(BlockEMCCrafter.activePower, Actionable.SIMULATE, PowerMultiplier.CONFIG);

                if (powerExtracted - BlockEMCCrafter.activePower >= 0.0D) {
                    eGrid.extractAEPower(BlockEMCCrafter.activePower, Actionable.MODULATE, PowerMultiplier.CONFIG);
                    craftTickCounter++;
                }
            }
        } catch (final GridAccessException e) {
            CommonUtils.debugLog("TileEMCCrafter:craftingTick: Error accessing grid:", e);
        }
    }

    private void setDisplayStack(final ItemStack stack) {
        if (stack == null) {
            displayStack = currentTome;
        } else {
            displayStack = stack.copy();
            displayStack.stackSize = 1;
        }
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    public boolean checkPermissions(final EntityPlayer player) {
        try {
            final ISecurityGrid sGrid = GridUtils.getSecurity(getProxy());

            return sGrid.hasPermission(player, SecurityPermissions.INJECT) && sGrid.hasPermission(player, SecurityPermissions.EXTRACT);
        } catch (final GridAccessException e) {
            CommonUtils.debugLog("TileEMCCrafter:checkPermissions: Error accessing grid:", e);
        }
        return true;
    }

    public boolean canPlayerInteract(final EntityPlayer player) {
        return checkPermissions(player) && !isCrafting;
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

    public static final void postKnowledgeChange(final UUID playerUUID) {
        if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
            final Iterator<DimensionalLocation> iter = TileEMCCrafter.crafterTiles.iterator();
            while (iter.hasNext()) {
                final DimensionalLocation currentLoc = iter.next();
                final TileEntity crafter = currentLoc.getTE();
                if (crafter instanceof TileEMCCrafter) {
                    ((TileEMCCrafter) crafter).playerKnowledgeChange(playerUUID);
                } else {
                    iter.remove();
                }
            }
        }
    }

    public static final void postEnergyValueChange() {
        if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
            EMCCraftingPattern.relearnPatterns();
            final Iterator<DimensionalLocation> iter = TileEMCCrafter.crafterTiles.iterator();
            while (iter.hasNext()) {
                final DimensionalLocation currentLoc = iter.next();
                final TileEntity crafter = currentLoc.getTE();
                if (crafter instanceof TileEMCCrafter) {
                    ((TileEMCCrafter) crafter).energyValueEvent();
                } else {
                    iter.remove();
                }
            }
        }
    }

	@Override
	public void getDrops(World world, int x, int y, int z, List<ItemStack> drops) {
		if (currentTome != null) {
			drops.add(currentTome);
		}
		
	}
}
