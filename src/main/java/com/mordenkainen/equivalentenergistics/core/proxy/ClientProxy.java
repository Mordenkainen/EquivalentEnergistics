package com.mordenkainen.equivalentenergistics.core.proxy;

import com.mordenkainen.equivalentenergistics.blocks.condenser.render.CondenserRenderer;
import com.mordenkainen.equivalentenergistics.blocks.condenser.tiles.TileEMCCondenserExt;
import com.mordenkainen.equivalentenergistics.blocks.crafter.render.CrafterRenderer;
import com.mordenkainen.equivalentenergistics.blocks.crafter.tiles.TileEMCCrafter;
import com.mordenkainen.equivalentenergistics.core.Reference;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClientProxy extends CommonProxy {

	public TextureAtlasSprite condenserInput;
	public TextureAtlasSprite condenserOutput;
	
	@Override
	public void preInit() {
		MinecraftForge.EVENT_BUS.register(this);
	}
	
    @Override
	public void init() {
		super.init();
		ClientRegistry.bindTileEntitySpecialRenderer(TileEMCCondenserExt.class, new CondenserRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEMCCrafter.class, new CrafterRenderer());
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
    public void registerItemRenderer(Item item, int meta, String name) {
    	ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(Reference.MOD_ID + ":" + name, "inventory"));
    }
    
    @Override
    public void registerItemRenderer(Item item, int meta, String name, String variant) {
    	ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(Reference.MOD_ID + ":" + name, variant));
    }
    
    @SubscribeEvent
    public void registerTextures(final TextureStitchEvent.Pre event) {
        final TextureMap map = event.getMap();
        condenserInput = map.registerSprite(new ResourceLocation(Reference.MOD_ID, "blocks/emc_condenser_input"));
        condenserOutput = map.registerSprite(new ResourceLocation(Reference.MOD_ID, "blocks/emc_condenser_output"));
    }

}
