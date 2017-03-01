package com.mordenkainen.equivalentenergistics.tiles.crafter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.mordenkainen.equivalentenergistics.integration.ae2.grid.IGridProxy;
import com.mordenkainen.equivalentenergistics.registries.ItemEnum;

import appeng.api.networking.security.MachineSource;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class CraftingManager {

	private final double craftingTime;
	private final int maxJobs;
	private final ICraftingMonitor monitor;
	private final List<CraftingJob> jobs = new ArrayList<CraftingJob>();
	private final IGridProxy proxy;
	private final MachineSource source;
	
	public CraftingManager(final double craftingTime, final int maxJobs, final ICraftingMonitor monitor, final IGridProxy proxy, final MachineSource source) {
		super();
		this.craftingTime = craftingTime;
		this.maxJobs = maxJobs;
		this.monitor = monitor;
		this.proxy = proxy;
		this.source = source;
	}
	
	public boolean isBusy() {
		return jobs.size() >= maxJobs;
	}
	
	public boolean isCrafting() {
		return !jobs.isEmpty();
	}
	
	public boolean addJob(final ItemStack outputStack) {
		if (!isBusy()) {
			jobs.add(new CraftingJob(ItemEnum.isCrystal(outputStack) ? 0 : craftingTime, outputStack, proxy, source));
			return true;
		}
		return false;
	}
	
	public void craftingTick() {
		final Iterator<CraftingJob> iter = jobs.iterator();
		while (iter.hasNext()) {
			final CraftingJob job = iter.next();
			job.craftingTick();
			if (job.isFinished()) {
				iter.remove();
				monitor.craftingFinished(job.getOutput());
			}
		}
	}
	
	public List<ItemStack> getCurrentJobs() {
		final List<ItemStack> result = new ArrayList<ItemStack>();
		for (final CraftingJob job : jobs) {
			result.add(job.getOutput());
		}
		return result;
	}
	
	public void writeToNBT(final NBTTagCompound tag) {
		for (int i = 0; i < jobs.size(); i++) {
			final CraftingJob job = jobs.get(i);
			final NBTTagCompound itemStack = new NBTTagCompound();
			job.getOutput().writeToNBT(itemStack);
			itemStack.setDouble("RemainingTicks", job.getRemainingTicks());
			tag.setTag("Job" + i, itemStack);
		}
	}
	
	public void readFromNBT(final NBTTagCompound tag) {
		jobs.clear();
		for (int i = 0; i < maxJobs; i++) {
			if (tag.hasKey("Job" + i)) {
				final ItemStack outputStack = ItemStack.loadItemStackFromNBT((NBTTagCompound) tag.getTag("Job" + i));
				jobs.add(new CraftingJob(ItemEnum.isCrystal(outputStack) ? 0 : tag.getDouble("RemainingTicks"), outputStack, proxy, source));
			}
		}
	}
	
}
