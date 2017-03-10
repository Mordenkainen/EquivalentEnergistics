package com.mordenkainen.equivalentenergistics.blocks.crafter.render;

import org.lwjgl.opengl.GL11;

import com.mordenkainen.equivalentenergistics.blocks.crafter.model.ModelEMCCrafter;
import com.mordenkainen.equivalentenergistics.blocks.crafter.tiles.TileEMCCrafter;
import com.mordenkainen.equivalentenergistics.core.Reference;

import appeng.api.implementations.parts.IPartCable;
import appeng.api.networking.IGridHost;
import appeng.api.parts.IPart;
import appeng.api.parts.IPartHost;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEMCCrafterRenderer extends TileEntitySpecialRenderer {

    private static final ModelEMCCrafter MODEL = new ModelEMCCrafter();
    private static final ResourceLocation MODELTEXTURE = new ResourceLocation(Reference.MOD_ID + ":textures/models/EMCCrafter.png");

    @Override
    public void renderTileEntityAt(final TileEntity tile, final double x, final double y, final double z, final float partialTicks) {
        if (!(tile instanceof TileEMCCrafter)) {
            return;
        }
        GL11.glPushMatrix();
        GL11.glTranslatef((float) x, (float) y, (float) z);
        GL11.glScalef(-1F, -1F, 1F);
        bindTexture(MODELTEXTURE);
        MODEL.render();
        if (tile.getWorldObj() != null) {
            for (final ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
                if (isCableConnected(tile.getWorldObj(), tile.xCoord, tile.yCoord, tile.zCoord, side)) {
                    MODEL.renderConnector(side);
                }
            }
        }
        GL11.glPopMatrix();

        if (!((TileEMCCrafter) tile).displayStacks.isEmpty()) {
            final double time = Minecraft.getMinecraft().renderViewEntity.ticksExisted + partialTicks;
            if (((TileEMCCrafter) tile).displayStacks.size() > 1) {
                float[] angles = new float[((TileEMCCrafter) tile).displayStacks.size()];

                final float anglePer = 360F / ((TileEMCCrafter) tile).displayStacks.size();
                float totalAngle = 0F;
                for(int i = 0; i < angles.length; i++) {
                    angles[i] = totalAngle += anglePer;
                }
                
                for(int i = 0; i < angles.length; i++) {
                    GL11.glPushMatrix();
                    GL11.glTranslatef((float) x + 0.5F, (float) y + 0.4F, (float) z + 0.5F);
                    GL11.glScalef(0.5F, 0.5F, 0.5F);
                    GL11.glRotatef(angles[i] + (float) time, 0F, 1F, 0F);
                    GL11.glTranslatef(0.2F, 0F, 0.5F);
                    renderItem(tile.getWorldObj(), ((TileEMCCrafter) tile).displayStacks.get(i), (float) time);
                    GL11.glPopMatrix();
                }
            } else {
                GL11.glPushMatrix();
                GL11.glTranslatef((float) x + 0.5F, (float) y + 0.3F, (float) z + 0.5F);
                renderItem(tile.getWorldObj(), ((TileEMCCrafter) tile).displayStacks.get(0), (float) time);
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
            if (cable.isConnected(side.getOpposite())) {
                return true;
            }
        }

        return false;
    }

}
