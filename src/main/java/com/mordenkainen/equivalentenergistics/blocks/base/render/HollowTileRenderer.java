package com.mordenkainen.equivalentenergistics.blocks.base.render;

import org.lwjgl.opengl.GL11;

import com.mordenkainen.equivalentenergistics.blocks.base.model.ModelHollowCube;

import appeng.api.implementations.parts.IPartCable;
import appeng.api.networking.IGridHost;
import appeng.api.parts.IPart;
import appeng.api.parts.IPartHost;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class HollowTileRenderer extends TileEntitySpecialRenderer {

    protected static final ModelHollowCube MODEL = new ModelHollowCube();
    
    protected void renderFrame(final ResourceLocation tex, final double x, final double y, final double z, final int metaData) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float) x, (float) y, (float) z);
        GL11.glScalef(-1F, -1F, 1F);
        bindTexture(tex);
        MODEL.render();
        GL11.glPopMatrix();
    }

    protected void renderConnectors(final TileEntity tile, final ResourceLocation tex, final double x, final double y, final double z) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float) x, (float) y, (float) z);
        GL11.glScalef(-1F, -1F, 1F);
        bindTexture(tex);
        for (final ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
            if (isCableConnected(tile.getWorldObj(), tile.xCoord, tile.yCoord, tile.zCoord, side)) {
                MODEL.renderConnector(side);
            }
        }
        GL11.glPopMatrix();
    }
    
    protected void renderLights(final IIcon tex, final double x, final double y, final double z) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float) x, (float) y, (float) z);
        
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

    protected void renderItem(final World world, final ItemStack itemStack, final float time) {
        final EntityItem entityitem = new EntityItem(world, 0.0D, 0.0D, 0.0D, itemStack);
        GL11.glRotatef((float) time % 360.0F, 0.0F, 1.0F, 0.0F);
        entityitem.hoverStart = 0.0F;
        RenderManager.instance.renderEntityWithPosYaw(entityitem, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);
    }

    protected boolean isCableConnected(final IBlockAccess world, final int x, final int y, final int z, final ForgeDirection side) {
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