package com.mordenkainen.equivalentenergistics.proxy;

import com.mordenkainen.equivalentenergistics.exception.ClientUnmetDependencyException;
import com.mordenkainen.equivalentenergistics.registries.BlockEnum;
import com.mordenkainen.equivalentenergistics.registries.TextureEnum;
import com.mordenkainen.equivalentenergistics.render.BlockEMCCrafterRenderer;
import com.mordenkainen.equivalentenergistics.render.TileEMCCrafterRenderer;
import com.mordenkainen.equivalentenergistics.tiles.crafter.TileEMCCrafter;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy extends CommonProxy {

    @Override
    public void preInit() {
        super.preInit();
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public boolean isClient() {
        return true;
    }

    @Override
    public boolean isServer() {
        return false;
    }

    @Override
    public void initRenderers() {
        if (BlockEnum.EMCCRAFTER.isEnabled()) {
            ClientRegistry.bindTileEntitySpecialRenderer(TileEMCCrafter.class, new TileEMCCrafterRenderer());
            crafterRenderer = RenderingRegistry.getNextAvailableRenderId();
            RenderingRegistry.registerBlockHandler(new BlockEMCCrafterRenderer());
        }
    }

    @Override
    public void unmetDependency() {
        throw new ClientUnmetDependencyException();
    }

    @SubscribeEvent
    public void registerTextures(final TextureStitchEvent.Pre event) {
        final TextureMap map = event.map;
        TextureEnum.registerTextures(map);
    }

}
