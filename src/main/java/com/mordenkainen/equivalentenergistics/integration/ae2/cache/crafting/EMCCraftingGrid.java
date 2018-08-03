package com.mordenkainen.equivalentenergistics.integration.ae2.cache.crafting;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

import com.google.common.base.Equivalence;
import com.google.common.base.Equivalence.Wrapper;
import com.mordenkainen.equivalentenergistics.EquivalentEnergistics;
import com.mordenkainen.equivalentenergistics.integration.Integration;
import com.mordenkainen.equivalentenergistics.integration.ae2.EMCCraftingPattern;
import com.mordenkainen.equivalentenergistics.items.ItemEnum;
import com.mordenkainen.equivalentenergistics.util.CompItemStack;

import appeng.api.networking.IGrid;
import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
import appeng.api.networking.events.MENetworkCraftingPatternChange;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

public class EMCCraftingGrid implements IEMCCraftingGrid {

    private static Equivalence<ItemStack> eq = new CompItemStack();
    private static Map<Equivalence.Wrapper<ItemStack>, EMCCraftingPattern> patternList = new HashMap<Equivalence.Wrapper<ItemStack>, EMCCraftingPattern>();
    private static int patternVer;
    private static Map<EMCCraftingGrid, Boolean> craftingGrids = new WeakHashMap<EMCCraftingGrid, Boolean>();

    private final IGrid grid;
    private final Map<Equivalence.Wrapper<ItemStack>, EMCCraftingPattern> patterns = new HashMap<Equivalence.Wrapper<ItemStack>, EMCCraftingPattern>();
    private final Map<ITransProvider, String> patternProviders = new WeakHashMap<ITransProvider, String>();
    private int lastPatternVer = -1;

    public EMCCraftingGrid(final IGrid grid) {
        this.grid = grid;
        craftingGrids.put(this, true);
    }

    @Override
    public void onUpdateTick() {
        if (lastPatternVer != patternVer) {
            updatePatterns();
            lastPatternVer = patternVer;
        }
    }

    @Override
    public void removeNode(final IGridNode gridNode, final IGridHost machine) {
        if (machine instanceof ITransProvider) {
            patternProviders.remove((ITransProvider) machine);
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
            patternProviders.put(provider, provider.getPlayerUUID());
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
            final Equivalence.Wrapper<ItemStack> wrappedStack = eq.wrap(ItemEnum.EMCCRYSTAL.getStack(64, i));
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
            if (Integration.emcHandler.hasEMC(wrappedStack.get())) {
                final EMCCraftingPattern pattern = patternList.get(wrappedStack);
                pattern.rebuildPattern();
                if (!pattern.valid) {
                    EquivalentEnergistics.logger.warn("Invalid EMC pattern detected. Item: " + StatCollector.translateToLocal(pattern.getOutputs()[0].getItem().getUnlocalizedName(pattern.getOutputs()[0].getItemStack()) + ".name") + " EMC: " + String.format("%f", pattern.outputEMC));
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
                EquivalentEnergistics.logger.warn("Invalid EMC pattern detected. Item: " + StatCollector.translateToLocal(pattern.getOutputs()[0].getItem().getUnlocalizedName(pattern.getOutputs()[0].getItemStack()) + ".name") + " EMC: " + String.format("%f", pattern.outputEMC));
            }
        }
    }
}
