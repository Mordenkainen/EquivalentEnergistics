package com.mordenkainen.equivalentenergistics.blocks.crafter.render;

import java.util.List;

import org.lwjgl.opengl.GL11;

import com.mordenkainen.equivalentenergistics.blocks.crafter.model.ModelEMCCrafter;
import com.mordenkainen.equivalentenergistics.blocks.crafter.tiles.TileEMCCrafterBase;
import com.mordenkainen.equivalentenergistics.core.Reference;
import com.mordenkainen.equivalentenergistics.core.textures.TextureEnum;

import appeng.api.implementations.parts.IPartCable;
import appeng.api.networking.IGridHost;
import appeng.api.parts.IPart;
import appeng.api.parts.IPartHost;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEMCCrafterRenderer extends TileEntitySpecialRenderer {

    private static final ModelEMCCrafter MODEL = new ModelEMCCrafter();
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
                
        renderCrafter(x, y, z, crafter.getBlockMetadata());
        
        if (crafter.getWorldObj() != null) {
            if (crafter.isActive()) {
                renderLights(crafter, x, y, z);
            }
            
            renderConnectors(crafter, x, y, z);
            
            renderContent(crafter, x, y, z, partialTicks);
        }
    }
    
    private void renderCrafter(final double x, final double y, final double z, final int metaData) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float) x, (float) y, (float) z);
        GL11.glScalef(-1F, -1F, 1F);
        bindTexture(MODELTEXTURES[metaData]);
        MODEL.render();
        GL11.glPopMatrix();
    }
    
    private void renderLights(final TileEMCCrafterBase tile, final double x, final double y, final double z) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float) x, (float) y, (float) z);
        bindTexture(TextureMap.locationBlocksTexture);
        final IIcon tex = tile.isErrored() ? TextureEnum.EMCASSEMBLER.getTexture(1) : TextureEnum.EMCASSEMBLER.getTexture(0);
        Tessellator.instance.startDrawingQuads();
        Tessellator.instance.setColorRGBA_F( 1, 1, 1, 0.3f );
        Tessellator.instance.setBrightness( 14 << 20 | 14 << 4 );
        
        Tessellator.instance.addVertexWithUV(1.0625, -0.0625, 0.9375, tex.getMaxU(), tex.getMaxV());
        Tessellator.instance.addVertexWithUV(1.0625, 1.0625, 0.9375, tex.getMaxU(), tex.getMinV());
        Tessellator.instance.addVertexWithUV(-0.0625, 1.0625, 0.9375, tex.getMinU(), tex.getMinV());
        Tessellator.instance.addVertexWithUV(-0.0625, -0.0625, 0.9375, tex.getMinU(), tex.getMaxV());
        
        Tessellator.instance.addVertexWithUV(-0.0625, -0.0625, 0.0625, tex.getMaxU(), tex.getMaxV());
        Tessellator.instance.addVertexWithUV(-0.0625, 1.0625, 0.0625, tex.getMaxU(), tex.getMinV());
        Tessellator.instance.addVertexWithUV(1.0625, 1.0625, 0.0625, tex.getMinU(), tex.getMinV());
        Tessellator.instance.addVertexWithUV(1.0625, -0.0625, 0.0625, tex.getMinU(), tex.getMaxV());
        
        Tessellator.instance.addVertexWithUV(0.9375, -0.0625, -0.0625, tex.getMaxU(), tex.getMaxV());
        Tessellator.instance.addVertexWithUV(0.9375, 1.0625, -0.0625, tex.getMaxU(), tex.getMinV());
        Tessellator.instance.addVertexWithUV(0.9375, 1.0625, 1.0625, tex.getMinU(), tex.getMinV());
        Tessellator.instance.addVertexWithUV(0.9375, -0.0625, 1.0625, tex.getMinU(), tex.getMaxV());
        
        Tessellator.instance.addVertexWithUV(0.0625, -0.0625, 1.0625, tex.getMaxU(), tex.getMaxV());
        Tessellator.instance.addVertexWithUV(0.0625, 1.0625, 1.0625, tex.getMaxU(), tex.getMinV());
        Tessellator.instance.addVertexWithUV(0.0625, 1.0625, -0.0625, tex.getMinU(), tex.getMinV());
        Tessellator.instance.addVertexWithUV(0.0625, -0.0625, -0.0625, tex.getMinU(), tex.getMaxV());
        
        Tessellator.instance.addVertexWithUV(1.0625, 0.9375, 1.0625, tex.getMaxU(), tex.getMaxV());
        Tessellator.instance.addVertexWithUV(1.0625, 0.9375, -0.0625, tex.getMaxU(), tex.getMinV());
        Tessellator.instance.addVertexWithUV(-0.0625, 0.9375, -0.0625, tex.getMinU(), tex.getMinV());
        Tessellator.instance.addVertexWithUV(-0.0625, 0.9375, 1.0625, tex.getMinU(), tex.getMaxV());
        
        Tessellator.instance.addVertexWithUV(-0.0625, 0.0625, 1.0625, tex.getMaxU(), tex.getMaxV());
        Tessellator.instance.addVertexWithUV(-0.0625, 0.0625, -0.0625, tex.getMaxU(), tex.getMinV());
        Tessellator.instance.addVertexWithUV(1.0625, 0.0625, -0.0625, tex.getMinU(), tex.getMinV());
        Tessellator.instance.addVertexWithUV(1.0625, 0.0625, 1.0625, tex.getMinU(), tex.getMaxV());
        
        Tessellator.instance.draw();
        GL11.glPopMatrix();
    }
    
    private void renderConnectors(final TileEMCCrafterBase tile, final double x, final double y, final double z) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float) x, (float) y, (float) z);
        GL11.glScalef(-1F, -1F, 1F);
        bindTexture(MODELTEXTURES[tile.getBlockMetadata()]);
        for (final ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
            if (isCableConnected(tile.getWorldObj(), tile.xCoord, tile.yCoord, tile.zCoord, side)) {
                MODEL.renderConnector(side);
            }
        }
        GL11.glPopMatrix();
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

    private void renderItem(final World world, final ItemStack itemStack, final float time) {
        final EntityItem entityitem = new EntityItem(world, 0.0D, 0.0D, 0.0D, itemStack);
        GL11.glRotatef((float) time % 360.0F, 0.0F, 1.0F, 0.0F);
        entityitem.hoverStart = 0.0F;
        RenderManager.instance.renderEntityWithPosYaw(entityitem, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);
    }

    private boolean isCableConnected(final IBlockAccess world, final int x, final int y, final int z, final ForgeDirection side) {
        final int tileYPos = y + side.offsetY;
        if (tileYPos < 0 || tileYPos > 256) {
            return false;
        }

        final TileEntity tile = world.getTileEntity(x + side.offsetX, tileYPos, z + side.offsetZ);
        if (!(tile instanceof IGridHost && tile instanceof IPartHost)) {
            return false;
        }

        final IPartHost host = (IPartHost) tile;
        final IPart part = host.getPart(ForgeDirection.UNKNOWN);
        if (part instanceof IPartCable) {
            final IPartCable cable = (IPartCable) part;
            return cable.isConnected(side.getOpposite());
        }

        return false;
    }

}
