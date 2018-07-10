package com.mordenkainen.equivalentenergistics.blocks.crafter.render;

import java.util.List;

import org.lwjgl.opengl.GL11;

import com.mordenkainen.equivalentenergistics.blocks.base.render.HollowTileRenderer;
import com.mordenkainen.equivalentenergistics.blocks.crafter.tiles.TileEMCCrafterBase;
import com.mordenkainen.equivalentenergistics.core.Reference;
import com.mordenkainen.equivalentenergistics.core.textures.TextureEnum;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

public class TileEMCCrafterRenderer extends HollowTileRenderer {

    private static final ResourceLocation[] MODELTEXTURES = {
            new ResourceLocation(Reference.MOD_ID + ":textures/models/EMCCrafter.png"),
            new ResourceLocation(Reference.MOD_ID + ":textures/models/EMCCrafterAdv.png"),
            new ResourceLocation(Reference.MOD_ID + ":textures/models/EMCCrafterExt.png"),
            new ResourceLocation(Reference.MOD_ID + ":textures/models/EMCCrafterUlt.png")
    };

    @Override
    public void renderTileEntityAt(final TileEntity tile, final double x, final double y, final double z, final float partialTicks) {
        if (!(tile instanceof TileEMCCrafterBase)) {
            return;
        }
        
        final TileEMCCrafterBase crafter = (TileEMCCrafterBase) tile;
                
        renderFrame(MODELTEXTURES[crafter.getBlockMetadata()], x, y, z, crafter.getBlockMetadata());
        
        if (crafter.getWorldObj() != null) {
            if (crafter.isActive()) {
                bindTexture(TextureMap.locationBlocksTexture);
                final IIcon tex = crafter.isErrored() ? TextureEnum.EMCASSEMBLER.getTexture(1) : TextureEnum.EMCASSEMBLER.getTexture(0);
                renderLights(tex, x, y, z);
            }
            
            renderConnectors(crafter, MODELTEXTURES[tile.getBlockMetadata()], x, y, z);
            
            renderContent(crafter, x, y, z, partialTicks);
        }
    }
    
    private void renderContent(final TileEMCCrafterBase tile, final double x, final double y, final double z, final float partialTicks) {
        final List<ItemStack> stacks = tile.getDisplayStacks();
        final float time = Minecraft.getMinecraft().renderViewEntity.ticksExisted + partialTicks;
        if(tile.isCrafting() && tile.maxJobs > 1){
            final float anglePer = 360F / tile.maxJobs;
            
            for(int i = 0; i < stacks.size(); i++) {
                final ItemStack stack = stacks.get(i);
                if (stack == null) {
                    continue;
                }
                GL11.glPushMatrix();
                GL11.glTranslatef((float) x + 0.5F, (float) y + 0.45F, (float) z + 0.5F);
                GL11.glScalef(0.5F, 0.5F, 0.5F);
                GL11.glRotatef(anglePer * i + time, 0F, 1F, 0F);
                GL11.glTranslatef(0.2F, 0F, 0.25F);
                renderItem(tile.getWorldObj(), stack, time);
                GL11.glPopMatrix();
            }
            
        } else {
            final ItemStack stack = tile.isCrafting() ? stacks.get(0) : tile.getCurrentTome();
            if(stack != null) {
                GL11.glPushMatrix();
                GL11.glTranslatef((float) x + 0.5F, (float) y + 0.3F, (float) z + 0.5F);
                renderItem(tile.getWorldObj(), stack, time);
                GL11.glPopMatrix();
            }
        }
    }

}
