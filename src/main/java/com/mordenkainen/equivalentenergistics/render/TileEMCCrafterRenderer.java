package com.mordenkainen.equivalentenergistics.render;

import org.lwjgl.opengl.GL11;

import appeng.api.implementations.parts.IPartCable;
import appeng.api.networking.IGridHost;
import appeng.api.parts.IPart;
import appeng.api.parts.IPartHost;

import com.mordenkainen.equivalentenergistics.lib.Ref;
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

	ModelEMCCrafter model = new ModelEMCCrafter();
	private static final ResourceLocation modelTexture = new ResourceLocation(Ref.getId("textures/models/EMCCrafter.png"));

	@Override
	public void renderTileEntityAt(TileEntity te, double x, double y, double z, float t) {
		if (te instanceof TileEMCCrafter) {
			GL11.glPushMatrix();
			GL11.glTranslatef((float)x, (float)y, (float)z);
			GL11.glScalef(-1F, -1F, 1F);
			GL11.glTranslatef(-.5F, -1.5F, .5F);
			bindTexture(modelTexture);
			model.render();
			if(te.getWorldObj() != null) {
				for(ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
					if(isCableConnected(te.getWorldObj(), te.xCoord, te.yCoord, te.zCoord, side)) {
						model.renderConnector(side);
					}
				}
			}
			GL11.glPopMatrix();
			
			EntityItem entityitem = null;
			
			if(((TileEMCCrafter)te).displayStack != null) {
				 entityitem = new EntityItem(te.getWorldObj(), 0.0D, 0.0D, 0.0D, ((TileEMCCrafter)te).displayStack);
			}
			
			if(entityitem != null) {
				float ticks = (float) (Minecraft.getMinecraft().renderViewEntity.ticksExisted + t + x);
				GL11.glPushMatrix();
				GL11.glTranslatef((float)x + 0.5f, (float)y + 0.3f, (float)z + 0.5f);
				GL11.glRotatef(ticks % 360.0F, 0.0F, 1.0F, 0.0F);
				entityitem.hoverStart = 0.0F;
			    RenderManager.instance.renderEntityWithPosYaw(entityitem, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);
			    GL11.glPopMatrix();
			}
		}
	}
	
	boolean isCableConnected(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
		int tileYPos = y + side.offsetY;
		if ((-1 < tileYPos) && (tileYPos < 256)) {
			TileEntity ne = world.getTileEntity(x + side.offsetX, tileYPos, z + side.offsetZ);
			if (((ne instanceof IGridHost)) && ((ne instanceof IPartHost))) {
				IPartHost ph = (IPartHost)ne;
				IPart pcx = ph.getPart(ForgeDirection.UNKNOWN);
				if ((pcx instanceof IPartCable)) {
					IPartCable pc = (IPartCable)pcx;
					if (pc.isConnected(side.getOpposite())) {
						return true;
					}
				}
			}
		}
		return false;
	}
}
