package com.mordenkainen.equivalentenergistics.blocks.provider.render;

import org.lwjgl.opengl.GL11;

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
        InternalInventory inv = tile.getInventory();
        if (!inv.isEmpty()) {
            final float time = Minecraft.getMinecraft().renderViewEntity.ticksExisted + partialTicks;
            final float anglePer = 360F / inv.getSizeInventory();
            for(int i = 0; i < inv.getSizeInventory(); i++) {
                final ItemStack stack = inv.getStackInSlot(i);
                if (stack == null) {
                    continue;
                }
                
                GL11.glPushMatrix();
                GL11.glTranslatef((float) x + 0.5F, (float) y + 0.4F, (float) z + 0.5F);
                GL11.glScalef(0.5F, 0.5F, 0.5F);
                GL11.glRotatef(anglePer * i + time, 0F, 1F, 0F);
                GL11.glTranslatef(0.2F, 0F, 0.25F);
                renderItem(tile.getWorldObj(), stack, time);
                GL11.glPopMatrix();
                
            }
        }
    }

}
