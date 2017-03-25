// TODO: Partial block rendering?
package com.mordenkainen.equivalentenergistics.blocks;

import java.util.EnumSet;

import org.lwjgl.opengl.GL11;

import com.mordenkainen.equivalentenergistics.EquivalentEnergistics;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

public class LayeredBlockRenderer implements ISimpleBlockRenderingHandler {
    
    @Override
    public void renderInventoryBlock(final Block block, final int metadata, final int modelId, final RenderBlocks renderer) {
        final Tessellator tessellator = Tessellator.instance;
        float f1;
        float f2;
        float f3;

        if (renderer.useInventoryTint) {
            final int j = block.getRenderColor(metadata);

            f1 = (float) (j >> 16 & 255) / 255.0F;
            f2 = (float) (j >> 8 & 255) / 255.0F;
            f3 = (float) (j & 255) / 255.0F;
            GL11.glColor4f(f1, f2, f3, 1.0F);
        }

        block.setBlockBoundsForItemRender();
        renderer.setRenderBoundsFromBlock(block);
        GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);

        for (final ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            tessellator.startDrawingQuads();
            tessellator.setNormal(dir.offsetX, dir.offsetY, dir.offsetZ);
            renderFaceWithLayers(renderer, block, metadata, dir, 0.0D, 0.0D, 0.0D, true);
            tessellator.draw();
        }

        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
    }

    @Override
    public boolean renderWorldBlock(final IBlockAccess world, final int x, final int y, final int z, final Block block, final int modelId, final RenderBlocks renderer) {
        final int color = block.colorMultiplier(world, x, y, z);
        final int meta = world.getBlockMetadata(x, y, z);
        float r = (float) (color >> 16 & 255) / 255.0F;
        float g = (float) (color >> 8 & 255) / 255.0F;
        float b = (float) (color & 255) / 255.0F;

        if (EntityRenderer.anaglyphEnable) {
            final float r3d = (r * 30.0F + g * 59.0F + b * 11.0F) / 100.0F;
            final float g3d = (r * 30.0F + g * 70.0F) / 100.0F;
            final float b3d = (r * 30.0F + b * 70.0F) / 100.0F;
            r = r3d;
            g = g3d;
            b = b3d;
        }

        return Minecraft.isAmbientOcclusionEnabled() && block.getLightValue() == 0 ? renderLayeredBlockWithAmbientOcclusion(renderer, block, meta, x, y, z, r, g, b) : renderLayeredBlockWithColorMultiplier(renderer, block, meta, x, y, z, r, g, b);
    }

    @Override
    public boolean shouldRender3DInInventory(final int modelId) {
        return true;
    }

    @Override
    public int getRenderId() {
        return EquivalentEnergistics.proxy.layeredRenderer;
    }
    
    private boolean renderLayeredBlockWithAmbientOcclusion(final RenderBlocks renderer, final Block block, final int metadata, final int x, final int y, final int z, final float r, final float g, final float b) { // NOPMD
        boolean rendered = false;
        renderer.enableAO = true;
        final int blockBrightness = block.getMixedBrightnessForBlock(renderer.blockAccess, x, y, z);
        Tessellator.instance.setBrightness(983055);
        
        final float[] faceBrightnesses = {0.5F, 1.0F, 0.8F, 0.8F, 0.6F, 0.6F};
        final boolean boundryTest[] = {renderer.renderMinY <= 0.0D, renderer.renderMaxY >= 1.0D, renderer.renderMinZ <= 0.0D, renderer.renderMaxZ >= 1.0D, renderer.renderMinX <= 0.0D, renderer.renderMaxX >= 1.0D};
        final int corners[][][] = {{{-1, -1, -1}, {1, -1, -1}, {1, -1, 1}, {-1, -1, 1}},
                            {{-1, 1, -1}, {1, 1, -1}, {1, 1, 1}, {-1, 1, 1}},
                            {{-1, -1, -1}, {1, -1, -1}, {1, 1, -1}, {-1, 1, -1}},
                            {{-1, -1, 1}, {1, -1, 1}, {1, -1, 1}, {-1, -1, 1}},
                            {{-1, -1, -1}, {-1, 1, -1}, {-1, 1, 1}, {-1, -1, 1}},
                            {{1, -1, -1}, {1, 1, -1}, {1, 1, 1}, {1, -1, 1}}};
        
        for (final ForgeDirection face : ForgeDirection.VALID_DIRECTIONS) {
            if (renderer.renderAllFaces || block.shouldSideBeRendered(renderer.blockAccess, x + face.offsetX, y + face.offsetY, z +face.offsetZ, face.ordinal())) {
                int offset = 0;
                if (boundryTest[face.ordinal()]) {
                    offset = 1;
                }
                int brightnesses[] = new int[8];
                float occlusionValues[] = new float[8];
                boolean transparencies[] = new boolean[4];
                
                final ForgeDirection sides[] = EnumSet.complementOf(EnumSet.of(face, face.getOpposite(), ForgeDirection.UNKNOWN)).toArray(new ForgeDirection[0]);
                for (int i = 0; i < 4; i++) {
                    final int x1 = x + sides[i].offsetX + (face.offsetX * offset);
                    final int y1 = y + sides[i].offsetY + (face.offsetY * offset);
                    final int z1 = z + sides[i].offsetZ + (face.offsetZ * offset);
                    brightnesses[i] = block.getMixedBrightnessForBlock(renderer.blockAccess, x1 , y1, z1);
                    occlusionValues[i] = renderer.blockAccess.getBlock(x1, y1, z1).getAmbientOcclusionLightValue();
                    transparencies[i] = renderer.blockAccess.getBlock(x1, y1, z1).getCanBlockGrass();
                }
                
                if (transparencies[0] || transparencies[2]) {
                    brightnesses[4] = block.getMixedBrightnessForBlock(renderer.blockAccess, x + corners[face.ordinal()][0][0], y + corners[face.ordinal()][0][1], z + corners[face.ordinal()][0][2]);
                    occlusionValues[4] = renderer.blockAccess.getBlock(x + corners[face.ordinal()][0][0], y + corners[face.ordinal()][0][1], z + corners[face.ordinal()][0][2]).getAmbientOcclusionLightValue();
                } else {
                    brightnesses[4] = (brightnesses[0] + brightnesses[2]) / 2;
                    occlusionValues[4] = (occlusionValues[0] + occlusionValues[2]) / 2.0F;
                }
                
                if (transparencies[1] || transparencies[2]) {
                    brightnesses[5] = block.getMixedBrightnessForBlock(renderer.blockAccess, x + corners[face.ordinal()][1][0], y + corners[face.ordinal()][1][1], z + corners[face.ordinal()][1][2]);
                    occlusionValues[5] = renderer.blockAccess.getBlock(x + corners[face.ordinal()][1][0], y + corners[face.ordinal()][1][1], z + corners[face.ordinal()][1][2]).getAmbientOcclusionLightValue();
                } else {
                    brightnesses[5] = (brightnesses[1] + brightnesses[2]) / 2;
                    occlusionValues[5] = (occlusionValues[1] + occlusionValues[2]) / 2.0F;
                }
                
                if (transparencies[1] || transparencies[3]) {
                    brightnesses[6] = block.getMixedBrightnessForBlock(renderer.blockAccess, x + corners[face.ordinal()][2][0], y + corners[face.ordinal()][2][1], z + corners[face.ordinal()][2][2]);
                    occlusionValues[6] = renderer.blockAccess.getBlock(x + corners[face.ordinal()][2][0], y + corners[face.ordinal()][2][1], z + corners[face.ordinal()][2][2]).getAmbientOcclusionLightValue();
                } else {
                    brightnesses[6] = (brightnesses[1] + brightnesses[3]) / 2;
                    occlusionValues[6] = (occlusionValues[1] + occlusionValues[3]) / 2.0F;
                }
                
                if (transparencies[0] || transparencies[3]) {
                    brightnesses[7] = block.getMixedBrightnessForBlock(renderer.blockAccess, x + corners[face.ordinal()][3][0],  y + corners[face.ordinal()][3][1], z + corners[face.ordinal()][3][2]);
                    occlusionValues[7] = renderer.blockAccess.getBlock(x + corners[face.ordinal()][3][0], y + corners[face.ordinal()][3][1], z + corners[face.ordinal()][3][2]).getAmbientOcclusionLightValue();
                } else {
                    brightnesses[7] = (brightnesses[0] + brightnesses[3]) / 2;
                    occlusionValues[7] = (occlusionValues[0] + occlusionValues[3]) / 2.0F;
                }
                
                int faceBrightness = blockBrightness;

                if (boundryTest[face.ordinal()] || !renderer.blockAccess.getBlock(x + face.offsetX, y + face.offsetY, z + face.offsetZ).isOpaqueCube()) {
                    faceBrightness = block.getMixedBrightnessForBlock(renderer.blockAccess, x + face.offsetX, y + face.offsetY, z + face.offsetZ);
                }
                
                final float faceocclusion = renderer.blockAccess.getBlock(x + face.offsetX, y + face.offsetY, z + face.offsetZ).getAmbientOcclusionLightValue();
                
                final float lightValueTL = (faceocclusion + occlusionValues[0] + occlusionValues[7]+ occlusionValues[3]) / 4.0F;
                final float lightValueBL = (faceocclusion + occlusionValues[0] + occlusionValues[4]+ occlusionValues[2]) / 4.0F;
                final float lightValueBR = (faceocclusion + occlusionValues[1] + occlusionValues[6]+ occlusionValues[3]) / 4.0F;
                final float lightValueTR = (faceocclusion + occlusionValues[1] + occlusionValues[5]+ occlusionValues[2]) / 4.0F;
                
                renderer.brightnessTopLeft = renderer.getAoBrightness(brightnesses[0], brightnesses[7], brightnesses[3], faceBrightness);
                renderer.brightnessBottomLeft = renderer.getAoBrightness(brightnesses[0], brightnesses[4], brightnesses[2], faceBrightness);
                renderer.brightnessBottomRight = renderer.getAoBrightness(brightnesses[1], brightnesses[6], brightnesses[3], faceBrightness);
                renderer.brightnessTopRight = renderer.getAoBrightness(brightnesses[1], brightnesses[5], brightnesses[2], faceBrightness);
                
                setRendererColors(renderer, r, g, b,faceBrightnesses[face.ordinal()], lightValueTL, lightValueBL, lightValueBR, lightValueTR, !renderer.hasOverrideBlockTexture());
                
                renderFaceWithLayers(renderer, block, metadata, face, (double) x, (double) y, (double) z, false);
                
                rendered = true;
            }
        }
        
        renderer.enableAO = false;
        return rendered;
    }

    private void setRendererColors(final RenderBlocks renderer, final float r, final float g, final float b, final float sideMult, final float multTopLeft, final float multBotLeft, final float multBotRight, final float multTopRight, final boolean flag) {
        if (flag) {
            renderer.colorRedTopLeft = renderer.colorRedBottomLeft = renderer.colorRedBottomRight = renderer.colorRedTopRight = r * sideMult;
            renderer.colorGreenTopLeft = renderer.colorGreenBottomLeft = renderer.colorGreenBottomRight = renderer.colorGreenTopRight = g * sideMult;
            renderer.colorBlueTopLeft = renderer.colorBlueBottomLeft = renderer.colorBlueBottomRight = renderer.colorBlueTopRight = b * sideMult;
        } else {
            renderer.colorRedTopLeft = renderer.colorRedBottomLeft = renderer.colorRedBottomRight = renderer.colorRedTopRight = sideMult;
            renderer.colorGreenTopLeft = renderer.colorGreenBottomLeft = renderer.colorGreenBottomRight = renderer.colorGreenTopRight = sideMult;
            renderer.colorBlueTopLeft = renderer.colorBlueBottomLeft = renderer.colorBlueBottomRight = renderer.colorBlueTopRight = sideMult;
        }

        renderer.colorRedTopLeft *= multTopLeft;
        renderer.colorGreenTopLeft *= multTopLeft;
        renderer.colorBlueTopLeft *= multTopLeft;
        renderer.colorRedBottomLeft *= multBotLeft;
        renderer.colorGreenBottomLeft *= multBotLeft;
        renderer.colorBlueBottomLeft *= multBotLeft;
        renderer.colorRedBottomRight *= multBotRight;
        renderer.colorGreenBottomRight *= multBotRight;
        renderer.colorBlueBottomRight *= multBotRight;
        renderer.colorRedTopRight *= multTopRight;
        renderer.colorGreenTopRight *= multTopRight;
        renderer.colorBlueTopRight *= multTopRight;
    }
    
    private boolean renderLayeredBlockWithColorMultiplier(final RenderBlocks renderer, final Block block, final int metadata, final int x, final int y, final int z, final float r, final float g, final float b) {
        renderer.enableAO = false;
        final Tessellator tessellator = Tessellator.instance;
        boolean flag = false;

        final int l = block.getMixedBrightnessForBlock(renderer.blockAccess, x, y, z);
        
        final boolean[] blockTests = {renderer.renderMinY > 0.0D, renderer.renderMaxY < 1.0D, renderer.renderMinZ > 0.0D, renderer.renderMaxZ < 1.0D, renderer.renderMinX > 0.0D, renderer.renderMaxX < 1.0D};
        final float[] brightnesses = {0.5F, 1.0F, 0.8F, 0.8F, 0.6F, 0.6F};

        for (final ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            if (renderer.renderAllFaces || block.shouldSideBeRendered(renderer.blockAccess, x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ, dir.ordinal())) {
                tessellator.setBrightness(blockTests[dir.ordinal()] ? l : block.getMixedBrightnessForBlock(renderer.blockAccess, x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ));
                final float brightness = brightnesses[dir.ordinal()];
                tessellator.setColorOpaque_F(brightness * r, brightness * g, brightness * b);
                renderFaceWithLayers(renderer, block, metadata, dir, (double) x, (double) y, (double) z, false);
                flag = true;
            }
        }

        return flag;
    }
    
    private void renderFaceWithLayers(final RenderBlocks renderer, final Block block, final int metadata, final ForgeDirection dir, final double x, final double y, final double z, final boolean inv) {
        final ILayeredBlock layers = (ILayeredBlock) block;
        renderFaceByDir(renderer, block, dir, x, y, z, renderer.getBlockIconFromSideAndMetadata(block, dir.ordinal(), metadata));
        final int numLayers = inv ? layers.numLayers(block, metadata) : layers.numLayers(renderer.blockAccess, block, (int) x, (int) y, (int) z, metadata);
        for (int i = 1; i <= numLayers; i++) {
            final IIcon layer = inv ? layers.getLayer(block, dir.ordinal(), metadata, i) : layers.getLayer(renderer.blockAccess, block, (int) x, (int) y, (int) z, dir.ordinal(), metadata, i);
            if (layer != null) {
                renderFaceByDir(renderer, block, dir, x + ((dir.offsetX * i) * 0.00001D), y + ((dir.offsetY * i) * 0.00001D), z + ((dir.offsetZ * i) * 0.00001D), layer);
            }
        }
    }
    
    private void renderFaceByDir(final RenderBlocks renderer, final Block block, final ForgeDirection dir, final double x, final double y, final double z, final IIcon icon) {
        switch (dir) {
            case DOWN:
                renderer.renderFaceYNeg(block, x, y, z, icon);
                break;
            case UP:
                renderer.renderFaceYPos(block, x, y, z, icon);
                break;
            case NORTH:
                renderer.renderFaceZNeg(block, x, y, z, icon);
                break;
            case SOUTH:
                renderer.renderFaceZPos(block, x, y, z, icon);
                break;
            case WEST:
                renderer.renderFaceXNeg(block, x, y, z, icon);
                break;
            case EAST:
                renderer.renderFaceXPos(block, x, y, z, icon);
                break;
            default:
                break;
        }
    }

}
