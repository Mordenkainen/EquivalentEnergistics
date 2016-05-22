package com.mordenkainen.equivalentenergistics.render;

import org.lwjgl.opengl.GL11;

import com.mordenkainen.equivalentenergistics.EquivalentEnergistics;
import com.mordenkainen.equivalentenergistics.tiles.TileEMCCrafter;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.world.IBlockAccess;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class BlockEMCCrafterRenderer implements ISimpleBlockRenderingHandler {
	
	@Override
	public void renderInventoryBlock(final Block block, final int metadata, final int modelId, final RenderBlocks renderer) {
		GL11.glPushMatrix();
		GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
		TileEntityRendererDispatcher.instance.renderTileEntityAt(new TileEMCCrafter(), 0.0D, 0.0D, 0.0D, 0.0F);
		GL11.glPopMatrix();
	}

	@Override
	public boolean renderWorldBlock(final IBlockAccess world, final int x, final int y, final int z, final Block block, final int modelId, final RenderBlocks renderer) {
		return false;
	}

	@Override
	public boolean shouldRender3DInInventory(final int modelId) {
		return true;
	}

	@Override
	public int getRenderId() {
		return EquivalentEnergistics.proxy.crafterRenderer;
	}
	
}
