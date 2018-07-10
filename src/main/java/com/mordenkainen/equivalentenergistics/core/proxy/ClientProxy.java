package com.mordenkainen.equivalentenergistics.core.proxy;

import com.mordenkainen.equivalentenergistics.blocks.BlockEnum;
import com.mordenkainen.equivalentenergistics.blocks.base.block.LayeredBlockRenderer;
import com.mordenkainen.equivalentenergistics.blocks.base.render.BlockWithTileRenderer;
import com.mordenkainen.equivalentenergistics.blocks.crafter.render.TileEMCCrafterRenderer;
import com.mordenkainen.equivalentenergistics.blocks.crafter.tiles.TileEMCCrafterBase;
import com.mordenkainen.equivalentenergistics.blocks.provider.render.TileEMCProviderRenderer;
import com.mordenkainen.equivalentenergistics.blocks.provider.tile.TileEMCPatternProvider;
import com.mordenkainen.equivalentenergistics.core.exceptions.ClientUnmetDependencyException;
import com.mordenkainen.equivalentenergistics.core.textures.TextureEnum;

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
            ClientRegistry.bindTileEntitySpecialRenderer(TileEMCCrafterBase.class, new TileEMCCrafterRenderer());
            crafterRenderer = RenderingRegistry.getNextAvailableRenderId();
            RenderingRegistry.registerBlockHandler(new BlockWithTileRenderer(crafterRenderer));
            layeredRenderer = RenderingRegistry.getNextAvailableRenderId();
            RenderingRegistry.registerBlockHandler(new LayeredBlockRenderer());
        }
        if (BlockEnum.EMCPROVIDER.isEnabled()) {
            ClientRegistry.bindTileEntitySpecialRenderer(TileEMCPatternProvider.class, new TileEMCProviderRenderer());
            providerRenderer = RenderingRegistry.getNextAvailableRenderId();
            RenderingRegistry.registerBlockHandler(new BlockWithTileRenderer(providerRenderer));
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
