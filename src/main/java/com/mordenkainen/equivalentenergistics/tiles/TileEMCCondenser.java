package com.mordenkainen.equivalentenergistics.tiles;

import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.util.ArrayList;

import com.mordenkainen.equivalentenergistics.EquivalentEnergistics;
import com.mordenkainen.equivalentenergistics.config.ConfigManager;
import com.mordenkainen.equivalentenergistics.util.EMCUtils;
import com.mordenkainen.equivalentenergistics.util.InternalInventory;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.networking.GridFlags;
import appeng.api.networking.energy.IEnergyGrid;
import appeng.api.networking.security.MachineSource;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.util.AECableType;
import appeng.api.util.DimensionalCoord;
import appeng.me.GridAccessException;
import appeng.me.GridException;
import appeng.tile.TileEvent;
import appeng.tile.events.TileEventType;
import appeng.tile.grid.AENetworkInvTile;
import appeng.tile.inventory.InvOperation;
import appeng.api.AEApi;

public class TileEMCCondenser extends AENetworkInvTile {
	private class CondenserInventory extends InternalInventory {
		public CondenserInventory()	{
			super("EMCCondenserInventory", TileEMCCondenser.SLOT_COUNT, 64); 
		}
	
		@Override
		public boolean isItemValidForSlot(final int slotId, final ItemStack itemStack) {
			return EMCUtils.getInstance().hasEMC(itemStack);
		}
	}
	
	private MachineSource mySource;
	private CondenserInventory internalInventory;
	public static final int SLOT_COUNT = 4;
	private static final String INVSLOTS = "items";
	private float currentEMC = 0.0f;
	private boolean isActive;
	
	public TileEMCCondenser() {
		mySource = new MachineSource(this);
		internalInventory = new CondenserInventory();
		if(FMLCommonHandler.instance().getEffectiveSide().isServer()) {
			this.gridProxy.setIdlePowerUsage(ConfigManager.condenserIdlePower);
			this.gridProxy.setFlags(GridFlags.REQUIRE_CHANNEL);
		}
	}
	
	@Override
	public AECableType getCableConnectionType(ForgeDirection dir) {
		return AECableType.SMART;
	}

	@Override
	public DimensionalCoord getLocation() {
		return new DimensionalCoord(this);
	}

	@Override
	public int[] getAccessibleSlotsBySide(ForgeDirection dir) {
		return new int[] {0,1,2,3};
	}
	
	@Override
	public boolean canExtractItem(int slotIndex, ItemStack extractedItem, int side) {
		return false;
	}

	@Override
	public IInventory getInternalInventory() {
		return internalInventory;
	}

	@Override
	public void onChangeInventory(IInventory arg0, int arg1, InvOperation arg2, ItemStack arg3, ItemStack arg4) {}
	
	@TileEvent(TileEventType.TICK)
	public void onTick() {
		if(!worldObj.isRemote) {
			if(!isActive()) {
				return;
			}
			try	{
				for(int i = 0; i < SLOT_COUNT; i++) {
					if(internalInventory.getStackInSlot(i) != null){
						if(EMCUtils.getInstance().hasEMC(internalInventory.getStackInSlot(i))) {
							ItemStack testItem = internalInventory.getStackInSlot(i).copy();
							testItem.stackSize = 1;
							float itemEMC = EMCUtils.getInstance().getEnergyValue(testItem);
							int itemAvail = Math.min(ConfigManager.itemsPerTick, Math.min(internalInventory.getStackInSlot(i).stackSize, (int)Math.floor((Float.MAX_VALUE - currentEMC) / itemEMC)));
							internalInventory.decrStackSize(i, itemAvail);
							currentEMC += itemEMC * itemAvail;
						} else {
							IStorageGrid storageGrid = this.gridProxy.getStorage();
							
							IAEItemStack rejected = storageGrid.getItemInventory().injectItems(AEApi.instance().storage().createItemStack(internalInventory.getStackInSlot(i)), Actionable.SIMULATE, this.mySource);
				
							if(rejected == null || rejected.getStackSize() == 0) {
								storageGrid.getItemInventory().injectItems(AEApi.instance().storage().createItemStack(internalInventory.getStackInSlot(i)), Actionable.MODULATE, this.mySource);
								internalInventory.setInventorySlotContents(i, null);
							}
						}
					}
				}
				
				float crystalEMC = EMCUtils.getInstance().getCrystalEMC();
				if(currentEMC >= crystalEMC) {
					int numCrystals = Math.min(ConfigManager.crystalsPerTick, (int)Math.floor(currentEMC/crystalEMC));
					IEnergyGrid eGrid = gridProxy.getEnergy();
					double powerRequired = crystalEMC * numCrystals * ConfigManager.condenserActivePower;
					while(numCrystals > 0) {
						if(eGrid.extractAEPower(powerRequired, Actionable.SIMULATE, PowerMultiplier.CONFIG) >= powerRequired) {
							break;
						} else {
							numCrystals--;
						}
					}
					
					IAEItemStack crystal = AEApi.instance().storage().createItemStack(new ItemStack(EquivalentEnergistics.itemEMCCrystal, numCrystals));
					IStorageGrid storageGrid = this.gridProxy.getStorage();
		
					IAEItemStack rejected = storageGrid.getItemInventory().injectItems(crystal, Actionable.SIMULATE, this.mySource);
		
					if(rejected == null || rejected.getStackSize() == 0) {
						storageGrid.getItemInventory().injectItems(crystal, Actionable.MODULATE, this.mySource);
						eGrid.extractAEPower(powerRequired, Actionable.MODULATE, PowerMultiplier.CONFIG);
						currentEMC -= crystalEMC * numCrystals;
					}
				}
			} catch(GridAccessException e) {}
		}
	}

	@TileEvent(TileEventType.WORLD_NBT_READ)
	public void onLoadNBT(final NBTTagCompound data) {
		internalInventory.loadFromNBT(data, TileEMCCondenser.INVSLOTS);
		currentEMC = data.getFloat("CurrentEMC");
	}
	
	@TileEvent(TileEventType.WORLD_NBT_WRITE)
	public void onSaveNBT(final NBTTagCompound data) {
		internalInventory.saveToNBT(data, TileEMCCondenser.INVSLOTS);
		data.setFloat("CurrentEMC", currentEMC);
	}
	
	@Override
	protected ItemStack getItemFromTile(final Object obj) {
		return new ItemStack(Item.getItemFromBlock(EquivalentEnergistics.blockEMCCondenser));
	}
	
	@Override
	public void getDrops(final World world, final int x, final int y, final int z, final ArrayList<ItemStack> drops) {		
		for(int i = 0; i < SLOT_COUNT; i++) {
			ItemStack item = this.internalInventory.slots[i];

			if(item != null) {
				drops.add(item);
			}
		}
	}

	public boolean isActive() {
		if(!worldObj.isRemote) {
			if((gridProxy != null) && (gridProxy.getNode() != null)) {
				isActive = gridProxy.getNode().isActive();
			}
		}

		return isActive;
	}
	
	public void onBreak() {
		gridProxy.invalidate();
	}
	
	@TileEvent(TileEventType.NETWORK_READ)
	@SideOnly(Side.CLIENT)
	public boolean onReceiveNetworkData(final ByteBuf stream)	{
		isActive = stream.readBoolean();
		return true;
	}
	
	@TileEvent(TileEventType.NETWORK_WRITE)
	public void onSendNetworkData(final ByteBuf stream) throws IOException {
		stream.writeBoolean(isActive());
	}
	
	public void setOwner(final EntityPlayer player) {
		gridProxy.setOwner(player);
	}
}
