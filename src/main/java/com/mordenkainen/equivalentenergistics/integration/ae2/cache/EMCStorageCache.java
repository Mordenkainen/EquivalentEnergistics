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
	
	private long currentEMC;
	private long lastDisplay;
	private final IGrid grid;
	
	public EMCStorageCache(final IGrid _grid) {
		grid = _grid;
	}
	
	@MENetworkEventSubscribe
	public void afterCacheConstruction( final MENetworkPostCacheConstruction cacheConstruction )
	{

		((IStorageGrid)this.grid.getCache(IStorageGrid.class)).registerCellProvider( this );
	}
	
	@Override
	public void onUpdateTick() {
		//System.out.println(currentEMC);

	}

	@Override
	public void removeNode(final IGridNode gridNode, final IGridHost machine) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addNode(final IGridNode gridNode, final IGridHost machine) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSplit(final IGridStorage destinationStorage) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onJoin(final IGridStorage sourceStorage) {
		// TODO Auto-generated method stub

	}

	@Override
	public void populateGridStorage(final IGridStorage destinationStorage) {
		// TODO Auto-generated method stub

	}

	@Override
	public IAEItemStack injectItems(final IAEItemStack input, final Actionable type, final BaseActionSource src) {
		if (input.getItem().equals(ItemEnum.EMCCRYSTAL.getItem())) {
			final int dam = input.getItemDamage();
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
	public IAEItemStack extractItems(final IAEItemStack request, final Actionable mode, final BaseActionSource src) {
		if (request.getItem().equals(ItemEnum.EMCCRYSTAL.getItem()) && currentEMC >= 256) {
			final int toRemove = (int)Math.min(request.getStackSize(), currentEMC/256);
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
	public IItemList<IAEItemStack> getAvailableItems(final IItemList<IAEItemStack> out) {
		final int crystalcount = (int) (currentEMC/256);
		if (crystalcount > 0) {
			final IAEItemStack stack = AEApi.instance().storage().createItemStack(new ItemStack(ItemEnum.EMCCRYSTAL.getItem(), crystalcount, 0));
			out.add(stack);
		}
		if (currentEMC > 0) {
			final ItemStack totStack = new ItemStack(ItemEnum.EMCTOTITEM.getItem(), 1);
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
	public boolean isPrioritized(final IAEItemStack input) {
		return input.getItem().equals(ItemEnum.EMCCRYSTAL.getItem());
	}

	@Override
	public boolean canAccept(final IAEItemStack input) {
		return input.getItem().equals(ItemEnum.EMCCRYSTAL.getItem());
	}

	@Override
	public int getSlot() {
		return 0;
	}

	@Override
	public boolean validForPass(final int pass) {
		return pass == 1;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List<IMEInventoryHandler> getCellArray(final StorageChannel channel) {
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
		final List<IAEItemStack> stacks = new ArrayList<IAEItemStack>();
		if (lastDisplay > 0) {
			final ItemStack oldTotStack = new ItemStack(ItemEnum.EMCTOTITEM.getItem(), -1);
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
