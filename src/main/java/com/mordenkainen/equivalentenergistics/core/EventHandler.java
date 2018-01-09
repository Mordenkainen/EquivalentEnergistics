package com.mordenkainen.equivalentenergistics.core;

import java.util.Deque;
import java.util.LinkedList;

import com.mordenkainen.equivalentenergistics.blocks.ModBlocks;
import com.mordenkainen.equivalentenergistics.blocks.base.tile.EqETileBase;
import com.mordenkainen.equivalentenergistics.integration.ae2.cache.crafting.EMCCraftingGrid;
import com.mordenkainen.equivalentenergistics.integration.ae2.grid.IAEProxyHost;
import com.mordenkainen.equivalentenergistics.items.ModItems;

import moze_intel.projecte.api.event.EMCRemapEvent;
import moze_intel.projecte.api.event.PlayerKnowledgeChangeEvent;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.Type;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class EventHandler {

	private EventHandler() {}
	
	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event) {
		ModItems.register(event.getRegistry());
		ModBlocks.registerItemBlocks(event.getRegistry());
	}
	
	@SubscribeEvent
	public static void registerModels(ModelRegistryEvent event) {
		ModItems.registerModels();
		ModBlocks.registerModels();
	}
	
	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> event) {
		ModBlocks.register(event.getRegistry());
	}
	
	private final static Deque<EqETileBase> tiles = new LinkedList<EqETileBase>();
    
    public static void addInit(final EqETileBase tile) {
        if(FMLCommonHandler.instance().getEffectiveSide().isServer()) {
            tiles.add(tile);
        }
    }
    
    @SubscribeEvent
    public static void onChunkLoad(final ChunkEvent.Load load) {
        for(final Object te : load.getChunk().getTileEntityMap().values()) {
            if(te instanceof IAEProxyHost) {
                ((EqETileBase) te).onChunkLoad();
            }
        }
    }
    
    @SubscribeEvent
    public static void onTick(final TickEvent ev) {
        if(ev.type == Type.SERVER && ev.phase == Phase.END) {
            while( !tiles.isEmpty() )
            {
                final EqETileBase tile = tiles.poll();
                if(!tile.isInvalid()) {
                    tile.onReady();
                }
            }
        }
    }
    
    @SubscribeEvent
    public void onPlayerKnowledgeChange(final PlayerKnowledgeChangeEvent event) {
        EMCCraftingGrid.knowledgeEvent(event.getPlayerUUID());
    }

    @SubscribeEvent
    public void onEnergyValueChange(final EMCRemapEvent event) {
        EMCCraftingGrid.energyEvent();
    }
    
}
