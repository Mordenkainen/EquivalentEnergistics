package com.mordenkainen.equivalentenergistics.integration.ae2.cache.crafting;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.Random;

import com.google.common.base.Equivalence;
import com.google.common.base.Equivalence.Wrapper;
import com.mordenkainen.equivalentenergistics.EquivalentEnergistics;
import com.mordenkainen.equivalentenergistics.core.config.EqEConfig;
import com.mordenkainen.equivalentenergistics.integration.ae2.EMCCraftingPattern;
import com.mordenkainen.equivalentenergistics.items.ModItems;
import com.mordenkainen.equivalentenergistics.util.CompItemStack;

import appeng.api.networking.IGrid;
import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
import appeng.api.networking.IGridStorage;
import appeng.api.networking.events.MENetworkCraftingPatternChange;
import moze_intel.projecte.api.ProjectEAPI;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class EMCCraftingGrid implements IEMCCraftingGrid {

    private static Equivalence<ItemStack> eq = new CompItemStack();
    private static Map<Equivalence.Wrapper<ItemStack>, EMCCraftingPattern> patternList = new HashMap<Equivalence.Wrapper<ItemStack>, EMCCraftingPattern>();
    private static int patternVer;
    private static Map<EMCCraftingGrid, Boolean> craftingGrids = new WeakHashMap<EMCCraftingGrid, Boolean>();

    private final IGrid grid;
    private final Map<Equivalence.Wrapper<ItemStack>, EMCCraftingPattern> patterns = new HashMap<Equivalence.Wrapper<ItemStack>, EMCCraftingPattern>();
    private final Map<ITransProvider, String> patternProviders = new WeakHashMap<ITransProvider, String>();
    private int lastPatternVer = -1;
    private int refreshcounter;

    public EMCCraftingGrid(final IGrid grid) {
        this.grid = grid;
        craftingGrids.put(this, true);
        final Random rand = new Random();
        refreshcounter = rand.nextInt(EqEConfig.emcAssembler.refreshTime * 20);
    }

    @Override
    public void onUpdateTick() {
        if(++refreshcounter % (EqEConfig.emcAssembler.refreshTime * 20) == 0) {
            refreshcounter = 0;
            lastPatternVer = -1;
        }
        if (lastPatternVer != patternVer) {
            updatePatterns();
            lastPatternVer = patternVer;
        }
    }

    @Override
    public void removeNode(final IGridNode gridNode, final IGridHost machine) {
        if (machine instanceof ITransProvider) {
            patternProviders.remove(machine);
            if (!((ITransProvider) machine).getTransmutations().isEmpty()) {
                lastPatternVer = -1;
            }
        }
    }

    @Override
    public void addNode(final IGridNode gridNode, final IGridHost machine) {
        if (machine instanceof ITransProvider) {
            patternProviders.put((ITransProvider) machine, ((ITransProvider) machine).getPlayerUUID());
            if (!((ITransProvider) machine).getTransmutations().isEmpty()) {
                lastPatternVer = -1;
            }
        }
    }

    @Override
    public void updatePatterns() {
        patterns.clear();
        addCrystalPatterns();
        for (final ITransProvider provider : patternProviders.keySet()) {
            for (final ItemStack stack : provider.getTransmutations()) {
                addPattern(stack);
            }
        }
        grid.postEvent(new MENetworkCraftingPatternChange(null, null));
    }

    private void addPattern(final ItemStack stack) {
        final Equivalence.Wrapper<ItemStack> wrappedStack = eq.wrap(stack);
        createPattern(wrappedStack);
        if (patternList.containsKey(wrappedStack)) {
            patterns.put(wrappedStack, patternList.get(wrappedStack));
        }
    }

    private void addCrystalPatterns() {
        for (int i = 0; i < 4; i++) {
            final Equivalence.Wrapper<ItemStack> wrappedStack = eq.wrap(new ItemStack(ModItems.CRYSTAL, 64, i));
            if (!patternList.containsKey(wrappedStack)) {
                patternList.put(wrappedStack, new EMCCraftingPattern(wrappedStack.get()));
            }
            patterns.put(wrappedStack, patternList.get(wrappedStack));
        }
    }

    private void postKnowledgeEvent(final UUID playerUUID) {
        if (patternProviders.values().contains(playerUUID.toString())) {
            lastPatternVer = -1;
        }
    }

    @Override
    public EMCCraftingPattern[] getPatterns() {
        return patterns.isEmpty() ? new EMCCraftingPattern[0] : patterns.values().toArray(new EMCCraftingPattern[0]);
    }

    public static void energyEvent() {
        final Iterator<Wrapper<ItemStack>> iter = patternList.keySet().iterator();
        while (iter.hasNext()) {
            final Wrapper<ItemStack> wrappedStack = iter.next();
            if (ProjectEAPI.getEMCProxy().getValue(wrappedStack.get()) > 0) {
                final EMCCraftingPattern pattern = patternList.get(wrappedStack);
                pattern.rebuildPattern();
                if (!pattern.valid) {
                    EquivalentEnergistics.logger.warn("Invalid EMC pattern detected. Item: " + I18n.format(pattern.getOutputs()[0].getItem().getUnlocalizedName(pattern.getOutputs()[0].createItemStack()) + ".name", new Object[0]) + " EMC: " + String.format("%f", pattern.outputEMC));
                    iter.remove();
                }
            } else {
                iter.remove();
            }
        }

        patternVer++;
    }

    public static final void knowledgeEvent(final UUID playerUUID) {
        if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
            for (final EMCCraftingGrid craftingGrid : craftingGrids.keySet()) {
                craftingGrid.postKnowledgeEvent(playerUUID);
            }
        }
    }

    public static EMCCraftingPattern getPattern(final ItemStack stack) {
        final Equivalence.Wrapper<ItemStack> wrappedStack = eq.wrap(stack);
        createPattern(wrappedStack);
        if (patternList.containsKey(wrappedStack)) {
            return patternList.get(wrappedStack);
        }
        return null;
    }

    private static void createPattern(final Equivalence.Wrapper<ItemStack> wrappedStack) {
        if (!patternList.containsKey(wrappedStack)) {
            final EMCCraftingPattern pattern = new EMCCraftingPattern(wrappedStack.get());
            if (pattern.valid) {
                patternList.put(wrappedStack, new EMCCraftingPattern(wrappedStack.get()));
            } else {
                EquivalentEnergistics.logger.warn("Invalid EMC pattern detected. Item: " + I18n.format(pattern.getOutputs()[0].getItem().getUnlocalizedName(pattern.getOutputs()[0].createItemStack()) + ".name", new Object[0])  + " EMC: " + String.format("%f", pattern.outputEMC));
            }
        }
    }

    @Override
    public void onJoin(final IGridStorage arg0) {}

    @Override
    public void onSplit(final IGridStorage arg0) {}

    @Override
    public void populateGridStorage(final IGridStorage arg0) {}

}
