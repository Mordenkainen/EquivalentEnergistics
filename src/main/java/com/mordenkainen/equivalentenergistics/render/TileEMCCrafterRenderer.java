package com.mordenkainen.equivalentenergistics.render;

import org.lwjgl.opengl.GL11;

import appeng.api.implementations.parts.IPartCable;
import appeng.api.networking.IGridHost;
import appeng.api.parts.IPart;
import appeng.api.parts.IPartHost;

import com.mordenkainen.equivalentenergistics.lib.Reference;
import com.mordenkainen.equivalentenergistics.models.ModelEMCCrafter;
import com.mordenkainen.equivalentenergistics.tiles.TileEMCCrafter;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;

import net.minecraftforge.common.util.ForgeDirection;

public class TileEMCCrafterRenderer extends TileEntitySpecialRenderer {

	private static final ModelEMCCrafter MODEL = new ModelEMCCrafter();
	private static final ResourceLocation MODELTEXTURE = new ResourceLocation(Reference.MOD_ID + ":textures/models/EMCCrafter.png");

	@Override
	public void renderTileEntityAt(final TileEntity tile, final double x, final double y, final double z, final float partialTicks) {
		if (tile instanceof TileEMCCrafter) {
			GL11.glPushMatrix();
			GL11.glTranslatef((float)x, (float)y, (float)z);
			GL11.glScalef(-1F, -1F, 1F);
			GL11.glTranslatef(-.5F, -1.5F, .5F);
			bindTexture(MODELTEXTURE);
			MODEL.render();
			if(tile.getWorldObj() != null) {
				for(final ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
					if(isCableConnected(tile.getWorldObj(), tile.xCoord, tile.yCoord, tile.zCoord, side)) {
						MODEL.renderConnector(side);
					}
				}
			}
			GL11.glPopMatrix();
			
			EntityItem entityitem = null;
			
			if(((TileEMCCrafter)tile).displayStack != null) {
				 entityitem = new EntityItem(tile.getWorldObj(), 0.0D, 0.0D, 0.0D, ((TileEMCCrafter)tile).displayStack);
			}
			
			if(entityitem != null) {
				final float ticks = (float) (Minecraft.getMinecraft().renderViewEntity.ticksExisted + partialTicks + x);
				GL11.glPushMatrix();
				GL11.glTranslatef((float)x + 0.5f, (float)y + 0.3f, (float)z + 0.5f);
				GL11.glRotatef(ticks % 360.0F, 0.0F, 1.0F, 0.0F);
				entityitem.hoverStart = 0.0F;
			    RenderManager.instance.renderEntityWithPosYaw(entityitem, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);
			    GL11.glPopMatrix();
			}
		}
	}
	
	private boolean isCableConnected(final IBlockAccess world, final int x, final int y, final int z, final ForgeDirection side) {
		final int tileYPos = y + side.offsetY;
		if (-1 < tileYPos && tileYPos < 256) {
			final TileEntity tile = world.getTileEntity(x + side.offsetX, tileYPos, z + side.offsetZ);
			if (tile instanceof IGridHost && tile instanceof IPartHost) {
				final IPartHost host = (IPartHost)tile;
				final IPart part = host.getPart(ForgeDirection.UNKNOWN);
				if (part instanceof IPartCable) {
					final IPartCable cable = (IPartCable)part;
					if (cable.isConnected(side.getOpposite())) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
}
