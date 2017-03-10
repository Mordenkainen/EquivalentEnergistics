package com.mordenkainen.equivalentenergistics.blocks.crafter.render;

import org.lwjgl.opengl.GL11;

import com.mordenkainen.equivalentenergistics.EquivalentEnergistics;
import com.mordenkainen.equivalentenergistics.blocks.crafter.tiles.TileEMCCrafter;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;

public class BlockEMCCrafterRenderer implements ISimpleBlockRenderingHandler {
    
    private static TileEntity tile = new TileEMCCrafter();
    
    @Override
    public void renderInventoryBlock(final Block block, final int metadata, final int modelId, final RenderBlocks renderer) {
        GL11.glPushMatrix();
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
        TileEntityRendererDispatcher.instance.renderTileEntityAt(tile, 0.0D, 0.0D, 0.0D, 0.0F);
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
