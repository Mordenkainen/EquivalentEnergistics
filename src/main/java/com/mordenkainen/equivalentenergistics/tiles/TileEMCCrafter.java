package com.mordenkainen.equivalentenergistics.tiles;

import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import com.mordenkainen.equivalentenergistics.EquivalentEnergistics;
import com.mordenkainen.equivalentenergistics.util.EMCCraftingPattern;
import com.pahimar.ee3.api.exchange.EnergyValueRegistryProxy;
import com.pahimar.ee3.api.knowledge.TransmutationKnowledgeRegistryProxy;
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

public class TileEMCCrafter extends AENetworkTile implements ICraftingProvider {
	private static final double IDLE_POWER = 0.0D, ACTIVE_POWER = 1.5D;
	private static final int BASE_TICKS_PER_CRAFT = 20;
	private MachineSource mySource;
	private boolean isActive, isCrafting, stalePatterns = true;
	private ItemStack currentTome, outputStack;
	private int craftTickCounter;
	private float currentEMC;
	
	public TileEMCCrafter() {
		mySource = new MachineSource(this);
		if(FMLCommonHandler.instance().getEffectiveSide().isServer()) {
			this.gridProxy.setIdlePowerUsage(TileEMCCrafter.IDLE_POWER);
			this.gridProxy.setFlags(GridFlags.REQUIRE_CHANNEL);
		}
	}
	
	@Override
	public boolean pushPattern(final ICraftingPatternDetails patternDetails, final InventoryCrafting table) {
		if((!isCrafting) && (patternDetails instanceof EMCCraftingPattern)) {
			isCrafting = true;
			craftTickCounter = 0;
			outputStack =  ((EMCCraftingPattern)patternDetails).getOutputs()[0].getItemStack();
			currentEMC += ((EMCCraftingPattern)patternDetails).inputEMC - ((EMCCraftingPattern)patternDetails).outputEMC;
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
		if(currentTome != null && ItemHelper.getOwnerUUID(currentTome) != null) {
			ArrayList<EMCCraftingPattern> bookPatterns = getPatterns();

			for(EMCCraftingPattern pattern : bookPatterns) {
				if(pattern != null) {
					craftingTracker.addCraftingOption(this, pattern);
				}
			}
		}
	}
	
	private ArrayList<EMCCraftingPattern> getPatterns() {
		ArrayList<EMCCraftingPattern> patterns = new ArrayList<EMCCraftingPattern>();
		Set<ItemStack> transmutations = TransmutationKnowledgeRegistryProxy.getPlayerKnownTransmutations(ItemHelper.getOwnerUUID(currentTome));
		for(ItemStack curItem : transmutations) {
			patterns.add(new EMCCraftingPattern(currentTome, curItem));
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
		return new ItemStack(Item.getItemFromBlock(EquivalentEnergistics.EMCCondenser));
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
			UUID tomeUUID = ItemHelper.getOwnerUUID(currentTome);
			if(tomeUUID != null && tomeUUID.equals(playerUUID)) {
				stalePatterns = true;
			}
		}
	}
	
	@TileEvent(TileEventType.TICK)
	public void onTick() {
		if(worldObj.isRemote) {
			if(isCrafting && craftTickCounter < TileEMCCrafter.BASE_TICKS_PER_CRAFT) {
				craftTickCounter++;
				return;
			}
		}
		
		if(!this.isActive()) {
			return;
		}
		
		
		try {
			float crystalEMC = EnergyValueRegistryProxy.getEnergyValue(EquivalentEnergistics.EMCCrystal).getValue();
			if(currentEMC >= crystalEMC) {
				int numCrystals = (int)(currentEMC/crystalEMC);
				IStorageGrid storageGrid = gridProxy.getStorage();
				IAEItemStack crystals = AEApi.instance().storage().createItemStack(new ItemStack(EquivalentEnergistics.EMCCrystal, numCrystals));
	
				IAEItemStack rejected = storageGrid.getItemInventory().injectItems(crystals, Actionable.SIMULATE, mySource);
	
				if((rejected == null) || (rejected.getStackSize() == 0)) {
					storageGrid.getItemInventory().injectItems(crystals, Actionable.MODULATE, mySource);
					currentEMC -= crystalEMC * numCrystals;
				}
			}
			
			if(stalePatterns && gridProxy.isReady()) {
				gridProxy.getGrid().postEvent(new MENetworkCraftingPatternChange(this, getActionableNode()));
				stalePatterns = false;
			}
		} catch(GridAccessException e) {}
		
		if(isCrafting) {
			craftingTick();
		}
	}

	private void craftingTick() {
		if(outputStack == null) {
			isCrafting = false;
		}
		
		if(this.craftTickCounter >= TileEMCCrafter.BASE_TICKS_PER_CRAFT) {
			try	{
				IStorageGrid storageGrid = gridProxy.getStorage();

				IAEItemStack rejected = storageGrid.getItemInventory().injectItems(AEApi.instance().storage().createItemStack(outputStack), Actionable.SIMULATE, mySource );

				if(rejected == null || rejected.getStackSize() == 0) {
					storageGrid.getItemInventory().injectItems(AEApi.instance().storage().createItemStack(outputStack), Actionable.MODULATE, mySource);

					this.isCrafting = false;
					this.outputStack = null;
				}
			} catch(GridAccessException e) {}
		} else {
			try	{
				IEnergyGrid eGrid = gridProxy.getEnergy();
				double powerExtracted = eGrid.extractAEPower(TileEMCCrafter.ACTIVE_POWER, Actionable.SIMULATE, PowerMultiplier.CONFIG);

				if(powerExtracted - TileEMCCrafter.ACTIVE_POWER >= 0.0D) {
					eGrid.extractAEPower(TileEMCCrafter.ACTIVE_POWER, Actionable.MODULATE, PowerMultiplier.CONFIG);
					craftTickCounter++ ;
				}
			} catch(GridAccessException e) {}
		}
	}

	public void energyValueEvent() {
		if(currentTome != null) {
			if(ItemHelper.getOwnerUUID(currentTome) != null) {
				stalePatterns = true;
			}
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
	public boolean onReceiveNetworkData(final ByteBuf stream)	{
		isActive = stream.readBoolean();
		isCrafting = stream.readBoolean();
		if(isCrafting) {
			craftTickCounter = stream.readInt();
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
	}
}
