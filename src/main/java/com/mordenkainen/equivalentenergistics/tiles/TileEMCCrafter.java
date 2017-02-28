package com.mordenkainen.equivalentenergistics.tiles;

import java.util.ArrayList;
import java.util.List;

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
import appeng.api.networking.security.ISecurityGrid;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.storage.data.IAEItemStack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class TileEMCCrafter extends TileAEBase implements ICraftingProvider, IWailaNBTProvider, IDropItems {

    private boolean isCrafting;
    private ItemStack currentTome, outputStack;
    private int craftTickCounter;
    public float currentEMC;
    public ItemStack displayStack;

    public TileEMCCrafter() {
        super(new ItemStack(Item.getItemFromBlock(BlockEnum.EMCCRAFTER.getBlock())));
        gridProxy.setIdlePowerUsage(BlockEMCCrafter.idlePower);
        gridProxy.setFlags(GridFlags.REQUIRE_CHANNEL);
    }

    @Override
    public NBTTagCompound getWailaTag(final NBTTagCompound tag) {
        tag.setFloat("currentEMC", currentEMC);
        if (currentTome != null) {
            tag.setString("owner", Integration.emcHandler.getTomeOwner(currentTome));
        }
        return tag;
    }
    
    @Override
    public void updateEntity() {
        if (worldObj.isRemote || !isActive()) {
            return;
        }

        injectEMC();

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
        return currentTome;
    }

    public void setCurrentTome(final ItemStack heldItem) {
        currentTome = heldItem;
        setDisplayStack(null);
        try {
			GridUtils.getEMCCrafting(getProxy()).updatePatterns();
		} catch (GridAccessException e) {
			CommonUtils.debugLog("setCurrentTome: Error accessing grid:", e);
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

	@Override
	public void getDrops(final World world, final int x, final int y, final int z, final List<ItemStack> drops) {
		if (currentTome != null) {
			drops.add(currentTome);
		}
	}

	@Override
	protected void getPacketData(final NBTTagCompound nbttagcompound) {
		if (displayStack != null) {
            final NBTTagCompound stackTags = new NBTTagCompound();
            displayStack.writeToNBT(stackTags);
            nbttagcompound.setTag("displayStack", stackTags);
        }
	}

	@Override
	protected void readPacketData(final NBTTagCompound nbttagcompound) {
		displayStack = nbttagcompound.hasKey("displayStack") ? ItemStack.loadItemStackFromNBT((NBTTagCompound) nbttagcompound.getTag("displayStack")) : null;
	}

	public String getPlayerUUID() {
		return currentTome == null ? null : Integration.emcHandler.getTomeUUID(currentTome).toString();
	}

	public List<ItemStack> getTransmutations() {
		return currentTome == null ? new ArrayList<ItemStack>() : Integration.emcHandler.getTransmutations(this);
	}
}
