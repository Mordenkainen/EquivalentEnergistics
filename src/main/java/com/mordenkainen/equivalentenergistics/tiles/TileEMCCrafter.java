package com.mordenkainen.equivalentenergistics.tiles;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.mordenkainen.equivalentenergistics.blocks.BlockEMCCrafter;
import com.mordenkainen.equivalentenergistics.integration.ae2.grid.GridAccessException;
import com.mordenkainen.equivalentenergistics.integration.ae2.tiles.TileNetworkBase;
import com.mordenkainen.equivalentenergistics.integration.waila.IWailaNBTProvider;
import com.mordenkainen.equivalentenergistics.registries.BlockEnum;
import com.mordenkainen.equivalentenergistics.registries.ItemEnum;
import com.mordenkainen.equivalentenergistics.util.DimensionalLocation;
import com.mordenkainen.equivalentenergistics.util.EMCCraftingPattern;
import com.mordenkainen.equivalentenergistics.util.EMCUtils;

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
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;

public class TileEMCCrafter extends TileNetworkBase implements ICraftingProvider, IWailaNBTProvider {
	public static ArrayList<DimensionalLocation> crafterTiles = new ArrayList<DimensionalLocation>();
	
	private boolean isCrafting, sentEvent = false, stalePatterns = true;
	private ItemStack currentTome, outputStack;
	private int craftTickCounter;
	public float currentEMC;
	private ArrayList<EMCCraftingPattern> bookPatterns = new ArrayList<EMCCraftingPattern>();
	public ItemStack displayStack = null;
	
	public TileEMCCrafter() {
		super(new ItemStack(Item.getItemFromBlock(BlockEnum.EMCCRAFTER.getBlock())));
		gridProxy.setIdlePowerUsage(BlockEMCCrafter.idlePower);
		gridProxy.setFlags(GridFlags.REQUIRE_CHANNEL);
	}
	
	public ItemStack getCurrentTome() {
		return currentTome;
	}

	public void setCurrentTome(ItemStack heldItem) {
		currentTome = heldItem;
		stalePatterns = true;
		sentEvent = false;
		if(!isCrafting()) {
			setDisplayStack(null);
		}
	}

	public void playerKnowledgeChange(UUID playerUUID) {
		if(currentTome != null) {
			UUID tomeUUID = EMCUtils.getInstance().getTomeUUID(currentTome);
			if(tomeUUID.equals(playerUUID)) {
				stalePatterns = true;
				sentEvent = false;
			}
		}
	}
	
	private ArrayList<EMCCraftingPattern> getPatterns() {
		ArrayList<EMCCraftingPattern> patterns = new ArrayList<EMCCraftingPattern>();
		List<ItemStack> transmutations = EMCUtils.getInstance().getTransmutations(this);
		for(ItemStack curItem : transmutations) {
			patterns.add(EMCCraftingPattern.get(curItem));
		}
		return patterns;
	}
	
	public void energyValueEvent() {
		if(currentTome != null) {
			stalePatterns = true;
			sentEvent = false;
		}
	}
	
	public boolean isCrafting() {
		return isCrafting;
	}

	public ItemStack getCurrentOutput() {
		return outputStack;
	}
	
	private void craftingTick() {
		if(outputStack == null) {
			isCrafting = false;
			setDisplayStack(null);
		}
		
		try	{
			if(this.craftTickCounter >= (outputStack.getItem() == ItemEnum.EMCCRYSTAL.getItem() ? -1 : BlockEMCCrafter.craftingTime)) {
				IStorageGrid storageGrid = gridProxy.getStorage();
	
				IAEItemStack rejected = storageGrid.getItemInventory().injectItems(AEApi.instance().storage().createItemStack(outputStack), Actionable.SIMULATE, mySource);
	
				if(rejected == null || rejected.getStackSize() == 0) {
					storageGrid.getItemInventory().injectItems(AEApi.instance().storage().createItemStack(outputStack), Actionable.MODULATE, mySource);
	
					isCrafting = false;
					outputStack = null;
					setDisplayStack(null);
				}
			} else {
				IEnergyGrid eGrid = gridProxy.getEnergy();
				double powerExtracted = eGrid.extractAEPower(BlockEMCCrafter.activePower, Actionable.SIMULATE, PowerMultiplier.CONFIG);
	
				if(powerExtracted - BlockEMCCrafter.activePower >= 0.0D) {
					eGrid.extractAEPower(BlockEMCCrafter.activePower, Actionable.MODULATE, PowerMultiplier.CONFIG);
					craftTickCounter++ ;
				}
			}
		} catch(GridAccessException e) {}
	}
	
	private void setDisplayStack(ItemStack stack) {
		if(stack == null) {
			displayStack = currentTome;
		} else {
			displayStack = stack.copy();
			displayStack.stackSize = 1;
		}
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}
	
	public boolean checkPermissions(final EntityPlayer player) {
		try	{
			ISecurityGrid sGrid = gridProxy.getSecurity();

			return((sGrid.hasPermission(player, SecurityPermissions.INJECT)) && (sGrid.hasPermission(player, SecurityPermissions.EXTRACT)));
		} catch(GridAccessException e) {
			return true;
		}
	}
	
	@Override
	public NBTTagCompound getWailaTag(NBTTagCompound tag) {
		tag.setFloat("currentEMC", currentEMC);
		if(currentTome != null) {
			tag.setString("owner", EMCUtils.getInstance().getTomeOwner(currentTome));
		}
		return tag;
	}
	
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
	public boolean pushPattern(final ICraftingPatternDetails patternDetails, final InventoryCrafting table) {
		if((!isCrafting) && (patternDetails instanceof EMCCraftingPattern)) {
			isCrafting = true;
			craftTickCounter = 0;
			outputStack = ((EMCCraftingPattern)patternDetails).getOutputs()[0].getItemStack();
			currentEMC += ((EMCCraftingPattern)patternDetails).inputEMC - ((EMCCraftingPattern)patternDetails).outputEMC;
			setDisplayStack(outputStack);
			return true;
		}
		return false;
	}
	
	@Override
	public boolean isBusy()	{
		return isCrafting;
	}

	@Override
	public void provideCrafting(ICraftingProviderHelper craftingTracker) {
		if (stalePatterns) {
			if (EMCUtils.getInstance().isValidTome(currentTome)) {
				bookPatterns = getPatterns();
			}
			stalePatterns = false;
		}
		if (EMCUtils.getInstance().isValidTome(currentTome)) {
			for (EMCCraftingPattern pattern : bookPatterns) {
				if (pattern.valid) {
					craftingTracker.addCraftingOption(this, pattern);
				}
			}
		}
		craftingTracker.addCraftingOption(this, EMCCraftingPattern.get(new ItemStack(ItemEnum.EMCCRYSTAL.getItem(), 1, 1)));
		craftingTracker.addCraftingOption(this, EMCCraftingPattern.get(new ItemStack(ItemEnum.EMCCRYSTAL.getItem(), 1, 2)));
	}

	@Override
	public void updateEntity() {
		if(worldObj.isRemote ||  !isActive()) {
			return;
		}
		
		try {
			float crystalEMC = EMCUtils.getInstance().getCrystalEMC();
			if(currentEMC >= crystalEMC) {
				int numCrystals = (int)Math.floor(currentEMC / crystalEMC);
				IStorageGrid storageGrid = gridProxy.getStorage();
				IAEItemStack crystals = AEApi.instance().storage().createItemStack(new ItemStack(ItemEnum.EMCCRYSTAL.getItem(), numCrystals));
	
				IAEItemStack rejected = storageGrid.getItemInventory().injectItems(crystals, Actionable.SIMULATE, mySource);
	
				if(rejected == null || rejected.getStackSize() == 0) {
					storageGrid.getItemInventory().injectItems(crystals, Actionable.MODULATE, mySource);
					currentEMC -= crystalEMC * numCrystals;
				}
			}
			
			if(stalePatterns && !sentEvent && gridProxy.isReady()) {
				gridProxy.getGrid().postEvent(new MENetworkCraftingPatternChange(this, getActionableNode()));
				sentEvent = true;
			}
		} catch(GridAccessException e) {}
		
		if(isCrafting) {
			craftingTick();
		}
	}
	
	@Override
	public void readFromNBT(final NBTTagCompound data) {
		super.readFromNBT(data);
		isCrafting = data.getBoolean("Crafting");
		currentEMC = data.getFloat("CurrentEMC");
		if(data.hasKey("Tome")) {
			currentTome = ItemStack.loadItemStackFromNBT((NBTTagCompound)data.getTag("Tome"));
		}
		if(data.hasKey("Output")) {
			outputStack = ItemStack.loadItemStackFromNBT((NBTTagCompound)data.getTag("Output"));
		}
		if(isCrafting) {
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
		if(currentTome != null) {
			NBTTagCompound tome = new NBTTagCompound();
			currentTome.writeToNBT(tome);
			data.setTag("Tome", tome);
		}
		if(outputStack != null) {
			NBTTagCompound output = new NBTTagCompound();
			outputStack.writeToNBT(output);
			data.setTag("Output", output);
		}
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound nbttagcompound = new NBTTagCompound();
		if(displayStack != null) {
			NBTTagCompound stacktags = new NBTTagCompound();
			displayStack.writeToNBT(stacktags);
			nbttagcompound.setTag("displayStack", stacktags);
		}
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, -999, nbttagcompound);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		NBTTagCompound tag = pkt.func_148857_g();
		if(tag.hasKey("displayStack")) {
			displayStack = ItemStack.loadItemStackFromNBT((NBTTagCompound)tag.getTag("displayStack"));
		} else {
			displayStack = null;
		}
	}
}
