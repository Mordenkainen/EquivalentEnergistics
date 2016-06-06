package com.mordenkainen.equivalentenergistics.integration.ae2.cache;

import java.util.ArrayList;
import java.util.List;

import com.mordenkainen.equivalentenergistics.integration.Integration;
import com.mordenkainen.equivalentenergistics.items.HandlerEMCCell;
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
import appeng.api.storage.ICellContainer;
import appeng.api.storage.ICellProvider;
import appeng.api.storage.IMEInventoryHandler;
import appeng.api.storage.StorageChannel;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IItemList;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class EMCStorageGrid implements IGridCache, ICellProvider, IMEInventoryHandler<IAEItemStack> {
	
	private float currentEMC;
	private float maxEMC;
	private final IGrid grid;
	private boolean dirty = false;
	private List<ICellProvider> driveBays = new ArrayList<ICellProvider>();
	
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
			List<IMEInventoryHandler> cells = provider.getCellArray(StorageChannel.ITEMS);
			for(IMEInventoryHandler cell : cells) {
				IItemList<IAEItemStack> checkStack = AEApi.instance().storage().createItemList();
				checkStack.add(AEApi.instance().storage().createItemStack(ItemEnum.EMCCOMM.getDamagedStack(0)));
				IItemList<IAEItemStack> retStacks = cell.getAvailableItems(checkStack);
				if(!retStacks.isEmpty() && retStacks.getFirstItem().getItem().equals(ItemEnum.EMCCOMM.getItem()) && retStacks.getFirstItem().getItemStack().hasTagCompound()) {
					ItemStack stack = retStacks.getFirstItem().getItemStack();
					currentEMC += stack.getTagCompound().getFloat("emc");
					maxEMC += stack.getTagCompound().getFloat("max");
				}
			}
		}
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

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void removeNode(final IGridNode gridNode, final IGridHost machine) {
		if(machine instanceof ICellProvider) {
			driveBays.remove((ICellProvider)machine);
			List<IMEInventoryHandler> cells = ((ICellProvider)machine).getCellArray(StorageChannel.ITEMS);
			for(IMEInventoryHandler cell : cells) {
				IItemList<IAEItemStack> checkStack = AEApi.instance().storage().createItemList();
				checkStack.add(AEApi.instance().storage().createItemStack(ItemEnum.EMCCOMM.getDamagedStack(0)));
				IItemList<IAEItemStack> retStacks = cell.getAvailableItems(checkStack);
				if(!retStacks.isEmpty() && retStacks.getFirstItem().getItem().equals(ItemEnum.EMCCOMM.getItem()) && retStacks.getFirstItem().getItemStack().hasTagCompound()) {
					ItemStack stack = retStacks.getFirstItem().getItemStack();
					currentEMC -= stack.getTagCompound().getFloat("emc");
					maxEMC -= stack.getTagCompound().getFloat("max");
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
	public IItemList<IAEItemStack> getAvailableItems(IItemList<IAEItemStack> stacks) {
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
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public float injectEMC(float emc) {
		float toAdd = emc;
		for(ICellProvider provider : driveBays) {
			List<IMEInventoryHandler> cells = provider.getCellArray(StorageChannel.ITEMS);
			for(IMEInventoryHandler cell : cells) {
				IItemList<IAEItemStack> checkStack = AEApi.instance().storage().createItemList();
				ItemStack injectItem = ItemEnum.EMCCOMM.getDamagedStack(0);
				injectItem.stackTagCompound = new NBTTagCompound();
				injectItem.getTagCompound().setFloat("change", toAdd);
				checkStack.add(AEApi.instance().storage().createItemStack(injectItem));
				IItemList<IAEItemStack> retStacks = cell.getAvailableItems(checkStack);
				if(!retStacks.isEmpty() && retStacks.getFirstItem().getItem().equals(ItemEnum.EMCCOMM.getItem())) {
					ItemStack comStack = retStacks.getFirstItem().getItemStack();
					if(comStack.hasTagCompound() && comStack.stackTagCompound.hasKey("adjust")) {
						toAdd -= comStack.stackTagCompound.getFloat("adjust");
						if(toAdd <= 0) {
							break;
						}
					}
				}
			}
			if(toAdd <= 0) {
				break;
			}
		}
		
		currentEMC += emc - toAdd;
		
		
		
		
		
		//dirty = true;
		
		
		
		return emc - toAdd;
	}
	
}
