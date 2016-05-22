package com.mordenkainen.equivalentenergistics.items;

import com.mordenkainen.equivalentenergistics.integration.Integration;
import com.mordenkainen.equivalentenergistics.registries.ItemEnum;

import appeng.api.AEApi;
import appeng.api.config.AccessRestriction;
import appeng.api.config.Actionable;
import appeng.api.networking.security.BaseActionSource;
import appeng.api.storage.IMEInventoryHandler;
import appeng.api.storage.ISaveProvider;
import appeng.api.storage.StorageChannel;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IItemList;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class HandlerEMCCell implements IMEInventoryHandler<IAEItemStack> {

	private final NBTTagCompound cellData;
	private final ISaveProvider saveProvider;
	private long currentEMC;

	public HandlerEMCCell( final ItemStack storageStack, final ISaveProvider saveProvider )
	{
		// Ensure we have a NBT tag
		if( !storageStack.hasTagCompound() )
		{
			storageStack.setTagCompound( new NBTTagCompound() );
		}

		// Get the NBT tag
		this.cellData = storageStack.getTagCompound();
		
		this.saveProvider = saveProvider;
		
		if(cellData.hasKey("emc")) {
			currentEMC = cellData.getLong("emc");
		}
	}
	
	@Override
	public IAEItemStack injectItems(final IAEItemStack input, final Actionable type, final BaseActionSource src) {
		if (input.getItem().equals(ItemEnum.EMCCRYSTAL.getItem())) {
			final int dam = ((IAEItemStack)input).getItemDamage();
			if(type == Actionable.MODULATE) {
				currentEMC += Integration.emcHandler.getCrystalEMC(dam) *  ((IAEItemStack)input).getStackSize();
				cellData.setLong("emc", currentEMC);
				
				if( this.saveProvider != null )
				{
					this.saveProvider.saveChanges( this );
				}
			}
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
					cellData.setLong("emc", currentEMC);
					
					if( this.saveProvider != null )
					{
						this.saveProvider.saveChanges( this );
					}
				}
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
	public int getPriority() {
		return 0;
	}

	@Override
	public int getSlot() {
		return 0;
	}

	@Override
	public boolean validForPass(final int pass) {
		return pass == 1;
	}

}
