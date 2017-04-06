package com.mordenkainen.equivalentenergistics.core;

import java.util.Deque;
import java.util.LinkedList;

import com.mordenkainen.equivalentenergistics.blocks.common.EqETileBase;
import com.mordenkainen.equivalentenergistics.integration.ae2.grid.IAEProxyHost;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.Type;
import net.minecraftforge.event.world.ChunkEvent;

public class TickHandler {

    public static final TickHandler INSTANCE = new TickHandler();
    
    private final Deque<EqETileBase> tiles = new LinkedList<EqETileBase>();
    
    public void addInit(final EqETileBase tile) {
        if(FMLCommonHandler.instance().getEffectiveSide().isServer()) {
            tiles.add(tile);
        }
    }
    
    @SubscribeEvent
    public void onChunkLoad(final ChunkEvent.Load load) {
        for(final Object te : load.getChunk().chunkTileEntityMap.values()) {
            if(te instanceof IAEProxyHost) {
                ((EqETileBase) te).onChunkLoad();
            }
        }
    }
    
    @SubscribeEvent
    public void onTick(final TickEvent ev) {
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
    
}
