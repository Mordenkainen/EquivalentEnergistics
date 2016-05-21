package com.mordenkainen.equivalentenergistics.integration.ae2.cache;

import java.util.ArrayList;
import java.util.List;

import com.mordenkainen.equivalentenergistics.integration.Integration;
import com.mordenkainen.equivalentenergistics.registries.ItemEnum;

import appeng.api.AEApi;
import appeng.api.config.AccessRestriction;
import appeng.api.config.Actionable;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridCache;
import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
import appeng.api.networking.IGridStorage;
import appeng.api.networking.events.MENetworkCellArrayUpdate;
import appeng.api.networking.events.MENetworkEventSubscribe;
import appeng.api.networking.events.MENetworkPostCacheConstruction;
import appeng.api.networking.security.BaseActionSource;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.storage.ICellProvider;
import appeng.api.storage.IMEInventoryHandler;
import appeng.api.storage.StorageChannel;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IItemList;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class EMCStorageCache implements IGridCache, ICellProvider, IMEInventoryHandler<IAEItemStack> {
	public EMCStorageCache(IGrid _grid) {
		grid = _grid;
	}

	private long currentEMC;
	private long lastDisplay;
	private IGrid grid;
	
	@MENetworkEventSubscribe
	public void afterCacheConstruction( final MENetworkPostCacheConstruction cacheConstruction )
	{

		((IStorageGrid)this.grid.getCache(IStorageGrid.class)).registerCellProvider( this );
	}
	
	@Override
	public void onUpdateTick() {
		System.out.println(currentEMC);

	}

	@Override
	public void removeNode(IGridNode gridNode, IGridHost machine) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addNode(IGridNode gridNode, IGridHost machine) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSplit(IGridStorage destinationStorage) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onJoin(IGridStorage sourceStorage) {
		// TODO Auto-generated method stub

	}

	@Override
	public void populateGridStorage(IGridStorage destinationStorage) {
		// TODO Auto-generated method stub

	}

	@Override
	public IAEItemStack injectItems(IAEItemStack input, Actionable type, BaseActionSource src) {
		if (input.getItem().equals(ItemEnum.EMCCRYSTAL.getItem())) {
			int dam = input.getItemDamage();
			if(type == Actionable.MODULATE) {
				currentEMC += Integration.emcHandler.getCrystalEMC(dam) *  input.getStackSize();
			}
			updateDisplay();
			return null;
		} else {
			return input;
		}
	}

	@Override
	public IAEItemStack extractItems(IAEItemStack request, Actionable mode, BaseActionSource src) {
		if (request.getItem().equals(ItemEnum.EMCCRYSTAL.getItem()) && currentEMC >= 256) {
			int toRemove = (int)Math.min(request.getStackSize(), currentEMC/256);
			if (toRemove > 0) {
				if(mode == Actionable.MODULATE) {
					currentEMC -= toRemove * 256;
				}
				updateDisplay();
				return AEApi.instance().storage().createItemStack(new ItemStack(ItemEnum.EMCCRYSTAL.getItem(), toRemove));
			}
		}
		return null;
	}

	@Override
	public IItemList<IAEItemStack> getAvailableItems(IItemList<IAEItemStack> out) {
		int crystalcount = (int) (currentEMC/256);
		if (crystalcount > 0) {
			IAEItemStack stack = AEApi.instance().storage().createItemStack(new ItemStack(ItemEnum.EMCCRYSTAL.getItem(), crystalcount, 0));
			out.add(stack);
		}
		if (currentEMC > 0) {
			ItemStack totStack = new ItemStack(ItemEnum.EMCTOTITEM.getItem(), 1);
			totStack.stackTagCompound = new NBTTagCompound();
			totStack.stackTagCompound.setLong("emc", currentEMC);
			out.add(AEApi.instance().storage().createItemStack(totStack));
			lastDisplay = currentEMC;
		}
		return out;
	}

	@Override
	public StorageChannel getChannel() {
		return StorageChannel.ITEMS;
	}

	@Override
	public AccessRestriction getAccess() {
		return AccessRestriction.READ_WRITE;
	}

	@Override
	public boolean isPrioritized(IAEItemStack input) {
		return input.getItem().equals(ItemEnum.EMCCRYSTAL.getItem());
	}

	@Override
	public boolean canAccept(IAEItemStack input) {
		return input.getItem().equals(ItemEnum.EMCCRYSTAL.getItem());
	}

	@Override
	public int getSlot() {
		return 0;
	}

	@Override
	public boolean validForPass(int i) {
		return i == 1;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List<IMEInventoryHandler> getCellArray(StorageChannel channel) {
		final List<IMEInventoryHandler> list = new ArrayList<IMEInventoryHandler>( 1 );

		if( channel == StorageChannel.ITEMS )
		{
			list.add( this );
		}

		return list;
	}

	@Override
	public int getPriority() {
		return Integer.MAX_VALUE - 1;
	}
	
	private void updateDisplay() {
		List<IAEItemStack> stacks = new ArrayList<IAEItemStack>();
		if (lastDisplay > 0) {
			ItemStack oldTotStack = new ItemStack(ItemEnum.EMCTOTITEM.getItem(), -1);
			oldTotStack.stackTagCompound = new NBTTagCompound();
			oldTotStack.stackTagCompound.setLong("emc", lastDisplay);
			stacks.add(AEApi.instance().storage().createItemStack(oldTotStack));
		}
		lastDisplay = 0;
		/*if (currentEMC > 0) {
			ItemStack totStack = new ItemStack(ItemEnum.EMCTOTITEM.getItem(), 1);
			totStack.stackTagCompound = new NBTTagCompound();
			totStack.stackTagCompound.setLong("emc", currentEMC);
			stacks.add(AEApi.instance().storage().createItemStack(totStack));
			lastDisplay = currentEMC;
		}*/
		((IStorageGrid)this.grid.getCache(IStorageGrid.class)).postAlterationOfStoredItems(StorageChannel.ITEMS, stacks, new BaseActionSource());
		this.grid.postEvent(new MENetworkCellArrayUpdate());
	}
}
