package com.mordenkainen.equivalentenergistics.tiles;

import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

import com.mordenkainen.equivalentenergistics.EquivalentEnergistics;
import com.mordenkainen.equivalentenergistics.config.ConfigManager;
import com.mordenkainen.equivalentenergistics.util.CrystalCraftingPattern;
import com.mordenkainen.equivalentenergistics.util.EECraftingPattern;
import com.mordenkainen.equivalentenergistics.util.EMCCraftingPattern;
import com.mordenkainen.equivalentenergistics.util.EMCUtils;
import com.pahimar.ee3.util.ItemHelper;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
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
import appeng.api.networking.security.MachineSource;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.util.AECableType;
import appeng.api.util.DimensionalCoord;
import appeng.me.GridAccessException;
import appeng.tile.TileEvent;
import appeng.tile.events.TileEventType;
import appeng.tile.grid.AENetworkTile;
import appeng.tile.inventory.InvOperation;
import appeng.util.item.AEItemStack;

public class TileEMCCrafter extends AENetworkTile implements ICraftingProvider {
	public static Set<TileEMCCrafter> crafterTiles = new LinkedHashSet<TileEMCCrafter>();

	private MachineSource mySource;
	private boolean isActive, isCrafting, stalePatterns = true;
	private ItemStack currentTome, outputStack;
	private int craftTickCounter, staleCounter;
	private float currentEMC;
	
	public TileEMCCrafter() {
		mySource = new MachineSource(this);
		if(FMLCommonHandler.instance().getEffectiveSide().isServer()) {
			this.gridProxy.setIdlePowerUsage(ConfigManager.crafterIdlePower);
			this.gridProxy.setFlags(GridFlags.REQUIRE_CHANNEL);
		}
	}
	
	@Override
	public void validate() {
		super.validate();
		if(!crafterTiles.contains(this)) {
			crafterTiles.add(this);
		}
	}
	
	@Override
	public void invalidate() {
		super.invalidate();
		crafterTiles.remove(this);
	}
	
	@Override
	public void onChunkUnload() {
		super.onChunkUnload();
		crafterTiles.remove(this);
	}
	
	@Override
	public boolean pushPattern(final ICraftingPatternDetails patternDetails, final InventoryCrafting table) {
		if((!isCrafting) && (patternDetails instanceof EECraftingPattern)) {
			isCrafting = true;
			craftTickCounter = staleCounter = 0;
			outputStack = ((EECraftingPattern)patternDetails).getOutputs()[0].getItemStack();
			currentEMC += ((EECraftingPattern)patternDetails).inputEMC - ((EECraftingPattern)patternDetails).outputEMC;
			return true;
		}
		return false;
	}

	@Override
	public boolean isBusy()
	{
		return isCrafting;
	}

	@Override
	public void provideCrafting(ICraftingProviderHelper craftingTracker) {
		if(EMCUtils.getInstance().isValidItem(currentTome)) {
			ArrayList<EMCCraftingPattern> bookPatterns = getPatterns();

			for(EMCCraftingPattern pattern : bookPatterns) {
				if(pattern.valid) {
					craftingTracker.addCraftingOption(this, pattern);
				}
			}
		}
		craftingTracker.addCraftingOption(this, new CrystalCraftingPattern(1));
		craftingTracker.addCraftingOption(this, new CrystalCraftingPattern(2));
	}
	
	private ArrayList<EMCCraftingPattern> getPatterns() {
		ArrayList<EMCCraftingPattern> patterns = new ArrayList<EMCCraftingPattern>();
		ArrayList<ItemStack> transmutations = EMCUtils.getInstance().getTransmutations(this);
		for(ItemStack curItem : transmutations) {
			patterns.add(new EMCCraftingPattern(curItem));
		}
		return patterns;
	}

	public boolean isActive() {
		if(!worldObj.isRemote) {
			if((gridProxy != null) && (gridProxy.getNode() != null)) {
				isActive = gridProxy.getNode().isActive();
			}
		}

		return isActive;
	}
	
	public boolean checkPermissions(final EntityPlayer player) {
		try	{
			ISecurityGrid sGrid = this.gridProxy.getSecurity();

			return((sGrid.hasPermission(player, SecurityPermissions.INJECT)) && (sGrid.hasPermission(player, SecurityPermissions.EXTRACT)));
		} catch(GridAccessException e) {
			return true;
		}
	}

	@Override
	protected ItemStack getItemFromTile(final Object obj) {
		return new ItemStack(Item.getItemFromBlock(EquivalentEnergistics.blockEMCCondenser));
	}
	
	public void onBreak() {
		gridProxy.invalidate();
	}
	
	public void setOwner(final EntityPlayer player) {
		gridProxy.setOwner(player);
	}

	public ItemStack getCurrentTome() {
		return currentTome;
	}

	public void setCurrentTome(ItemStack heldItem) {
		currentTome = heldItem;
		stalePatterns = true;
	}

	public void playerKnowledgeChange(UUID playerUUID) {
		if(currentTome != null) {
			UUID tomeUUID = EMCUtils.getInstance().getTomeUUID(currentTome);
			if(tomeUUID.equals(playerUUID)) {
				stalePatterns = true;
			}
		}
	}
	
	@TileEvent(TileEventType.TICK)
	public void onTick() {
		if(worldObj.isRemote) {
			if(isCrafting && craftTickCounter < (outputStack.getItem() == EquivalentEnergistics.itemEMCCrystal ? -1 : ConfigManager.craftingTime)) {
				craftTickCounter++;
			}
			return;
		}
		
		if(!this.isActive()) {
			return;
		}
		
		try {
			float crystalEMC = EMCUtils.getInstance().getCrystalEMC();
			if(currentEMC >= crystalEMC) {
				int numCrystals = (int)Math.floor(currentEMC/crystalEMC);
				IStorageGrid storageGrid = gridProxy.getStorage();
				IAEItemStack crystals = AEApi.instance().storage().createItemStack(new ItemStack(EquivalentEnergistics.itemEMCCrystal, numCrystals));
	
				IAEItemStack rejected = storageGrid.getItemInventory().injectItems(crystals, Actionable.SIMULATE, mySource);
	
				if(rejected == null || rejected.getStackSize() == 0) {
					storageGrid.getItemInventory().injectItems(crystals, Actionable.MODULATE, mySource);
					currentEMC -= crystalEMC * numCrystals;
				}
			}
			
			if(stalePatterns && gridProxy.isReady()) {
				if(staleCounter > 9) {
					gridProxy.getGrid().postEvent(new MENetworkCraftingPatternChange(this, getActionableNode()));
					stalePatterns = false;
				} else {
					staleCounter++;
				}
			}
		} catch(GridAccessException e) {}
		
		if(isCrafting) {
			craftingTick();
			staleCounter = 0;
		}
	}

	private void craftingTick() {
		if(outputStack == null) {
			isCrafting = false;
		}
		
		try	{
			if(this.craftTickCounter >= (outputStack.getItem() == EquivalentEnergistics.itemEMCCrystal ? -1 : ConfigManager.craftingTime)) {
				IStorageGrid storageGrid = gridProxy.getStorage();
	
				IAEItemStack rejected = storageGrid.getItemInventory().injectItems(AEApi.instance().storage().createItemStack(outputStack), Actionable.SIMULATE, mySource );
	
				if(rejected == null || rejected.getStackSize() == 0) {
					storageGrid.getItemInventory().injectItems(AEApi.instance().storage().createItemStack(outputStack), Actionable.MODULATE, mySource);
	
					this.isCrafting = false;
					this.outputStack = null;
				}
			} else {
				IEnergyGrid eGrid = gridProxy.getEnergy();
				double powerExtracted = eGrid.extractAEPower(ConfigManager.crafterActivePower, Actionable.SIMULATE, PowerMultiplier.CONFIG);
	
				if(powerExtracted - ConfigManager.crafterActivePower >= 0.0D) {
					eGrid.extractAEPower(ConfigManager.crafterActivePower, Actionable.MODULATE, PowerMultiplier.CONFIG);
					craftTickCounter++ ;
				}
			}
		} catch(GridAccessException e) {}
	}

	public void energyValueEvent() {
		if(currentTome != null) {
			stalePatterns = true;
		}
	}
	
	@TileEvent(TileEventType.WORLD_NBT_READ)
	public void onLoadNBT(final NBTTagCompound data) {
		isCrafting = data.getBoolean("Crafting");
		currentEMC = data.getFloat("CurrentEMC");
		if(data.hasKey("Tome")) {
			currentTome = ItemStack.loadItemStackFromNBT((NBTTagCompound)data.getTag("Tome"));
		}
		if(data.hasKey("Output")) {
			outputStack = ItemStack.loadItemStackFromNBT((NBTTagCompound)data.getTag("Output"));
		}
	}
	
	@TileEvent(TileEventType.NETWORK_READ)
	@SideOnly(Side.CLIENT)
	public boolean onReceiveNetworkData(final ByteBuf stream) throws IOException {
		isActive = stream.readBoolean();
		isCrafting = stream.readBoolean();
		if(isCrafting) {
			craftTickCounter = stream.readInt();
		}
		boolean hasTome = stream.readBoolean();
		if(hasTome){
			currentTome = AEItemStack.loadItemStackFromPacket(stream).getItemStack();
		}
		return true;
	}
	
	@TileEvent(TileEventType.WORLD_NBT_WRITE)
	public void onSaveNBT(final NBTTagCompound data) {
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
	
	@TileEvent(TileEventType.NETWORK_WRITE)
	public void onSendNetworkData(final ByteBuf stream) throws IOException {
		stream.writeBoolean(isActive());
		stream.writeBoolean(isCrafting);
		if(isCrafting) {
			stream.writeInt(craftTickCounter);
		}
		stream.writeBoolean(currentTome != null);
		if(currentTome != null) {
			AEApi.instance().storage().createItemStack(currentTome).writeToPacket(stream);
		}
	}
	
	private static final long HASH_A = 0x1387D;
	private static final long HASH_C = 0x3A8F05C5;

	@Override
	public int hashCode() {
		final int xTransform = (int)((HASH_A * (xCoord ^ 0x1AFF2BAD) + HASH_C) & 0xFFFFFFFF);
		final int zTransform = (int)((HASH_A * (zCoord ^ 0x25C8B353) + HASH_C) & 0xFFFFFFFF);
		final int yTransform = (int)((HASH_A * (yCoord ^ 0x39531FCD) + HASH_C) & 0xFFFFFFFF);
		return xTransform ^ zTransform ^ yTransform;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof TileEMCCrafter)
		{
			TileEMCCrafter te = (TileEMCCrafter)obj;
			return (te.xCoord == xCoord) & te.yCoord == yCoord & te.zCoord == zCoord &
					worldObj == te.worldObj;
		}
		return false;
	}
	
	public boolean isCrafting() {
		return isCrafting;
	}
}

