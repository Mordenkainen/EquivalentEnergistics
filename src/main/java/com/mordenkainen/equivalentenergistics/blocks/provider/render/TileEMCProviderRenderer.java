package com.mordenkainen.equivalentenergistics.blocks.provider.render;

import java.util.ArrayList;
import java.util.List;

import com.mordenkainen.equivalentenergistics.blocks.base.render.HollowTileRenderer;
import com.mordenkainen.equivalentenergistics.blocks.provider.tile.TileEMCPatternProvider;
import com.mordenkainen.equivalentenergistics.core.Reference;
import com.mordenkainen.equivalentenergistics.core.textures.TextureEnum;
import com.mordenkainen.equivalentenergistics.util.inventory.InternalInventory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class TileEMCProviderRenderer extends HollowTileRenderer {

    private static final ResourceLocation MODELTEXTURE = new ResourceLocation(Reference.MOD_ID + ":textures/models/EMCProvider.png");

    @Override
    public void renderTileEntityAt(final TileEntity tile, final double x, final double y, final double z, final float partialTicks) {
        if (!(tile instanceof TileEMCPatternProvider)) {
            return;
        }
        
        final TileEMCPatternProvider provider = (TileEMCPatternProvider) tile;
                
        renderFrame(MODELTEXTURE, x, y, z, provider.getBlockMetadata());
        
        if (provider.getWorldObj() != null) {
            if (provider.isActive()) {
                bindTexture(TextureMap.locationBlocksTexture);
                renderLights(TextureEnum.EMCASSEMBLER.getTexture(0), x, y, z);
            }
            
            renderConnectors(provider, MODELTEXTURE, x, y, z);
            
            renderContent(provider, x, y, z, partialTicks);
        }
    }
    
    private void renderContent(final TileEMCPatternProvider tile, final double x, final double y, final double z, final float partialTicks) {
        final InternalInventory inv = tile.getInventory();
        if (inv.isEmpty()) {
            return;
        }
        final float time = Minecraft.getMinecraft().renderViewEntity.ticksExisted + partialTicks;
        final List<List<ItemStack>> stacks = new ArrayList<List<ItemStack>>();
        
        List<ItemStack> tmpList = new ArrayList<ItemStack>();
        for(int i = 0; i < inv.getSizeInventory(); i++) {
            if(inv.getStackInSlot(i) != null) {
                if (tmpList.size() == 8) {
                    stacks.add(tmpList);
                    tmpList = new ArrayList<ItemStack>();
                }
                tmpList.add(inv.getStackInSlot(i));
            }
        }
        stacks.add(tmpList);
        
        if (stacks.size() == 1) {
            renderRing(tile, stacks.get(0), 0.35F, x, y, z, time, false, false);
            return;
        }
        
        renderRing(tile, stacks.get(0), 0.2F, x, y, z, time, false, false);
        renderRing(tile, stacks.get(1), 0.6F, x, y, z, time, true, true);
    }

}
