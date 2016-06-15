package com.mordenkainen.equivalentenergistics.integration.ae2.cache;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.mordenkainen.equivalentenergistics.integration.ae2.HandlerEMCCell;
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

public class EMCStorageGrid implements IGridCache, ICellProvider, IMEInventoryHandler<IAEItemStack> {
	
	private float currentEMC;
	private float maxEMC;
	private final IGrid grid;
	private boolean dirty = false;
	private final List<ICellProvider> driveBays = new ArrayList<ICellProvider>();
	
	public EMCStorageGrid(final IGrid _grid) {
		grid = _grid;
	}
	
	@MENetworkEventSubscribe
	public void afterCacheConstruction( final MENetworkPostCacheConstruction cacheConstruction )
	{
		((IStorageGrid)this.grid.getCache(IStorageGrid.class)).registerCellProvider( this );
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@MENetworkEventSubscribe
	public void cellUpdate( final MENetworkCellArrayUpdate cellUpdate )
	{
		currentEMC = maxEMC = 0;
		for(ICellProvider provider : driveBays) {
			final List<IMEInventoryHandler> cells = provider.getCellArray(StorageChannel.ITEMS);
			for(final IMEInventoryHandler cell : cells) {
				HandlerEMCCell handler = getHandler(cell);
				if (handler != null) {
					currentEMC += handler.getEMC();
					maxEMC += handler.getCapacity();
				}
			}
		}
	}
	
	private HandlerEMCCell getHandler(IMEInventoryHandler<IAEItemStack> cell) {
		if (cell == null) {
			return null;
		}
		if (cell instanceof HandlerEMCCell) {
			return (HandlerEMCCell) cell;
		}
		Class<?> clazz = cell.getClass();
		if ("DriveWatcher".equals(clazz.getSimpleName())) {
			Class<?> meHandler = clazz.getSuperclass();
			Field intHandler;
			try {
				intHandler = meHandler.getDeclaredField("internal");
				intHandler.setAccessible(true);
				IMEInventoryHandler<IAEItemStack> realHandler = (IMEInventoryHandler<IAEItemStack>) intHandler.get(cell);
				if (realHandler instanceof HandlerEMCCell) {
					return (HandlerEMCCell) realHandler;
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if ("ChestMonitorHandler".equals(clazz.getSimpleName())) {
			Class<?> meMon = clazz.getSuperclass();
			Field extHandler;
			Field intHandler;
			try {
				extHandler = meMon.getDeclaredField("internalHandler");
				extHandler.setAccessible(true);
				IMEInventoryHandler<IAEItemStack> extHandlerVal = (IMEInventoryHandler<IAEItemStack>) extHandler.get(cell);
				intHandler = extHandlerVal.getClass().getDeclaredField("internal");
				intHandler.setAccessible(true);
				IMEInventoryHandler<IAEItemStack> realHandler = (IMEInventoryHandler<IAEItemStack>) intHandler.get(extHandlerVal);
				if (realHandler instanceof HandlerEMCCell) {
					return (HandlerEMCCell) realHandler;
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	public void onUpdateTick() {
		/*if(dirty) {
			dirty = false;
			updateDisplay();
		}*/
		
		injectEMC(200);
		System.out.println("CurrentEMC: " + currentEMC);
		System.out.println("MaxEMC: " + maxEMC);
	}

	@SuppressWarnings({"rawtypes", "unchecked" })
	@Override
	public void removeNode(final IGridNode gridNode, final IGridHost machine) {
		if(machine instanceof ICellProvider) {
			driveBays.remove((ICellProvider)machine);
			final List<IMEInventoryHandler> cells = ((ICellProvider)machine).getCellArray(StorageChannel.ITEMS);
			for(final IMEInventoryHandler cell : cells) {
				HandlerEMCCell handler = getHandler(cell);
				if (handler != null) {
					currentEMC -= handler.getEMC();
					maxEMC -= handler.getCapacity();
				}
			}
		}
	}

	@Override
	public void addNode(final IGridNode gridNode, final IGridHost machine) {
		if(machine instanceof ICellProvider) {
			driveBays.add((ICellProvider)machine);
		}
	}

	@Override
	public void onSplit(final IGridStorage dstStorage) {
		/*currentEMC /= 2;
		dstStorage.dataObject().setFloat("emc", currentEMC);
		dirty = true;*/
	}

	@Override
	public void onJoin(final IGridStorage sourceStorage) {
		/*currentEMC += sourceStorage.dataObject().getFloat("emc");
		dirty = true;*/
	}

	@Override
	public void populateGridStorage(final IGridStorage dstStorage) {
		/*dstStorage.dataObject().setFloat("emc", currentEMC);
		dirty = true;*/
	}

	@Override
	public IAEItemStack injectItems(final IAEItemStack stack, final Actionable mode, final BaseActionSource src) {
		/*if (stack.getItem().equals(ItemEnum.EMCCRYSTAL.getItem())) {
			final int dam = stack.getItemDamage();
			if(mode == Actionable.MODULATE) {
				currentEMC += Integration.emcHandler.getCrystalEMC(dam) *  stack.getStackSize();
				dirty = true;
			}
			return null;
		} else {
			return stack;
		}*/
		return stack;
	}

	@Override
	public IAEItemStack extractItems(final IAEItemStack request, final Actionable mode, final BaseActionSource src) {
		/*if (request.getItem().equals(ItemEnum.EMCCRYSTAL.getItem())) {
			final int toRemove = (int)Math.min(request.getStackSize(), currentEMC / Integration.emcHandler.getCrystalEMC(request.getItemDamage()));
			if (toRemove > 0) {
				if(mode == Actionable.MODULATE) {
					currentEMC -= toRemove * Integration.emcHandler.getCrystalEMC(request.getItemDamage());
					dirty = true;
				}
				return AEApi.instance().storage().createItemStack(ItemEnum.EMCCRYSTAL.getDamagedStack(request.getItemDamage())).setStackSize(toRemove);
			}
		}*/
		return null;
	}

	@Override
	public IItemList<IAEItemStack> getAvailableItems(final IItemList<IAEItemStack> stacks) {
		/*if(currentEMC > 0) {
			float remainingEMC = currentEMC;
			for(int i = 4; i >= 0; i--) {
				long crystalcount = (long) (remainingEMC / Integration.emcHandler.getCrystalEMC(i));
				if(crystalcount > 0) {
					stacks.add(AEApi.instance().storage().createItemStack(ItemEnum.EMCCRYSTAL.getDamagedStack(i)).setStackSize(crystalcount));
					remainingEMC -= crystalcount * Integration.emcHandler.getCrystalEMC(i);
				}
			}
		}

		if (currentEMC > 0) {
			final ItemStack totStack = new ItemStack(ItemEnum.EMCTOTITEM.getItem(), 1);
			stacks.add(AEApi.instance().storage().createItemStack(totStack).setStackSize((long)currentEMC));
		}*/
		return stacks;
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
	public boolean isPrioritized(final IAEItemStack stack) {
		return stack.getItem().equals(ItemEnum.EMCCRYSTAL.getItem());
	}

	@Override
	public boolean canAccept(final IAEItemStack stack) {
		return stack.getItem().equals(ItemEnum.EMCCRYSTAL.getItem());
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
		IItemList<IAEItemStack> stacks = AEApi.instance().storage().createItemList();
		getAvailableItems(stacks);
		for(IAEItemStack stack: stacks) {
			stack.setStackSize(-stack.getStackSize());
		}
		((IStorageGrid)this.grid.getCache(IStorageGrid.class)).postAlterationOfStoredItems(StorageChannel.ITEMS, stacks, new BaseActionSource());
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked"})
	public float injectEMC(final float emc) {
		float toAdd = emc;
		for(final ICellProvider provider : driveBays) {
			List<IMEInventoryHandler> cells = provider.getCellArray(StorageChannel.ITEMS);
			for(IMEInventoryHandler cell : cells) {
				HandlerEMCCell handler = getHandler(cell);
				if (handler != null && handler.getAvail() > 0) {
					toAdd -= handler.adjustEMC(toAdd);
					if(toAdd <= 0) {
						break;
					}
				}
			}
			if(toAdd <= 0) {
				break;
			}
		}
		
		currentEMC += emc - toAdd;
		
		return emc - toAdd;
	}
	
}
