package com.mordenkainen.equivalentenergistics.blocks.crafter.tiles;

import java.util.ArrayList;
import java.util.List;

import com.mordenkainen.equivalentenergistics.integration.ae2.grid.AEProxy;
import com.mordenkainen.equivalentenergistics.items.ModItems;

import appeng.api.networking.security.MachineSource;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.items.ItemHandlerHelper;

public class CraftingManager {

    private static final String TICK_TAG = "RemainingTicks";
    private static final String POWER_TAG = "PowerPerTick";
    private static final String JOB_TAG = "Job";

    private final double craftingTime;
    private final int maxJobs;
    private final ICraftingMonitor monitor;
    private final CraftingJob[] jobs;
    private final AEProxy proxy;
    private final MachineSource source;

    public CraftingManager(final double craftingTime, final int maxJobs, final ICraftingMonitor monitor, final AEProxy proxy, final MachineSource source) {
        super();
        this.craftingTime = craftingTime;
        this.maxJobs = maxJobs;
        this.monitor = monitor;
        this.proxy = proxy;
        this.source = source;
        jobs = new CraftingJob[maxJobs];
    }

    public boolean isBusy() {
        for (int i = 0; i < maxJobs; i ++) {
            if (jobs[i] == null) {
                return false;
            }
        }
        return true;
    }

    public boolean isCrafting() {
        for (int i = 0; i < maxJobs; i ++) {
            if (jobs[i] != null) {
                return true;
            }
        }
        return false;
    }

    public boolean addJob(final ItemStack outputStack, final double emc, final double powerPerEMC) {
        if (!isBusy()) {
            for (int i = 0; i < maxJobs; i++) {
                if (jobs[i] == null) {
                    jobs[i] = new CraftingJob(outputStack.getItem() == ModItems.CRYSTAL ? 0 : craftingTime, outputStack, (emc / craftingTime) * powerPerEMC, proxy, source);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean craftingTick() {
        for (int i = 0; i < maxJobs; i ++) {
            if (jobs[i] != null) {
                if (!jobs[i].craftingTick()) {
                    return false;
                }
                if (jobs[i].isFinished()) {
                    final ItemStack output = jobs[i].getOutput();
                    jobs[i] = null;
                    monitor.craftingFinished(output);
                }
            }
        }
        return true;
    }

    public List<ItemStack> getCurrentJobs() {
        final List<ItemStack> result = new ArrayList<ItemStack>();
        for (int i = 0; i < jobs.length; i++) {
            ItemStack outputStack = null;
            if (jobs[i] == null) {
                outputStack = null;
            } else {
                outputStack = ItemHandlerHelper.copyStackWithSize(jobs[i].getOutput().copy(), 1);
            }
            result.add(outputStack);
        }
        return result;
    }

    public void writeToNBT(final NBTTagCompound tag) {
        for (int i = 0; i < jobs.length; i++) {
            final CraftingJob job = jobs[i];
            if (job != null) {
                final NBTTagCompound itemStack = new NBTTagCompound();
                job.getOutput().writeToNBT(itemStack);
                itemStack.setDouble(TICK_TAG, job.getRemainingTicks());
                itemStack.setDouble(POWER_TAG, job.getCost());
                tag.setTag(JOB_TAG + i, itemStack);
            }
        }
    }

    public void readFromNBT(final NBTTagCompound tag) {
        for (int i = 0; i < maxJobs; i++) {
            if (tag.hasKey(JOB_TAG + i)) {
                final ItemStack outputStack = ItemStack.loadItemStackFromNBT((NBTTagCompound) tag.getTag(JOB_TAG + i));
                jobs[i] = new CraftingJob(outputStack.getItem() == ModItems.CRYSTAL ? 0 : tag.getDouble(TICK_TAG), outputStack, tag.getDouble(POWER_TAG), proxy, source);
            } else {
                jobs[i] = null;
            }
        }
    }

}
