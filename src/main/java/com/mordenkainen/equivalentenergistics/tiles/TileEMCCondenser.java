package com.mordenkainen.equivalentenergistics.tiles;

import java.util.ArrayList;

import com.mordenkainen.equivalentenergistics.blocks.BlockEMCCondenser;
import com.mordenkainen.equivalentenergistics.integration.ae2.grid.GridAccessException;
import com.mordenkainen.equivalentenergistics.integration.ae2.tiles.TileNetworkInv;
import com.mordenkainen.equivalentenergistics.integration.waila.IWailaNBTProvider;
import com.mordenkainen.equivalentenergistics.registries.BlockEnum;
import com.mordenkainen.equivalentenergistics.registries.ItemEnum;
import com.mordenkainen.equivalentenergistics.util.EMCUtils;
import com.mordenkainen.equivalentenergistics.util.InternalInventory;

import appeng.api.AEApi;
import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.networking.GridFlags;
import appeng.api.networking.energy.IEnergyGrid;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.storage.data.IAEItemStack;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class TileEMCCondenser extends TileNetworkInv implements IWailaNBTProvider {
	public static final int SLOT_COUNT = 4;
	private static final String INVSLOTS = "items";
	
	private CondenserInventory internalInventory;
	public float currentEMC = 0.0f;
	
	private class CondenserInventory extends InternalInventory {
		public CondenserInventory()	{
			super("EMCCondenserInventory", TileEMCCondenser.SLOT_COUNT, 64); 
		}
	
		@Override
		public boolean isItemValidForSlot(final int slotId, final ItemStack itemStack) {
			return EMCUtils.getInstance().hasEMC(itemStack);
		}
	}
	
	public TileEMCCondenser() {
		super(new ItemStack(Item.getItemFromBlock(BlockEnum.EMCCONDENSER.getBlock())));
		internalInventory = new CondenserInventory();
		gridProxy.setFlags(GridFlags.REQUIRE_CHANNEL);
		gridProxy.setIdlePowerUsage(BlockEMCCondenser.condenserIdlePower);
	}
	
	public void getDrops(final World world, final int x, final int y, final int z, final ArrayList<ItemStack> drops) {		
		for(int i = 0; i < SLOT_COUNT; i++) {
			ItemStack item = internalInventory.slots[i];

			if(item != null) {
				drops.add(item);
			}
		}
	}
	
	@Override
	public NBTTagCompound getWailaTag(NBTTagCompound tag) {
		tag.setFloat("currentEMC", currentEMC);
		return tag;
	}
	
	@Override
	public void readFromNBT(final NBTTagCompound data) {
		super.readFromNBT(data);
		internalInventory.loadFromNBT(data, TileEMCCondenser.INVSLOTS);
		currentEMC = data.getFloat("CurrentEMC");
	}
	
	@Override
	public void writeToNBT(final NBTTagCompound data) {
		super.writeToNBT(data);
		internalInventory.saveToNBT(data, TileEMCCondenser.INVSLOTS);
		data.setFloat("CurrentEMC", currentEMC);
	}
	
	@Override
	public void updateEntity() {
		boolean flag = false;
		if(worldObj.isRemote || !isActive()) {
			return;
		}
		
		try	{
			for(int i = 0; i < SLOT_COUNT; i++) {
				if(internalInventory.getStackInSlot(i) != null){
					ItemStack testItem = internalInventory.getStackInSlot(i).copy();
					testItem.stackSize = 1;
					if(EMCUtils.getInstance().hasEMC(testItem) && EMCUtils.getInstance().getEnergyValue(testItem) > 0) {
						float itemEMC = EMCUtils.getInstance().getEnergyValue(testItem);
						int itemAvail = Math.min(BlockEMCCondenser.itemsPerTick, Math.min(internalInventory.getStackInSlot(i).stackSize, (int)Math.floor((Float.MAX_VALUE - currentEMC) / itemEMC)));
						internalInventory.decrStackSize(i, itemAvail);
						currentEMC += itemEMC * itemAvail;
						flag = true;
					} else {
						IStorageGrid storageGrid = gridProxy.getStorage();
						
						IAEItemStack rejected = storageGrid.getItemInventory().injectItems(AEApi.instance().storage().createItemStack(internalInventory.getStackInSlot(i)), Actionable.SIMULATE, mySource);
			
						if(rejected == null || rejected.getStackSize() == 0) {
							storageGrid.getItemInventory().injectItems(AEApi.instance().storage().createItemStack(internalInventory.getStackInSlot(i)), Actionable.MODULATE, mySource);
							internalInventory.setInventorySlotContents(i, null);
						}
					}
				}
			}
			
			float crystalEMC = EMCUtils.getInstance().getCrystalEMC();
			if(currentEMC >= crystalEMC) {
				int numCrystals = Math.min(BlockEMCCondenser.crystalsPerTick, (int)Math.floor(currentEMC/crystalEMC));
				IEnergyGrid eGrid = gridProxy.getEnergy();
				double powerRequired = crystalEMC * numCrystals * BlockEMCCondenser.condenserActivePower;
				while (numCrystals > 0 && eGrid.extractAEPower(powerRequired, Actionable.SIMULATE, PowerMultiplier.CONFIG) < powerRequired) {
					numCrystals--;
				}
				
				IAEItemStack crystal = AEApi.instance().storage().createItemStack(new ItemStack(ItemEnum.EMCCRYSTAL.getItem(), numCrystals));
				IStorageGrid storageGrid = gridProxy.getStorage();
	
				IAEItemStack rejected = storageGrid.getItemInventory().injectItems(crystal, Actionable.SIMULATE, mySource);
	
				if(rejected == null || rejected.getStackSize() == 0) {
					storageGrid.getItemInventory().injectItems(crystal, Actionable.MODULATE, mySource);
					eGrid.extractAEPower(powerRequired, Actionable.MODULATE, PowerMultiplier.CONFIG);
					currentEMC -= crystalEMC * numCrystals;
					flag = true;
				}
			}
		} catch(GridAccessException e) {}
		if (flag) {
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}
	}

	@Override
	public IInventory getInventory() {
		return internalInventory;
	}
}
