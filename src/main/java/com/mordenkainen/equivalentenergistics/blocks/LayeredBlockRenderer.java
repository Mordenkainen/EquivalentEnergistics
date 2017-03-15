package com.mordenkainen.equivalentenergistics.blocks;

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
            switch (dir) {
                case DOWN:
                    renderFaceYNegInv(renderer, block, metadata);
                    break;
                case UP:
                    renderFaceYPosInv(renderer, block, metadata);
                    break;
                case NORTH:
                    renderFaceZNegInv(renderer, block, metadata);
                    break;
                case SOUTH:
                    renderFaceZPosInv(renderer, block, metadata);
                    break;
                case WEST:
                    renderFaceXNegInv(renderer, block, metadata);
                    break;
                case EAST:
                    renderFaceXPosInv(renderer, block, metadata);
                    break;
                default:
                    break;
            }
            tessellator.draw();
        }

        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
    }

    @Override
    public boolean renderWorldBlock(final IBlockAccess world, final int x, final int y, final int z, final Block block, final int modelId, final RenderBlocks renderer) {
        final int l = block.colorMultiplier(world, x, y, z);
        final int meta = world.getBlockMetadata(x, y, z);
        float f = (float) (l >> 16 & 255) / 255.0F;
        float f1 = (float) (l >> 8 & 255) / 255.0F;
        float f2 = (float) (l & 255) / 255.0F;

        if (EntityRenderer.anaglyphEnable) {
            final float f3 = (f * 30.0F + f1 * 59.0F + f2 * 11.0F) / 100.0F;
            final float f4 = (f * 30.0F + f1 * 70.0F) / 100.0F;
            final float f5 = (f * 30.0F + f2 * 70.0F) / 100.0F;
            f = f3;
            f1 = f4;
            f2 = f5;
        }

        return Minecraft.isAmbientOcclusionEnabled() && block.getLightValue() == 0 ? (renderer.partialRenderBounds ? renderLayeredBlockWithAmbientOcclusionPartial(renderer, block, meta, x, y, z, f, f1, f2) : renderLayeredBlockWithAmbientOcclusion(renderer, block, meta, x, y, z, f, f1, f2)) : renderLayeredBlockWithColorMultiplier(renderer, block, meta, x, y, z, f, f1, f2);
    }

    @Override
    public boolean shouldRender3DInInventory(final int modelId) {
        return true;
    }

    @Override
    public int getRenderId() {
        return EquivalentEnergistics.proxy.layeredRenderer;
    }

    private boolean renderLayeredBlockWithAmbientOcclusion(final RenderBlocks renderer, final Block block, final int metadata, final int x, final int y, final int z, final float r, final float g, final float b) {
        int x1 = x;
        int y1 = y;
        int z1 = z;
        renderer.enableAO = true;
        boolean flag = false;
        float f3 = 0.0F;
        float f4 = 0.0F;
        float f5 = 0.0F;
        float f6 = 0.0F;
        boolean flag1 = true;
        final int l = block.getMixedBrightnessForBlock(renderer.blockAccess, x1, y1, z1);
        final Tessellator tessellator = Tessellator.instance;
        tessellator.setBrightness(983055);

        if (renderer.hasOverrideBlockTexture()) {
            flag1 = false;
        }

        boolean flag2;
        boolean flag3;
        boolean flag4;
        boolean flag5;
        int i1;
        float f7;

        if (renderer.renderAllFaces || block.shouldSideBeRendered(renderer.blockAccess, x1, y1 - 1, z1, 0)) {
            if (renderer.renderMinY <= 0.0D) {
                --y1;
            }

            renderer.aoBrightnessXYNN = block.getMixedBrightnessForBlock(renderer.blockAccess, x1 - 1, y1, z1);
            renderer.aoBrightnessYZNN = block.getMixedBrightnessForBlock(renderer.blockAccess, x1, y1, z1 - 1);
            renderer.aoBrightnessYZNP = block.getMixedBrightnessForBlock(renderer.blockAccess, x1, y1, z1 + 1);
            renderer.aoBrightnessXYPN = block.getMixedBrightnessForBlock(renderer.blockAccess, x1 + 1, y1, z1);
            renderer.aoLightValueScratchXYNN = renderer.blockAccess.getBlock(x1 - 1, y1, z1).getAmbientOcclusionLightValue();
            renderer.aoLightValueScratchYZNN = renderer.blockAccess.getBlock(x1, y1, z1 - 1).getAmbientOcclusionLightValue();
            renderer.aoLightValueScratchYZNP = renderer.blockAccess.getBlock(x1, y1, z1 + 1).getAmbientOcclusionLightValue();
            renderer.aoLightValueScratchXYPN = renderer.blockAccess.getBlock(x1 + 1, y1, z1).getAmbientOcclusionLightValue();
            flag2 = renderer.blockAccess.getBlock(x1 + 1, y1 - 1, z1).getCanBlockGrass();
            flag3 = renderer.blockAccess.getBlock(x1 - 1, y1 - 1, z1).getCanBlockGrass();
            flag4 = renderer.blockAccess.getBlock(x1, y1 - 1, z1 + 1).getCanBlockGrass();
            flag5 = renderer.blockAccess.getBlock(x1, y1 - 1, z1 - 1).getCanBlockGrass();

            if (flag5 || flag3) {
                renderer.aoLightValueScratchXYZNNN = renderer.blockAccess.getBlock(x1 - 1, y1, z1 - 1).getAmbientOcclusionLightValue();
                renderer.aoBrightnessXYZNNN = block.getMixedBrightnessForBlock(renderer.blockAccess, x1 - 1, y1, z1 - 1);
            } else {
                renderer.aoLightValueScratchXYZNNN = renderer.aoLightValueScratchXYNN;
                renderer.aoBrightnessXYZNNN = renderer.aoBrightnessXYNN;
            }

            if (flag4 || flag3) {
                renderer.aoLightValueScratchXYZNNP = renderer.blockAccess.getBlock(x1 - 1, y1, z1 + 1).getAmbientOcclusionLightValue();
                renderer.aoBrightnessXYZNNP = block.getMixedBrightnessForBlock(renderer.blockAccess, x1 - 1, y1, z1 + 1);
            } else {
                renderer.aoLightValueScratchXYZNNP = renderer.aoLightValueScratchXYNN;
                renderer.aoBrightnessXYZNNP = renderer.aoBrightnessXYNN;
            }

            if (flag5 || flag2) {
                renderer.aoLightValueScratchXYZPNN = renderer.blockAccess.getBlock(x1 + 1, y1, z1 - 1).getAmbientOcclusionLightValue();
                renderer.aoBrightnessXYZPNN = block.getMixedBrightnessForBlock(renderer.blockAccess, x1 + 1, y1, z1 - 1);
            } else {
                renderer.aoLightValueScratchXYZPNN = renderer.aoLightValueScratchXYPN;
                renderer.aoBrightnessXYZPNN = renderer.aoBrightnessXYPN;
            }

            if (flag4 || flag2) {
                renderer.aoLightValueScratchXYZPNP = renderer.blockAccess.getBlock(x1 + 1, y1, z1 + 1).getAmbientOcclusionLightValue();
                renderer.aoBrightnessXYZPNP = block.getMixedBrightnessForBlock(renderer.blockAccess, x1 + 1, y1, z1 + 1);
            } else {
                renderer.aoLightValueScratchXYZPNP = renderer.aoLightValueScratchXYPN;
                renderer.aoBrightnessXYZPNP = renderer.aoBrightnessXYPN;
            }

            if (renderer.renderMinY <= 0.0D) {
                ++y1;
            }

            i1 = l;

            if (renderer.renderMinY <= 0.0D || !renderer.blockAccess.getBlock(x1, y1 - 1, z1).isOpaqueCube()) {
                i1 = block.getMixedBrightnessForBlock(renderer.blockAccess, x1, y1 - 1, z1);
            }

            f7 = renderer.blockAccess.getBlock(x1, y1 - 1, z1).getAmbientOcclusionLightValue();
            f3 = (renderer.aoLightValueScratchXYZNNP + renderer.aoLightValueScratchXYNN + renderer.aoLightValueScratchYZNP + f7) / 4.0F;
            f6 = (renderer.aoLightValueScratchYZNP + f7 + renderer.aoLightValueScratchXYZPNP + renderer.aoLightValueScratchXYPN) / 4.0F;
            f5 = (f7 + renderer.aoLightValueScratchYZNN + renderer.aoLightValueScratchXYPN + renderer.aoLightValueScratchXYZPNN) / 4.0F;
            f4 = (renderer.aoLightValueScratchXYNN + renderer.aoLightValueScratchXYZNNN + f7 + renderer.aoLightValueScratchYZNN) / 4.0F;
            renderer.brightnessTopLeft = renderer.getAoBrightness(renderer.aoBrightnessXYZNNP, renderer.aoBrightnessXYNN, renderer.aoBrightnessYZNP, i1);
            renderer.brightnessTopRight = renderer.getAoBrightness(renderer.aoBrightnessYZNP, renderer.aoBrightnessXYZPNP, renderer.aoBrightnessXYPN, i1);
            renderer.brightnessBottomRight = renderer.getAoBrightness(renderer.aoBrightnessYZNN, renderer.aoBrightnessXYPN, renderer.aoBrightnessXYZPNN, i1);
            renderer.brightnessBottomLeft = renderer.getAoBrightness(renderer.aoBrightnessXYNN, renderer.aoBrightnessXYZNNN, renderer.aoBrightnessYZNN, i1);

            if (flag1) {
                renderer.colorRedTopLeft = renderer.colorRedBottomLeft = renderer.colorRedBottomRight = renderer.colorRedTopRight = r * 0.5F;
                renderer.colorGreenTopLeft = renderer.colorGreenBottomLeft = renderer.colorGreenBottomRight = renderer.colorGreenTopRight = g * 0.5F;
                renderer.colorBlueTopLeft = renderer.colorBlueBottomLeft = renderer.colorBlueBottomRight = renderer.colorBlueTopRight = b * 0.5F;
            } else {
                renderer.colorRedTopLeft = renderer.colorRedBottomLeft = renderer.colorRedBottomRight = renderer.colorRedTopRight = 0.5F;
                renderer.colorGreenTopLeft = renderer.colorGreenBottomLeft = renderer.colorGreenBottomRight = renderer.colorGreenTopRight = 0.5F;
                renderer.colorBlueTopLeft = renderer.colorBlueBottomLeft = renderer.colorBlueBottomRight = renderer.colorBlueTopRight = 0.5F;
            }

            renderer.colorRedTopLeft *= f3;
            renderer.colorGreenTopLeft *= f3;
            renderer.colorBlueTopLeft *= f3;
            renderer.colorRedBottomLeft *= f4;
            renderer.colorGreenBottomLeft *= f4;
            renderer.colorBlueBottomLeft *= f4;
            renderer.colorRedBottomRight *= f5;
            renderer.colorGreenBottomRight *= f5;
            renderer.colorBlueBottomRight *= f5;
            renderer.colorRedTopRight *= f6;
            renderer.colorGreenTopRight *= f6;
            renderer.colorBlueTopRight *= f6;
            renderFaceYNeg(renderer, block, metadata, (double) x1, (double) y1, (double) z1);
            flag = true;
        }

        if (renderer.renderAllFaces || block.shouldSideBeRendered(renderer.blockAccess, x1, y1 + 1, z1, 1)) {
            if (renderer.renderMaxY >= 1.0D) {
                ++y1;
            }

            renderer.aoBrightnessXYNP = block.getMixedBrightnessForBlock(renderer.blockAccess, x1 - 1, y1, z1);
            renderer.aoBrightnessXYPP = block.getMixedBrightnessForBlock(renderer.blockAccess, x1 + 1, y1, z1);
            renderer.aoBrightnessYZPN = block.getMixedBrightnessForBlock(renderer.blockAccess, x1, y1, z1 - 1);
            renderer.aoBrightnessYZPP = block.getMixedBrightnessForBlock(renderer.blockAccess, x1, y1, z1 + 1);
            renderer.aoLightValueScratchXYNP = renderer.blockAccess.getBlock(x1 - 1, y1, z1).getAmbientOcclusionLightValue();
            renderer.aoLightValueScratchXYPP = renderer.blockAccess.getBlock(x1 + 1, y1, z1).getAmbientOcclusionLightValue();
            renderer.aoLightValueScratchYZPN = renderer.blockAccess.getBlock(x1, y1, z1 - 1).getAmbientOcclusionLightValue();
            renderer.aoLightValueScratchYZPP = renderer.blockAccess.getBlock(x1, y1, z1 + 1).getAmbientOcclusionLightValue();
            flag2 = renderer.blockAccess.getBlock(x1 + 1, y1 + 1, z1).getCanBlockGrass();
            flag3 = renderer.blockAccess.getBlock(x1 - 1, y1 + 1, z1).getCanBlockGrass();
            flag4 = renderer.blockAccess.getBlock(x1, y1 + 1, z1 + 1).getCanBlockGrass();
            flag5 = renderer.blockAccess.getBlock(x1, y1 + 1, z1 - 1).getCanBlockGrass();

            if (flag5 || flag3) {
                renderer.aoLightValueScratchXYZNPN = renderer.blockAccess.getBlock(x1 - 1, y1, z1 - 1).getAmbientOcclusionLightValue();
                renderer.aoBrightnessXYZNPN = block.getMixedBrightnessForBlock(renderer.blockAccess, x1 - 1, y1, z1 - 1);
            } else {
                renderer.aoLightValueScratchXYZNPN = renderer.aoLightValueScratchXYNP;
                renderer.aoBrightnessXYZNPN = renderer.aoBrightnessXYNP;
            }

            if (flag5 || flag2) {
                renderer.aoLightValueScratchXYZPPN = renderer.blockAccess.getBlock(x1 + 1, y1, z1 - 1).getAmbientOcclusionLightValue();
                renderer.aoBrightnessXYZPPN = block.getMixedBrightnessForBlock(renderer.blockAccess, x1 + 1, y1, z1 - 1);
            } else {
                renderer.aoLightValueScratchXYZPPN = renderer.aoLightValueScratchXYPP;
                renderer.aoBrightnessXYZPPN = renderer.aoBrightnessXYPP;
            }

            if (flag4 || flag3) {
                renderer.aoLightValueScratchXYZNPP = renderer.blockAccess.getBlock(x1 - 1, y1, z1 + 1).getAmbientOcclusionLightValue();
                renderer.aoBrightnessXYZNPP = block.getMixedBrightnessForBlock(renderer.blockAccess, x1 - 1, y1, z1 + 1);
            } else {
                renderer.aoLightValueScratchXYZNPP = renderer.aoLightValueScratchXYNP;
                renderer.aoBrightnessXYZNPP = renderer.aoBrightnessXYNP;
            }

            if (flag4 || flag2) {
                renderer.aoLightValueScratchXYZPPP = renderer.blockAccess.getBlock(x1 + 1, y1, z1 + 1).getAmbientOcclusionLightValue();
                renderer.aoBrightnessXYZPPP = block.getMixedBrightnessForBlock(renderer.blockAccess, x1 + 1, y1, z1 + 1);
            } else {
                renderer.aoLightValueScratchXYZPPP = renderer.aoLightValueScratchXYPP;
                renderer.aoBrightnessXYZPPP = renderer.aoBrightnessXYPP;
            }

            if (renderer.renderMaxY >= 1.0D) {
                --y1;
            }

            i1 = l;

            if (renderer.renderMaxY >= 1.0D || !renderer.blockAccess.getBlock(x1, y1 + 1, z1).isOpaqueCube()) {
                i1 = block.getMixedBrightnessForBlock(renderer.blockAccess, x1, y1 + 1, z1);
            }

            f7 = renderer.blockAccess.getBlock(x1, y1 + 1, z1).getAmbientOcclusionLightValue();
            f6 = (renderer.aoLightValueScratchXYZNPP + renderer.aoLightValueScratchXYNP + renderer.aoLightValueScratchYZPP + f7) / 4.0F;
            f3 = (renderer.aoLightValueScratchYZPP + f7 + renderer.aoLightValueScratchXYZPPP + renderer.aoLightValueScratchXYPP) / 4.0F;
            f4 = (f7 + renderer.aoLightValueScratchYZPN + renderer.aoLightValueScratchXYPP + renderer.aoLightValueScratchXYZPPN) / 4.0F;
            f5 = (renderer.aoLightValueScratchXYNP + renderer.aoLightValueScratchXYZNPN + f7 + renderer.aoLightValueScratchYZPN) / 4.0F;
            renderer.brightnessTopRight = renderer.getAoBrightness(renderer.aoBrightnessXYZNPP, renderer.aoBrightnessXYNP, renderer.aoBrightnessYZPP, i1);
            renderer.brightnessTopLeft = renderer.getAoBrightness(renderer.aoBrightnessYZPP, renderer.aoBrightnessXYZPPP, renderer.aoBrightnessXYPP, i1);
            renderer.brightnessBottomLeft = renderer.getAoBrightness(renderer.aoBrightnessYZPN, renderer.aoBrightnessXYPP, renderer.aoBrightnessXYZPPN, i1);
            renderer.brightnessBottomRight = renderer.getAoBrightness(renderer.aoBrightnessXYNP, renderer.aoBrightnessXYZNPN, renderer.aoBrightnessYZPN, i1);
            renderer.colorRedTopLeft = renderer.colorRedBottomLeft = renderer.colorRedBottomRight = renderer.colorRedTopRight = r;
            renderer.colorGreenTopLeft = renderer.colorGreenBottomLeft = renderer.colorGreenBottomRight = renderer.colorGreenTopRight = g;
            renderer.colorBlueTopLeft = renderer.colorBlueBottomLeft = renderer.colorBlueBottomRight = renderer.colorBlueTopRight = b;
            renderer.colorRedTopLeft *= f3;
            renderer.colorGreenTopLeft *= f3;
            renderer.colorBlueTopLeft *= f3;
            renderer.colorRedBottomLeft *= f4;
            renderer.colorGreenBottomLeft *= f4;
            renderer.colorBlueBottomLeft *= f4;
            renderer.colorRedBottomRight *= f5;
            renderer.colorGreenBottomRight *= f5;
            renderer.colorBlueBottomRight *= f5;
            renderer.colorRedTopRight *= f6;
            renderer.colorGreenTopRight *= f6;
            renderer.colorBlueTopRight *= f6;
            renderFaceYPos(renderer, block, metadata, (double) x1, (double) y1, (double) z1);
            flag = true;
        }

        if (renderer.renderAllFaces || block.shouldSideBeRendered(renderer.blockAccess, x1, y1, z1 - 1, 2)) {
            if (renderer.renderMinZ <= 0.0D) {
                --z1;
            }

            renderer.aoLightValueScratchXZNN = renderer.blockAccess.getBlock(x1 - 1, y1, z1).getAmbientOcclusionLightValue();
            renderer.aoLightValueScratchYZNN = renderer.blockAccess.getBlock(x1, y1 - 1, z1).getAmbientOcclusionLightValue();
            renderer.aoLightValueScratchYZPN = renderer.blockAccess.getBlock(x1, y1 + 1, z1).getAmbientOcclusionLightValue();
            renderer.aoLightValueScratchXZPN = renderer.blockAccess.getBlock(x1 + 1, y1, z1).getAmbientOcclusionLightValue();
            renderer.aoBrightnessXZNN = block.getMixedBrightnessForBlock(renderer.blockAccess, x1 - 1, y1, z1);
            renderer.aoBrightnessYZNN = block.getMixedBrightnessForBlock(renderer.blockAccess, x1, y1 - 1, z1);
            renderer.aoBrightnessYZPN = block.getMixedBrightnessForBlock(renderer.blockAccess, x1, y1 + 1, z1);
            renderer.aoBrightnessXZPN = block.getMixedBrightnessForBlock(renderer.blockAccess, x1 + 1, y1, z1);
            flag2 = renderer.blockAccess.getBlock(x1 + 1, y1, z1 - 1).getCanBlockGrass();
            flag3 = renderer.blockAccess.getBlock(x1 - 1, y1, z1 - 1).getCanBlockGrass();
            flag4 = renderer.blockAccess.getBlock(x1, y1 + 1, z1 - 1).getCanBlockGrass();
            flag5 = renderer.blockAccess.getBlock(x1, y1 - 1, z1 - 1).getCanBlockGrass();

            if (flag3 || flag5) {
                renderer.aoLightValueScratchXYZNNN = renderer.blockAccess.getBlock(x1 - 1, y1 - 1, z1).getAmbientOcclusionLightValue();
                renderer.aoBrightnessXYZNNN = block.getMixedBrightnessForBlock(renderer.blockAccess, x1 - 1, y1 - 1, z1);
            } else {
                renderer.aoLightValueScratchXYZNNN = renderer.aoLightValueScratchXZNN;
                renderer.aoBrightnessXYZNNN = renderer.aoBrightnessXZNN;
            }

            if (flag3 || flag4) {
                renderer.aoLightValueScratchXYZNPN = renderer.blockAccess.getBlock(x1 - 1, y1 + 1, z1).getAmbientOcclusionLightValue();
                renderer.aoBrightnessXYZNPN = block.getMixedBrightnessForBlock(renderer.blockAccess, x1 - 1, y1 + 1, z1);
            } else {
                renderer.aoLightValueScratchXYZNPN = renderer.aoLightValueScratchXZNN;
                renderer.aoBrightnessXYZNPN = renderer.aoBrightnessXZNN;
            }

            if (flag2 || flag5) {
                renderer.aoLightValueScratchXYZPNN = renderer.blockAccess.getBlock(x1 + 1, y1 - 1, z1).getAmbientOcclusionLightValue();
                renderer.aoBrightnessXYZPNN = block.getMixedBrightnessForBlock(renderer.blockAccess, x1 + 1, y1 - 1, z1);
            } else {
                renderer.aoLightValueScratchXYZPNN = renderer.aoLightValueScratchXZPN;
                renderer.aoBrightnessXYZPNN = renderer.aoBrightnessXZPN;
            }

            if (flag2 || flag4) {
                renderer.aoLightValueScratchXYZPPN = renderer.blockAccess.getBlock(x1 + 1, y1 + 1, z1).getAmbientOcclusionLightValue();
                renderer.aoBrightnessXYZPPN = block.getMixedBrightnessForBlock(renderer.blockAccess, x1 + 1, y1 + 1, z1);
            } else {
                renderer.aoLightValueScratchXYZPPN = renderer.aoLightValueScratchXZPN;
                renderer.aoBrightnessXYZPPN = renderer.aoBrightnessXZPN;
            }

            if (renderer.renderMinZ <= 0.0D) {
                ++z1;
            }

            i1 = l;

            if (renderer.renderMinZ <= 0.0D || !renderer.blockAccess.getBlock(x1, y1, z1 - 1).isOpaqueCube()) {
                i1 = block.getMixedBrightnessForBlock(renderer.blockAccess, x1, y1, z1 - 1);
            }

            f7 = renderer.blockAccess.getBlock(x1, y1, z1 - 1).getAmbientOcclusionLightValue();
            f3 = (renderer.aoLightValueScratchXZNN + renderer.aoLightValueScratchXYZNPN + f7 + renderer.aoLightValueScratchYZPN) / 4.0F;
            f4 = (f7 + renderer.aoLightValueScratchYZPN + renderer.aoLightValueScratchXZPN + renderer.aoLightValueScratchXYZPPN) / 4.0F;
            f5 = (renderer.aoLightValueScratchYZNN + f7 + renderer.aoLightValueScratchXYZPNN + renderer.aoLightValueScratchXZPN) / 4.0F;
            f6 = (renderer.aoLightValueScratchXYZNNN + renderer.aoLightValueScratchXZNN + renderer.aoLightValueScratchYZNN + f7) / 4.0F;
            renderer.brightnessTopLeft = renderer.getAoBrightness(renderer.aoBrightnessXZNN, renderer.aoBrightnessXYZNPN, renderer.aoBrightnessYZPN, i1);
            renderer.brightnessBottomLeft = renderer.getAoBrightness(renderer.aoBrightnessYZPN, renderer.aoBrightnessXZPN, renderer.aoBrightnessXYZPPN, i1);
            renderer.brightnessBottomRight = renderer.getAoBrightness(renderer.aoBrightnessYZNN, renderer.aoBrightnessXYZPNN, renderer.aoBrightnessXZPN, i1);
            renderer.brightnessTopRight = renderer.getAoBrightness(renderer.aoBrightnessXYZNNN, renderer.aoBrightnessXZNN, renderer.aoBrightnessYZNN, i1);

            if (flag1) {
                renderer.colorRedTopLeft = renderer.colorRedBottomLeft = renderer.colorRedBottomRight = renderer.colorRedTopRight = r * 0.8F;
                renderer.colorGreenTopLeft = renderer.colorGreenBottomLeft = renderer.colorGreenBottomRight = renderer.colorGreenTopRight = g * 0.8F;
                renderer.colorBlueTopLeft = renderer.colorBlueBottomLeft = renderer.colorBlueBottomRight = renderer.colorBlueTopRight = b * 0.8F;
            } else {
                renderer.colorRedTopLeft = renderer.colorRedBottomLeft = renderer.colorRedBottomRight = renderer.colorRedTopRight = 0.8F;
                renderer.colorGreenTopLeft = renderer.colorGreenBottomLeft = renderer.colorGreenBottomRight = renderer.colorGreenTopRight = 0.8F;
                renderer.colorBlueTopLeft = renderer.colorBlueBottomLeft = renderer.colorBlueBottomRight = renderer.colorBlueTopRight = 0.8F;
            }

            renderer.colorRedTopLeft *= f3;
            renderer.colorGreenTopLeft *= f3;
            renderer.colorBlueTopLeft *= f3;
            renderer.colorRedBottomLeft *= f4;
            renderer.colorGreenBottomLeft *= f4;
            renderer.colorBlueBottomLeft *= f4;
            renderer.colorRedBottomRight *= f5;
            renderer.colorGreenBottomRight *= f5;
            renderer.colorBlueBottomRight *= f5;
            renderer.colorRedTopRight *= f6;
            renderer.colorGreenTopRight *= f6;
            renderer.colorBlueTopRight *= f6;
            renderFaceZNeg(renderer, block, metadata, (double) x1, (double) y1, (double) z1);
            flag = true;
        }

        if (renderer.renderAllFaces || block.shouldSideBeRendered(renderer.blockAccess, x1, y1, z1 + 1, 3)) {
            if (renderer.renderMaxZ >= 1.0D) {
                ++z1;
            }

            renderer.aoLightValueScratchXZNP = renderer.blockAccess.getBlock(x1 - 1, y1, z1).getAmbientOcclusionLightValue();
            renderer.aoLightValueScratchXZPP = renderer.blockAccess.getBlock(x1 + 1, y1, z1).getAmbientOcclusionLightValue();
            renderer.aoLightValueScratchYZNP = renderer.blockAccess.getBlock(x1, y1 - 1, z1).getAmbientOcclusionLightValue();
            renderer.aoLightValueScratchYZPP = renderer.blockAccess.getBlock(x1, y1 + 1, z1).getAmbientOcclusionLightValue();
            renderer.aoBrightnessXZNP = block.getMixedBrightnessForBlock(renderer.blockAccess, x1 - 1, y1, z1);
            renderer.aoBrightnessXZPP = block.getMixedBrightnessForBlock(renderer.blockAccess, x1 + 1, y1, z1);
            renderer.aoBrightnessYZNP = block.getMixedBrightnessForBlock(renderer.blockAccess, x1, y1 - 1, z1);
            renderer.aoBrightnessYZPP = block.getMixedBrightnessForBlock(renderer.blockAccess, x1, y1 + 1, z1);
            flag2 = renderer.blockAccess.getBlock(x1 + 1, y1, z1 + 1).getCanBlockGrass();
            flag3 = renderer.blockAccess.getBlock(x1 - 1, y1, z1 + 1).getCanBlockGrass();
            flag4 = renderer.blockAccess.getBlock(x1, y1 + 1, z1 + 1).getCanBlockGrass();
            flag5 = renderer.blockAccess.getBlock(x1, y1 - 1, z1 + 1).getCanBlockGrass();

            if (flag3 || flag5) {
                renderer.aoLightValueScratchXYZNNP = renderer.blockAccess.getBlock(x1 - 1, y1 - 1, z1).getAmbientOcclusionLightValue();
                renderer.aoBrightnessXYZNNP = block.getMixedBrightnessForBlock(renderer.blockAccess, x1 - 1, y1 - 1, z1);
            } else {
                renderer.aoLightValueScratchXYZNNP = renderer.aoLightValueScratchXZNP;
                renderer.aoBrightnessXYZNNP = renderer.aoBrightnessXZNP;
            }

            if (flag3 || flag4) {
                renderer.aoLightValueScratchXYZNPP = renderer.blockAccess.getBlock(x1 - 1, y1 + 1, z1).getAmbientOcclusionLightValue();
                renderer.aoBrightnessXYZNPP = block.getMixedBrightnessForBlock(renderer.blockAccess, x1 - 1, y1 + 1, z1);
            } else {
                renderer.aoLightValueScratchXYZNPP = renderer.aoLightValueScratchXZNP;
                renderer.aoBrightnessXYZNPP = renderer.aoBrightnessXZNP;
            }

            if (flag2 || flag5) {
                renderer.aoLightValueScratchXYZPNP = renderer.blockAccess.getBlock(x1 + 1, y1 - 1, z1).getAmbientOcclusionLightValue();
                renderer.aoBrightnessXYZPNP = block.getMixedBrightnessForBlock(renderer.blockAccess, x1 + 1, y1 - 1, z1);
            } else {
                renderer.aoLightValueScratchXYZPNP = renderer.aoLightValueScratchXZPP;
                renderer.aoBrightnessXYZPNP = renderer.aoBrightnessXZPP;
            }

            if (flag2 || flag4) {
                renderer.aoLightValueScratchXYZPPP = renderer.blockAccess.getBlock(x1 + 1, y1 + 1, z1).getAmbientOcclusionLightValue();
                renderer.aoBrightnessXYZPPP = block.getMixedBrightnessForBlock(renderer.blockAccess, x1 + 1, y1 + 1, z1);
            } else {
                renderer.aoLightValueScratchXYZPPP = renderer.aoLightValueScratchXZPP;
                renderer.aoBrightnessXYZPPP = renderer.aoBrightnessXZPP;
            }

            if (renderer.renderMaxZ >= 1.0D) {
                --z1;
            }

            i1 = l;

            if (renderer.renderMaxZ >= 1.0D || !renderer.blockAccess.getBlock(x1, y1, z1 + 1).isOpaqueCube()) {
                i1 = block.getMixedBrightnessForBlock(renderer.blockAccess, x1, y1, z1 + 1);
            }

            f7 = renderer.blockAccess.getBlock(x1, y1, z1 + 1).getAmbientOcclusionLightValue();
            f3 = (renderer.aoLightValueScratchXZNP + renderer.aoLightValueScratchXYZNPP + f7 + renderer.aoLightValueScratchYZPP) / 4.0F;
            f6 = (f7 + renderer.aoLightValueScratchYZPP + renderer.aoLightValueScratchXZPP + renderer.aoLightValueScratchXYZPPP) / 4.0F;
            f5 = (renderer.aoLightValueScratchYZNP + f7 + renderer.aoLightValueScratchXYZPNP + renderer.aoLightValueScratchXZPP) / 4.0F;
            f4 = (renderer.aoLightValueScratchXYZNNP + renderer.aoLightValueScratchXZNP + renderer.aoLightValueScratchYZNP + f7) / 4.0F;
            renderer.brightnessTopLeft = renderer.getAoBrightness(renderer.aoBrightnessXZNP, renderer.aoBrightnessXYZNPP, renderer.aoBrightnessYZPP, i1);
            renderer.brightnessTopRight = renderer.getAoBrightness(renderer.aoBrightnessYZPP, renderer.aoBrightnessXZPP, renderer.aoBrightnessXYZPPP, i1);
            renderer.brightnessBottomRight = renderer.getAoBrightness(renderer.aoBrightnessYZNP, renderer.aoBrightnessXYZPNP, renderer.aoBrightnessXZPP, i1);
            renderer.brightnessBottomLeft = renderer.getAoBrightness(renderer.aoBrightnessXYZNNP, renderer.aoBrightnessXZNP, renderer.aoBrightnessYZNP, i1);

            if (flag1) {
                renderer.colorRedTopLeft = renderer.colorRedBottomLeft = renderer.colorRedBottomRight = renderer.colorRedTopRight = r * 0.8F;
                renderer.colorGreenTopLeft = renderer.colorGreenBottomLeft = renderer.colorGreenBottomRight = renderer.colorGreenTopRight = g * 0.8F;
                renderer.colorBlueTopLeft = renderer.colorBlueBottomLeft = renderer.colorBlueBottomRight = renderer.colorBlueTopRight = b * 0.8F;
            } else {
                renderer.colorRedTopLeft = renderer.colorRedBottomLeft = renderer.colorRedBottomRight = renderer.colorRedTopRight = 0.8F;
                renderer.colorGreenTopLeft = renderer.colorGreenBottomLeft = renderer.colorGreenBottomRight = renderer.colorGreenTopRight = 0.8F;
                renderer.colorBlueTopLeft = renderer.colorBlueBottomLeft = renderer.colorBlueBottomRight = renderer.colorBlueTopRight = 0.8F;
            }

            renderer.colorRedTopLeft *= f3;
            renderer.colorGreenTopLeft *= f3;
            renderer.colorBlueTopLeft *= f3;
            renderer.colorRedBottomLeft *= f4;
            renderer.colorGreenBottomLeft *= f4;
            renderer.colorBlueBottomLeft *= f4;
            renderer.colorRedBottomRight *= f5;
            renderer.colorGreenBottomRight *= f5;
            renderer.colorBlueBottomRight *= f5;
            renderer.colorRedTopRight *= f6;
            renderer.colorGreenTopRight *= f6;
            renderer.colorBlueTopRight *= f6;
            renderFaceZPos(renderer, block, metadata, (double) x1, (double) y1, (double) z1);
            flag = true;
        }

        if (renderer.renderAllFaces || block.shouldSideBeRendered(renderer.blockAccess, x1 - 1, y1, z1, 4)) {
            if (renderer.renderMinX <= 0.0D) {
                --x1;
            }

            renderer.aoLightValueScratchXYNN = renderer.blockAccess.getBlock(x1, y1 - 1, z1).getAmbientOcclusionLightValue();
            renderer.aoLightValueScratchXZNN = renderer.blockAccess.getBlock(x1, y1, z1 - 1).getAmbientOcclusionLightValue();
            renderer.aoLightValueScratchXZNP = renderer.blockAccess.getBlock(x1, y1, z1 + 1).getAmbientOcclusionLightValue();
            renderer.aoLightValueScratchXYNP = renderer.blockAccess.getBlock(x1, y1 + 1, z1).getAmbientOcclusionLightValue();
            renderer.aoBrightnessXYNN = block.getMixedBrightnessForBlock(renderer.blockAccess, x1, y1 - 1, z1);
            renderer.aoBrightnessXZNN = block.getMixedBrightnessForBlock(renderer.blockAccess, x1, y1, z1 - 1);
            renderer.aoBrightnessXZNP = block.getMixedBrightnessForBlock(renderer.blockAccess, x1, y1, z1 + 1);
            renderer.aoBrightnessXYNP = block.getMixedBrightnessForBlock(renderer.blockAccess, x1, y1 + 1, z1);
            flag2 = renderer.blockAccess.getBlock(x1 - 1, y1 + 1, z1).getCanBlockGrass();
            flag3 = renderer.blockAccess.getBlock(x1 - 1, y1 - 1, z1).getCanBlockGrass();
            flag4 = renderer.blockAccess.getBlock(x1 - 1, y1, z1 - 1).getCanBlockGrass();
            flag5 = renderer.blockAccess.getBlock(x1 - 1, y1, z1 + 1).getCanBlockGrass();

            if (flag4 || flag3) {
                renderer.aoLightValueScratchXYZNNN = renderer.blockAccess.getBlock(x1, y1 - 1, z1 - 1).getAmbientOcclusionLightValue();
                renderer.aoBrightnessXYZNNN = block.getMixedBrightnessForBlock(renderer.blockAccess, x1, y1 - 1, z1 - 1);
            } else {
                renderer.aoLightValueScratchXYZNNN = renderer.aoLightValueScratchXZNN;
                renderer.aoBrightnessXYZNNN = renderer.aoBrightnessXZNN;
            }

            if (flag5 || flag3) {
                renderer.aoLightValueScratchXYZNNP = renderer.blockAccess.getBlock(x1, y1 - 1, z1 + 1).getAmbientOcclusionLightValue();
                renderer.aoBrightnessXYZNNP = block.getMixedBrightnessForBlock(renderer.blockAccess, x1, y1 - 1, z1 + 1);
            } else {
                renderer.aoLightValueScratchXYZNNP = renderer.aoLightValueScratchXZNP;
                renderer.aoBrightnessXYZNNP = renderer.aoBrightnessXZNP;
            }

            if (flag4 || flag2) {
                renderer.aoLightValueScratchXYZNPN = renderer.blockAccess.getBlock(x1, y1 + 1, z1 - 1).getAmbientOcclusionLightValue();
                renderer.aoBrightnessXYZNPN = block.getMixedBrightnessForBlock(renderer.blockAccess, x1, y1 + 1, z1 - 1);
            } else {
                renderer.aoLightValueScratchXYZNPN = renderer.aoLightValueScratchXZNN;
                renderer.aoBrightnessXYZNPN = renderer.aoBrightnessXZNN;
            }

            if (flag5 || flag2) {
                renderer.aoLightValueScratchXYZNPP = renderer.blockAccess.getBlock(x1, y1 + 1, z1 + 1).getAmbientOcclusionLightValue();
                renderer.aoBrightnessXYZNPP = block.getMixedBrightnessForBlock(renderer.blockAccess, x1, y1 + 1, z1 + 1);
            } else {
                renderer.aoLightValueScratchXYZNPP = renderer.aoLightValueScratchXZNP;
                renderer.aoBrightnessXYZNPP = renderer.aoBrightnessXZNP;
            }

            if (renderer.renderMinX <= 0.0D) {
                ++x1;
            }

            i1 = l;

            if (renderer.renderMinX <= 0.0D || !renderer.blockAccess.getBlock(x1 - 1, y1, z1).isOpaqueCube()) {
                i1 = block.getMixedBrightnessForBlock(renderer.blockAccess, x1 - 1, y1, z1);
            }

            f7 = renderer.blockAccess.getBlock(x1 - 1, y1, z1).getAmbientOcclusionLightValue();
            f6 = (renderer.aoLightValueScratchXYNN + renderer.aoLightValueScratchXYZNNP + f7 + renderer.aoLightValueScratchXZNP) / 4.0F;
            f3 = (f7 + renderer.aoLightValueScratchXZNP + renderer.aoLightValueScratchXYNP + renderer.aoLightValueScratchXYZNPP) / 4.0F;
            f4 = (renderer.aoLightValueScratchXZNN + f7 + renderer.aoLightValueScratchXYZNPN + renderer.aoLightValueScratchXYNP) / 4.0F;
            f5 = (renderer.aoLightValueScratchXYZNNN + renderer.aoLightValueScratchXYNN + renderer.aoLightValueScratchXZNN + f7) / 4.0F;
            renderer.brightnessTopRight = renderer.getAoBrightness(renderer.aoBrightnessXYNN, renderer.aoBrightnessXYZNNP, renderer.aoBrightnessXZNP, i1);
            renderer.brightnessTopLeft = renderer.getAoBrightness(renderer.aoBrightnessXZNP, renderer.aoBrightnessXYNP, renderer.aoBrightnessXYZNPP, i1);
            renderer.brightnessBottomLeft = renderer.getAoBrightness(renderer.aoBrightnessXZNN, renderer.aoBrightnessXYZNPN, renderer.aoBrightnessXYNP, i1);
            renderer.brightnessBottomRight = renderer.getAoBrightness(renderer.aoBrightnessXYZNNN, renderer.aoBrightnessXYNN, renderer.aoBrightnessXZNN, i1);

            if (flag1) {
                renderer.colorRedTopLeft = renderer.colorRedBottomLeft = renderer.colorRedBottomRight = renderer.colorRedTopRight = r * 0.6F;
                renderer.colorGreenTopLeft = renderer.colorGreenBottomLeft = renderer.colorGreenBottomRight = renderer.colorGreenTopRight = g * 0.6F;
                renderer.colorBlueTopLeft = renderer.colorBlueBottomLeft = renderer.colorBlueBottomRight = renderer.colorBlueTopRight = b * 0.6F;
            } else {
                renderer.colorRedTopLeft = renderer.colorRedBottomLeft = renderer.colorRedBottomRight = renderer.colorRedTopRight = 0.6F;
                renderer.colorGreenTopLeft = renderer.colorGreenBottomLeft = renderer.colorGreenBottomRight = renderer.colorGreenTopRight = 0.6F;
                renderer.colorBlueTopLeft = renderer.colorBlueBottomLeft = renderer.colorBlueBottomRight = renderer.colorBlueTopRight = 0.6F;
            }

            renderer.colorRedTopLeft *= f3;
            renderer.colorGreenTopLeft *= f3;
            renderer.colorBlueTopLeft *= f3;
            renderer.colorRedBottomLeft *= f4;
            renderer.colorGreenBottomLeft *= f4;
            renderer.colorBlueBottomLeft *= f4;
            renderer.colorRedBottomRight *= f5;
            renderer.colorGreenBottomRight *= f5;
            renderer.colorBlueBottomRight *= f5;
            renderer.colorRedTopRight *= f6;
            renderer.colorGreenTopRight *= f6;
            renderer.colorBlueTopRight *= f6;
            renderFaceXNeg(renderer, block, metadata, (double) x1, (double) y1, (double) z1);
            flag = true;
        }

        if (renderer.renderAllFaces || block.shouldSideBeRendered(renderer.blockAccess, x1 + 1, y1, z1, 5)) {
            if (renderer.renderMaxX >= 1.0D) {
                ++x1;
            }

            renderer.aoLightValueScratchXYPN = renderer.blockAccess.getBlock(x1, y1 - 1, z1).getAmbientOcclusionLightValue();
            renderer.aoLightValueScratchXZPN = renderer.blockAccess.getBlock(x1, y1, z1 - 1).getAmbientOcclusionLightValue();
            renderer.aoLightValueScratchXZPP = renderer.blockAccess.getBlock(x1, y1, z1 + 1).getAmbientOcclusionLightValue();
            renderer.aoLightValueScratchXYPP = renderer.blockAccess.getBlock(x1, y1 + 1, z1).getAmbientOcclusionLightValue();
            renderer.aoBrightnessXYPN = block.getMixedBrightnessForBlock(renderer.blockAccess, x1, y1 - 1, z1);
            renderer.aoBrightnessXZPN = block.getMixedBrightnessForBlock(renderer.blockAccess, x1, y1, z1 - 1);
            renderer.aoBrightnessXZPP = block.getMixedBrightnessForBlock(renderer.blockAccess, x1, y1, z1 + 1);
            renderer.aoBrightnessXYPP = block.getMixedBrightnessForBlock(renderer.blockAccess, x1, y1 + 1, z1);
            flag2 = renderer.blockAccess.getBlock(x1 + 1, y1 + 1, z1).getCanBlockGrass();
            flag3 = renderer.blockAccess.getBlock(x1 + 1, y1 - 1, z1).getCanBlockGrass();
            flag4 = renderer.blockAccess.getBlock(x1 + 1, y1, z1 + 1).getCanBlockGrass();
            flag5 = renderer.blockAccess.getBlock(x1 + 1, y1, z1 - 1).getCanBlockGrass();

            if (flag3 || flag5) {
                renderer.aoLightValueScratchXYZPNN = renderer.blockAccess.getBlock(x1, y1 - 1, z1 - 1).getAmbientOcclusionLightValue();
                renderer.aoBrightnessXYZPNN = block.getMixedBrightnessForBlock(renderer.blockAccess, x1, y1 - 1, z1 - 1);
            } else {
                renderer.aoLightValueScratchXYZPNN = renderer.aoLightValueScratchXZPN;
                renderer.aoBrightnessXYZPNN = renderer.aoBrightnessXZPN;
            }

            if (flag3 || flag4) {
                renderer.aoLightValueScratchXYZPNP = renderer.blockAccess.getBlock(x1, y1 - 1, z1 + 1).getAmbientOcclusionLightValue();
                renderer.aoBrightnessXYZPNP = block.getMixedBrightnessForBlock(renderer.blockAccess, x1, y1 - 1, z1 + 1);
            } else {
                renderer.aoLightValueScratchXYZPNP = renderer.aoLightValueScratchXZPP;
                renderer.aoBrightnessXYZPNP = renderer.aoBrightnessXZPP;
            }

            if (flag2 || flag5) {
                renderer.aoLightValueScratchXYZPPN = renderer.blockAccess.getBlock(x1, y1 + 1, z1 - 1).getAmbientOcclusionLightValue();
                renderer.aoBrightnessXYZPPN = block.getMixedBrightnessForBlock(renderer.blockAccess, x1, y1 + 1, z1 - 1);
            } else {
                renderer.aoLightValueScratchXYZPPN = renderer.aoLightValueScratchXZPN;
                renderer.aoBrightnessXYZPPN = renderer.aoBrightnessXZPN;
            }

            if (flag2 || flag4) {
                renderer.aoLightValueScratchXYZPPP = renderer.blockAccess.getBlock(x1, y1 + 1, z1 + 1).getAmbientOcclusionLightValue();
                renderer.aoBrightnessXYZPPP = block.getMixedBrightnessForBlock(renderer.blockAccess, x1, y1 + 1, z1 + 1);
            } else {
                renderer.aoLightValueScratchXYZPPP = renderer.aoLightValueScratchXZPP;
                renderer.aoBrightnessXYZPPP = renderer.aoBrightnessXZPP;
            }

            if (renderer.renderMaxX >= 1.0D) {
                --x1;
            }

            i1 = l;

            if (renderer.renderMaxX >= 1.0D || !renderer.blockAccess.getBlock(x1 + 1, y1, z1).isOpaqueCube()) {
                i1 = block.getMixedBrightnessForBlock(renderer.blockAccess, x1 + 1, y1, z1);
            }

            f7 = renderer.blockAccess.getBlock(x1 + 1, y1, z1).getAmbientOcclusionLightValue();
            f3 = (renderer.aoLightValueScratchXYPN + renderer.aoLightValueScratchXYZPNP + f7 + renderer.aoLightValueScratchXZPP) / 4.0F;
            f4 = (renderer.aoLightValueScratchXYZPNN + renderer.aoLightValueScratchXYPN + renderer.aoLightValueScratchXZPN + f7) / 4.0F;
            f5 = (renderer.aoLightValueScratchXZPN + f7 + renderer.aoLightValueScratchXYZPPN + renderer.aoLightValueScratchXYPP) / 4.0F;
            f6 = (f7 + renderer.aoLightValueScratchXZPP + renderer.aoLightValueScratchXYPP + renderer.aoLightValueScratchXYZPPP) / 4.0F;
            renderer.brightnessTopLeft = renderer.getAoBrightness(renderer.aoBrightnessXYPN, renderer.aoBrightnessXYZPNP, renderer.aoBrightnessXZPP, i1);
            renderer.brightnessTopRight = renderer.getAoBrightness(renderer.aoBrightnessXZPP, renderer.aoBrightnessXYPP, renderer.aoBrightnessXYZPPP, i1);
            renderer.brightnessBottomRight = renderer.getAoBrightness(renderer.aoBrightnessXZPN, renderer.aoBrightnessXYZPPN, renderer.aoBrightnessXYPP, i1);
            renderer.brightnessBottomLeft = renderer.getAoBrightness(renderer.aoBrightnessXYZPNN, renderer.aoBrightnessXYPN, renderer.aoBrightnessXZPN, i1);

            if (flag1) {
                renderer.colorRedTopLeft = renderer.colorRedBottomLeft = renderer.colorRedBottomRight = renderer.colorRedTopRight = r * 0.6F;
                renderer.colorGreenTopLeft = renderer.colorGreenBottomLeft = renderer.colorGreenBottomRight = renderer.colorGreenTopRight = g * 0.6F;
                renderer.colorBlueTopLeft = renderer.colorBlueBottomLeft = renderer.colorBlueBottomRight = renderer.colorBlueTopRight = b * 0.6F;
            } else {
                renderer.colorRedTopLeft = renderer.colorRedBottomLeft = renderer.colorRedBottomRight = renderer.colorRedTopRight = 0.6F;
                renderer.colorGreenTopLeft = renderer.colorGreenBottomLeft = renderer.colorGreenBottomRight = renderer.colorGreenTopRight = 0.6F;
                renderer.colorBlueTopLeft = renderer.colorBlueBottomLeft = renderer.colorBlueBottomRight = renderer.colorBlueTopRight = 0.6F;
            }

            renderer.colorRedTopLeft *= f3;
            renderer.colorGreenTopLeft *= f3;
            renderer.colorBlueTopLeft *= f3;
            renderer.colorRedBottomLeft *= f4;
            renderer.colorGreenBottomLeft *= f4;
            renderer.colorBlueBottomLeft *= f4;
            renderer.colorRedBottomRight *= f5;
            renderer.colorGreenBottomRight *= f5;
            renderer.colorBlueBottomRight *= f5;
            renderer.colorRedTopRight *= f6;
            renderer.colorGreenTopRight *= f6;
            renderer.colorBlueTopRight *= f6;
            renderFaceXPos(renderer, block, metadata, (double) x1, (double) y1, (double) z1);
            flag = true;
        }

        renderer.enableAO = false;
        return flag;
    }

    private boolean renderLayeredBlockWithAmbientOcclusionPartial(RenderBlocks renderer, final Block block, final int metadata, final int x, final int y, final int z, final float r, final float g, final float b) {
        int x1 = x;
        int y1 = y;
        int z1 = z;
        renderer.enableAO = true;
        boolean flag = false;
        float f3 = 0.0F;
        float f4 = 0.0F;
        float f5 = 0.0F;
        float f6 = 0.0F;
        boolean flag1 = true;
        final int l = block.getMixedBrightnessForBlock(renderer.blockAccess, x1, y1, z1);
        final Tessellator tessellator = Tessellator.instance;
        tessellator.setBrightness(983055);

        if (renderer.hasOverrideBlockTexture()) {
            flag1 = false;
        }

        boolean flag2;
        boolean flag3;
        boolean flag4;
        boolean flag5;
        int i1;
        float f7;

        if (renderer.renderAllFaces || block.shouldSideBeRendered(renderer.blockAccess, x1, y1 - 1, z1, 0)) {
            if (renderer.renderMinY <= 0.0D) {
                --y1;
            }

            renderer.aoBrightnessXYNN = block.getMixedBrightnessForBlock(renderer.blockAccess, x1 - 1, y1, z1);
            renderer.aoBrightnessYZNN = block.getMixedBrightnessForBlock(renderer.blockAccess, x1, y1, z1 - 1);
            renderer.aoBrightnessYZNP = block.getMixedBrightnessForBlock(renderer.blockAccess, x1, y1, z1 + 1);
            renderer.aoBrightnessXYPN = block.getMixedBrightnessForBlock(renderer.blockAccess, x1 + 1, y1, z1);
            renderer.aoLightValueScratchXYNN = renderer.blockAccess.getBlock(x1 - 1, y1, z1).getAmbientOcclusionLightValue();
            renderer.aoLightValueScratchYZNN = renderer.blockAccess.getBlock(x1, y1, z1 - 1).getAmbientOcclusionLightValue();
            renderer.aoLightValueScratchYZNP = renderer.blockAccess.getBlock(x1, y1, z1 + 1).getAmbientOcclusionLightValue();
            renderer.aoLightValueScratchXYPN = renderer.blockAccess.getBlock(x1 + 1, y1, z1).getAmbientOcclusionLightValue();
            flag2 = renderer.blockAccess.getBlock(x1 + 1, y1 - 1, z1).getCanBlockGrass();
            flag3 = renderer.blockAccess.getBlock(x1 - 1, y1 - 1, z1).getCanBlockGrass();
            flag4 = renderer.blockAccess.getBlock(x1, y1 - 1, z1 + 1).getCanBlockGrass();
            flag5 = renderer.blockAccess.getBlock(x1, y1 - 1, z1 - 1).getCanBlockGrass();

            if (flag5 || flag3) {
                renderer.aoLightValueScratchXYZNNN = renderer.blockAccess.getBlock(x1 - 1, y1, z1 - 1).getAmbientOcclusionLightValue();
                renderer.aoBrightnessXYZNNN = block.getMixedBrightnessForBlock(renderer.blockAccess, x1 - 1, y1, z1 - 1);
            } else {
                renderer.aoLightValueScratchXYZNNN = renderer.aoLightValueScratchXYNN;
                renderer.aoBrightnessXYZNNN = renderer.aoBrightnessXYNN;
            }

            if (flag4|| flag3) {
                renderer.aoLightValueScratchXYZNNP = renderer.blockAccess.getBlock(x1 - 1, y1, z1 + 1).getAmbientOcclusionLightValue();
                renderer.aoBrightnessXYZNNP = block.getMixedBrightnessForBlock(renderer.blockAccess, x1 - 1, y1, z1 + 1);
            } else {
                renderer.aoLightValueScratchXYZNNP = renderer.aoLightValueScratchXYNN;
                renderer.aoBrightnessXYZNNP = renderer.aoBrightnessXYNN;
            }

            if (flag5 || flag2) {
                renderer.aoLightValueScratchXYZPNN = renderer.blockAccess.getBlock(x1 + 1, y1, z1 - 1).getAmbientOcclusionLightValue();
                renderer.aoBrightnessXYZPNN = block.getMixedBrightnessForBlock(renderer.blockAccess, x1 + 1, y1, z1 - 1);
            } else {
                renderer.aoLightValueScratchXYZPNN = renderer.aoLightValueScratchXYPN;
                renderer.aoBrightnessXYZPNN = renderer.aoBrightnessXYPN;
            }

            if (flag4 || flag2) {
                renderer.aoLightValueScratchXYZPNP = renderer.blockAccess.getBlock(x1 + 1, y1, z1 + 1).getAmbientOcclusionLightValue();
                renderer.aoBrightnessXYZPNP = block.getMixedBrightnessForBlock(renderer.blockAccess, x1 + 1, y1, z1 + 1);
            } else {
                renderer.aoLightValueScratchXYZPNP = renderer.aoLightValueScratchXYPN;
                renderer.aoBrightnessXYZPNP = renderer.aoBrightnessXYPN;
            }

            if (renderer.renderMinY <= 0.0D) {
                ++y1;
            }

            i1 = l;

            if (renderer.renderMinY <= 0.0D || !renderer.blockAccess.getBlock(x1, y1 - 1, z1).isOpaqueCube()) {
                i1 = block.getMixedBrightnessForBlock(renderer.blockAccess, x1, y1 - 1, z1);
            }

            f7 = renderer.blockAccess.getBlock(x1, y1 - 1, z1).getAmbientOcclusionLightValue();
            f3 = (renderer.aoLightValueScratchXYZNNP + renderer.aoLightValueScratchXYNN + renderer.aoLightValueScratchYZNP + f7) / 4.0F;
            f6 = (renderer.aoLightValueScratchYZNP + f7 + renderer.aoLightValueScratchXYZPNP + renderer.aoLightValueScratchXYPN) / 4.0F;
            f5 = (f7 + renderer.aoLightValueScratchYZNN + renderer.aoLightValueScratchXYPN + renderer.aoLightValueScratchXYZPNN) / 4.0F;
            f4 = (renderer.aoLightValueScratchXYNN + renderer.aoLightValueScratchXYZNNN + f7 + renderer.aoLightValueScratchYZNN) / 4.0F;
            renderer.brightnessTopLeft = renderer.getAoBrightness(renderer.aoBrightnessXYZNNP, renderer.aoBrightnessXYNN, renderer.aoBrightnessYZNP, i1);
            renderer.brightnessTopRight = renderer.getAoBrightness(renderer.aoBrightnessYZNP, renderer.aoBrightnessXYZPNP, renderer.aoBrightnessXYPN, i1);
            renderer.brightnessBottomRight = renderer.getAoBrightness(renderer.aoBrightnessYZNN, renderer.aoBrightnessXYPN, renderer.aoBrightnessXYZPNN, i1);
            renderer.brightnessBottomLeft = renderer.getAoBrightness(renderer.aoBrightnessXYNN, renderer.aoBrightnessXYZNNN, renderer.aoBrightnessYZNN, i1);

            if (flag1) {
                renderer.colorRedTopLeft = renderer.colorRedBottomLeft = renderer.colorRedBottomRight = renderer.colorRedTopRight = r * 0.5F;
                renderer.colorGreenTopLeft = renderer.colorGreenBottomLeft = renderer.colorGreenBottomRight = renderer.colorGreenTopRight = g * 0.5F;
                renderer.colorBlueTopLeft = renderer.colorBlueBottomLeft = renderer.colorBlueBottomRight = renderer.colorBlueTopRight = b * 0.5F;
            } else {
                renderer.colorRedTopLeft = renderer.colorRedBottomLeft = renderer.colorRedBottomRight = renderer.colorRedTopRight = 0.5F;
                renderer.colorGreenTopLeft = renderer.colorGreenBottomLeft = renderer.colorGreenBottomRight = renderer.colorGreenTopRight = 0.5F;
                renderer.colorBlueTopLeft = renderer.colorBlueBottomLeft = renderer.colorBlueBottomRight = renderer.colorBlueTopRight = 0.5F;
            }

            renderer.colorRedTopLeft *= f3;
            renderer.colorGreenTopLeft *= f3;
            renderer.colorBlueTopLeft *= f3;
            renderer.colorRedBottomLeft *= f4;
            renderer.colorGreenBottomLeft *= f4;
            renderer.colorBlueBottomLeft *= f4;
            renderer.colorRedBottomRight *= f5;
            renderer.colorGreenBottomRight *= f5;
            renderer.colorBlueBottomRight *= f5;
            renderer.colorRedTopRight *= f6;
            renderer.colorGreenTopRight *= f6;
            renderer.colorBlueTopRight *= f6;
            renderFaceYNeg(renderer, block, metadata, (double) x1, (double) y1, (double) z1);
            flag = true;
        }

        if (renderer.renderAllFaces || block.shouldSideBeRendered(renderer.blockAccess, x1, y1 + 1, z1, 1)) {
            if (renderer.renderMaxY >= 1.0D) {
                ++y1;
            }

            renderer.aoBrightnessXYNP = block.getMixedBrightnessForBlock(renderer.blockAccess, x1 - 1, y1, z1);
            renderer.aoBrightnessXYPP = block.getMixedBrightnessForBlock(renderer.blockAccess, x1 + 1, y1, z1);
            renderer.aoBrightnessYZPN = block.getMixedBrightnessForBlock(renderer.blockAccess, x1, y1, z1 - 1);
            renderer.aoBrightnessYZPP = block.getMixedBrightnessForBlock(renderer.blockAccess, x1, y1, z1 + 1);
            renderer.aoLightValueScratchXYNP = renderer.blockAccess.getBlock(x1 - 1, y1, z1).getAmbientOcclusionLightValue();
            renderer.aoLightValueScratchXYPP = renderer.blockAccess.getBlock(x1 + 1, y1, z1).getAmbientOcclusionLightValue();
            renderer.aoLightValueScratchYZPN = renderer.blockAccess.getBlock(x1, y1, z1 - 1).getAmbientOcclusionLightValue();
            renderer.aoLightValueScratchYZPP = renderer.blockAccess.getBlock(x1, y1, z1 + 1).getAmbientOcclusionLightValue();
            flag2 = renderer.blockAccess.getBlock(x1 + 1, y1 + 1, z1).getCanBlockGrass();
            flag3 = renderer.blockAccess.getBlock(x1 - 1, y1 + 1, z1).getCanBlockGrass();
            flag4 = renderer.blockAccess.getBlock(x1, y1 + 1, z1 + 1).getCanBlockGrass();
            flag5 = renderer.blockAccess.getBlock(x1, y1 + 1, z1 - 1).getCanBlockGrass();

            if (flag5 || flag3) {
                renderer.aoLightValueScratchXYZNPN = renderer.blockAccess.getBlock(x1 - 1, y1, z1 - 1).getAmbientOcclusionLightValue();
                renderer.aoBrightnessXYZNPN = block.getMixedBrightnessForBlock(renderer.blockAccess, x1 - 1, y1, z1 - 1);
            } else {
                renderer.aoLightValueScratchXYZNPN = renderer.aoLightValueScratchXYNP;
                renderer.aoBrightnessXYZNPN = renderer.aoBrightnessXYNP;
            }

            if (flag5 || flag2) {
                renderer.aoLightValueScratchXYZPPN = renderer.blockAccess.getBlock(x1 + 1, y1, z1 - 1).getAmbientOcclusionLightValue();
                renderer.aoBrightnessXYZPPN = block.getMixedBrightnessForBlock(renderer.blockAccess, x1 + 1, y1, z1 - 1);
            } else {
                renderer.aoLightValueScratchXYZPPN = renderer.aoLightValueScratchXYPP;
                renderer.aoBrightnessXYZPPN = renderer.aoBrightnessXYPP;
            }

            if (flag4 || flag3) {
                renderer.aoLightValueScratchXYZNPP = renderer.blockAccess.getBlock(x1 - 1, y1, z1 + 1).getAmbientOcclusionLightValue();
                renderer.aoBrightnessXYZNPP = block.getMixedBrightnessForBlock(renderer.blockAccess, x1 - 1, y1, z1 + 1);
            } else {
                renderer.aoLightValueScratchXYZNPP = renderer.aoLightValueScratchXYNP;
                renderer.aoBrightnessXYZNPP = renderer.aoBrightnessXYNP;
            }

            if (flag4 || flag2) {
                renderer.aoLightValueScratchXYZPPP = renderer.blockAccess.getBlock(x1 + 1, y1, z1 + 1).getAmbientOcclusionLightValue();
                renderer.aoBrightnessXYZPPP = block.getMixedBrightnessForBlock(renderer.blockAccess, x1 + 1, y1, z1 + 1);
            } else {
                renderer.aoLightValueScratchXYZPPP = renderer.aoLightValueScratchXYPP;
                renderer.aoBrightnessXYZPPP = renderer.aoBrightnessXYPP;
            }

            if (renderer.renderMaxY >= 1.0D) {
                --y1;
            }

            i1 = l;

            if (renderer.renderMaxY >= 1.0D || !renderer.blockAccess.getBlock(x1, y1 + 1, z1).isOpaqueCube()) {
                i1 = block.getMixedBrightnessForBlock(renderer.blockAccess, x1, y1 + 1, z1);
            }

            f7 = renderer.blockAccess.getBlock(x1, y1 + 1, z1).getAmbientOcclusionLightValue();
            f6 = (renderer.aoLightValueScratchXYZNPP + renderer.aoLightValueScratchXYNP + renderer.aoLightValueScratchYZPP + f7) / 4.0F;
            f3 = (renderer.aoLightValueScratchYZPP + f7 + renderer.aoLightValueScratchXYZPPP + renderer.aoLightValueScratchXYPP) / 4.0F;
            f4 = (f7 + renderer.aoLightValueScratchYZPN + renderer.aoLightValueScratchXYPP + renderer.aoLightValueScratchXYZPPN) / 4.0F;
            f5 = (renderer.aoLightValueScratchXYNP + renderer.aoLightValueScratchXYZNPN + f7 + renderer.aoLightValueScratchYZPN) / 4.0F;
            renderer.brightnessTopRight = renderer.getAoBrightness(renderer.aoBrightnessXYZNPP, renderer.aoBrightnessXYNP, renderer.aoBrightnessYZPP, i1);
            renderer.brightnessTopLeft = renderer.getAoBrightness(renderer.aoBrightnessYZPP, renderer.aoBrightnessXYZPPP, renderer.aoBrightnessXYPP, i1);
            renderer.brightnessBottomLeft = renderer.getAoBrightness(renderer.aoBrightnessYZPN, renderer.aoBrightnessXYPP, renderer.aoBrightnessXYZPPN, i1);
            renderer.brightnessBottomRight = renderer.getAoBrightness(renderer.aoBrightnessXYNP, renderer.aoBrightnessXYZNPN, renderer.aoBrightnessYZPN, i1);
            renderer.colorRedTopLeft = renderer.colorRedBottomLeft = renderer.colorRedBottomRight = renderer.colorRedTopRight = r;
            renderer.colorGreenTopLeft = renderer.colorGreenBottomLeft = renderer.colorGreenBottomRight = renderer.colorGreenTopRight = g;
            renderer.colorBlueTopLeft = renderer.colorBlueBottomLeft = renderer.colorBlueBottomRight = renderer.colorBlueTopRight = b;
            renderer.colorRedTopLeft *= f3;
            renderer.colorGreenTopLeft *= f3;
            renderer.colorBlueTopLeft *= f3;
            renderer.colorRedBottomLeft *= f4;
            renderer.colorGreenBottomLeft *= f4;
            renderer.colorBlueBottomLeft *= f4;
            renderer.colorRedBottomRight *= f5;
            renderer.colorGreenBottomRight *= f5;
            renderer.colorBlueBottomRight *= f5;
            renderer.colorRedTopRight *= f6;
            renderer.colorGreenTopRight *= f6;
            renderer.colorBlueTopRight *= f6;
            renderFaceYPos(renderer, block, metadata, (double) x1, (double) y1, (double) z1);
            flag = true;
        }

        float f8;
        float f9;
        float f10;
        float f11;
        int j1;
        int k1;
        int l1;
        int i2;

        if (renderer.renderAllFaces || block.shouldSideBeRendered(renderer.blockAccess, x1, y1, z1 - 1, 2)) {
            if (renderer.renderMinZ <= 0.0D) {
                --z1;
            }

            renderer.aoLightValueScratchXZNN = renderer.blockAccess.getBlock(x1 - 1, y1, z1).getAmbientOcclusionLightValue();
            renderer.aoLightValueScratchYZNN = renderer.blockAccess.getBlock(x1, y1 - 1, z1).getAmbientOcclusionLightValue();
            renderer.aoLightValueScratchYZPN = renderer.blockAccess.getBlock(x1, y1 + 1, z1).getAmbientOcclusionLightValue();
            renderer.aoLightValueScratchXZPN = renderer.blockAccess.getBlock(x1 + 1, y1, z1).getAmbientOcclusionLightValue();
            renderer.aoBrightnessXZNN = block.getMixedBrightnessForBlock(renderer.blockAccess, x1 - 1, y1, z1);
            renderer.aoBrightnessYZNN = block.getMixedBrightnessForBlock(renderer.blockAccess, x1, y1 - 1, z1);
            renderer.aoBrightnessYZPN = block.getMixedBrightnessForBlock(renderer.blockAccess, x1, y1 + 1, z1);
            renderer.aoBrightnessXZPN = block.getMixedBrightnessForBlock(renderer.blockAccess, x1 + 1, y1, z1);
            flag2 = renderer.blockAccess.getBlock(x1 + 1, y1, z1 - 1).getCanBlockGrass();
            flag3 = renderer.blockAccess.getBlock(x1 - 1, y1, z1 - 1).getCanBlockGrass();
            flag4 = renderer.blockAccess.getBlock(x1, y1 + 1, z1 - 1).getCanBlockGrass();
            flag5 = renderer.blockAccess.getBlock(x1, y1 - 1, z1 - 1).getCanBlockGrass();

            if (flag3 || flag5) {
                renderer.aoLightValueScratchXYZNNN = renderer.blockAccess.getBlock(x1 - 1, y1 - 1, z1).getAmbientOcclusionLightValue();
                renderer.aoBrightnessXYZNNN = block.getMixedBrightnessForBlock(renderer.blockAccess, x1 - 1, y1 - 1, z1);
            } else {
                renderer.aoLightValueScratchXYZNNN = renderer.aoLightValueScratchXZNN;
                renderer.aoBrightnessXYZNNN = renderer.aoBrightnessXZNN;
            }

            if (flag3 || flag4) {
                renderer.aoLightValueScratchXYZNPN = renderer.blockAccess.getBlock(x1 - 1, y1 + 1, z1).getAmbientOcclusionLightValue();
                renderer.aoBrightnessXYZNPN = block.getMixedBrightnessForBlock(renderer.blockAccess, x1 - 1, y1 + 1, z1);
            } else {
                renderer.aoLightValueScratchXYZNPN = renderer.aoLightValueScratchXZNN;
                renderer.aoBrightnessXYZNPN = renderer.aoBrightnessXZNN;
            }

            if (flag2 || flag5) {
                renderer.aoLightValueScratchXYZPNN = renderer.blockAccess.getBlock(x1 + 1, y1 - 1, z1).getAmbientOcclusionLightValue();
                renderer.aoBrightnessXYZPNN = block.getMixedBrightnessForBlock(renderer.blockAccess, x1 + 1, y1 - 1, z1);
            } else {
                renderer.aoLightValueScratchXYZPNN = renderer.aoLightValueScratchXZPN;
                renderer.aoBrightnessXYZPNN = renderer.aoBrightnessXZPN;
            }

            if (flag2 || flag4) {
                renderer.aoLightValueScratchXYZPPN = renderer.blockAccess.getBlock(x1 + 1, y1 + 1, z1).getAmbientOcclusionLightValue();
                renderer.aoBrightnessXYZPPN = block.getMixedBrightnessForBlock(renderer.blockAccess, x1 + 1, y1 + 1, z1);
            } else {
                renderer.aoLightValueScratchXYZPPN = renderer.aoLightValueScratchXZPN;
                renderer.aoBrightnessXYZPPN = renderer.aoBrightnessXZPN;
            }

            if (renderer.renderMinZ <= 0.0D) {
                ++z1;
            }

            i1 = l;

            if (renderer.renderMinZ <= 0.0D || !renderer.blockAccess.getBlock(x1, y1, z1 - 1).isOpaqueCube()) {
                i1 = block.getMixedBrightnessForBlock(renderer.blockAccess, x1, y1, z1 - 1);
            }

            f7 = renderer.blockAccess.getBlock(x1, y1, z1 - 1).getAmbientOcclusionLightValue();
            f8 = (renderer.aoLightValueScratchXZNN + renderer.aoLightValueScratchXYZNPN + f7 + renderer.aoLightValueScratchYZPN) / 4.0F;
            f9 = (f7 + renderer.aoLightValueScratchYZPN + renderer.aoLightValueScratchXZPN + renderer.aoLightValueScratchXYZPPN) / 4.0F;
            f10 = (renderer.aoLightValueScratchYZNN + f7 + renderer.aoLightValueScratchXYZPNN + renderer.aoLightValueScratchXZPN) / 4.0F;
            f11 = (renderer.aoLightValueScratchXYZNNN + renderer.aoLightValueScratchXZNN + renderer.aoLightValueScratchYZNN + f7) / 4.0F;
            f3 = (float) ((double) f8 * renderer.renderMaxY * (1.0D - renderer.renderMinX) + (double) f9 * renderer.renderMaxY * renderer.renderMinX + (double) f10 * (1.0D - renderer.renderMaxY) * renderer.renderMinX + (double) f11 * (1.0D - renderer.renderMaxY) * (1.0D - renderer.renderMinX));
            f4 = (float) ((double) f8 * renderer.renderMaxY * (1.0D - renderer.renderMaxX) + (double) f9 * renderer.renderMaxY * renderer.renderMaxX + (double) f10 * (1.0D - renderer.renderMaxY) * renderer.renderMaxX + (double) f11 * (1.0D - renderer.renderMaxY) * (1.0D - renderer.renderMaxX));
            f5 = (float) ((double) f8 * renderer.renderMinY * (1.0D - renderer.renderMaxX) + (double) f9 * renderer.renderMinY * renderer.renderMaxX + (double) f10 * (1.0D - renderer.renderMinY) * renderer.renderMaxX + (double) f11 * (1.0D - renderer.renderMinY) * (1.0D - renderer.renderMaxX));
            f6 = (float) ((double) f8 * renderer.renderMinY * (1.0D - renderer.renderMinX) + (double) f9 * renderer.renderMinY * renderer.renderMinX + (double) f10 * (1.0D - renderer.renderMinY) * renderer.renderMinX + (double) f11 * (1.0D - renderer.renderMinY) * (1.0D - renderer.renderMinX));
            j1 = renderer.getAoBrightness(renderer.aoBrightnessXZNN, renderer.aoBrightnessXYZNPN, renderer.aoBrightnessYZPN, i1);
            k1 = renderer.getAoBrightness(renderer.aoBrightnessYZPN, renderer.aoBrightnessXZPN, renderer.aoBrightnessXYZPPN, i1);
            l1 = renderer.getAoBrightness(renderer.aoBrightnessYZNN, renderer.aoBrightnessXYZPNN, renderer.aoBrightnessXZPN, i1);
            i2 = renderer.getAoBrightness(renderer.aoBrightnessXYZNNN, renderer.aoBrightnessXZNN, renderer.aoBrightnessYZNN, i1);
            renderer.brightnessTopLeft = renderer.mixAoBrightness(j1, k1, l1, i2, renderer.renderMaxY * (1.0D - renderer.renderMinX), renderer.renderMaxY * renderer.renderMinX, (1.0D - renderer.renderMaxY) * renderer.renderMinX, (1.0D - renderer.renderMaxY) * (1.0D - renderer.renderMinX));
            renderer.brightnessBottomLeft = renderer.mixAoBrightness(j1, k1, l1, i2, renderer.renderMaxY * (1.0D - renderer.renderMaxX), renderer.renderMaxY * renderer.renderMaxX, (1.0D - renderer.renderMaxY) * renderer.renderMaxX, (1.0D - renderer.renderMaxY) * (1.0D - renderer.renderMaxX));
            renderer.brightnessBottomRight = renderer.mixAoBrightness(j1, k1, l1, i2, renderer.renderMinY * (1.0D - renderer.renderMaxX), renderer.renderMinY * renderer.renderMaxX, (1.0D - renderer.renderMinY) * renderer.renderMaxX, (1.0D - renderer.renderMinY) * (1.0D - renderer.renderMaxX));
            renderer.brightnessTopRight = renderer.mixAoBrightness(j1, k1, l1, i2, renderer.renderMinY * (1.0D - renderer.renderMinX), renderer.renderMinY * renderer.renderMinX, (1.0D - renderer.renderMinY) * renderer.renderMinX, (1.0D - renderer.renderMinY) * (1.0D - renderer.renderMinX));

            if (flag1) {
                renderer.colorRedTopLeft = renderer.colorRedBottomLeft = renderer.colorRedBottomRight = renderer.colorRedTopRight = r * 0.8F;
                renderer.colorGreenTopLeft = renderer.colorGreenBottomLeft = renderer.colorGreenBottomRight = renderer.colorGreenTopRight = g * 0.8F;
                renderer.colorBlueTopLeft = renderer.colorBlueBottomLeft = renderer.colorBlueBottomRight = renderer.colorBlueTopRight = b * 0.8F;
            } else {
                renderer.colorRedTopLeft = renderer.colorRedBottomLeft = renderer.colorRedBottomRight = renderer.colorRedTopRight = 0.8F;
                renderer.colorGreenTopLeft = renderer.colorGreenBottomLeft = renderer.colorGreenBottomRight = renderer.colorGreenTopRight = 0.8F;
                renderer.colorBlueTopLeft = renderer.colorBlueBottomLeft = renderer.colorBlueBottomRight = renderer.colorBlueTopRight = 0.8F;
            }

            renderer.colorRedTopLeft *= f3;
            renderer.colorGreenTopLeft *= f3;
            renderer.colorBlueTopLeft *= f3;
            renderer.colorRedBottomLeft *= f4;
            renderer.colorGreenBottomLeft *= f4;
            renderer.colorBlueBottomLeft *= f4;
            renderer.colorRedBottomRight *= f5;
            renderer.colorGreenBottomRight *= f5;
            renderer.colorBlueBottomRight *= f5;
            renderer.colorRedTopRight *= f6;
            renderer.colorGreenTopRight *= f6;
            renderer.colorBlueTopRight *= f6;
            renderFaceZNeg(renderer, block, metadata, (double) x1, (double) y1, (double) z1);
            flag = true;
        }

        if (renderer.renderAllFaces || block.shouldSideBeRendered(renderer.blockAccess, x1, y1, z1 + 1, 3)) {
            if (renderer.renderMaxZ >= 1.0D) {
                ++z1;
            }

            renderer.aoLightValueScratchXZNP = renderer.blockAccess.getBlock(x1 - 1, y1, z1).getAmbientOcclusionLightValue();
            renderer.aoLightValueScratchXZPP = renderer.blockAccess.getBlock(x1 + 1, y1, z1).getAmbientOcclusionLightValue();
            renderer.aoLightValueScratchYZNP = renderer.blockAccess.getBlock(x1, y1 - 1, z1).getAmbientOcclusionLightValue();
            renderer.aoLightValueScratchYZPP = renderer.blockAccess.getBlock(x1, y1 + 1, z1).getAmbientOcclusionLightValue();
            renderer.aoBrightnessXZNP = block.getMixedBrightnessForBlock(renderer.blockAccess, x1 - 1, y1, z1);
            renderer.aoBrightnessXZPP = block.getMixedBrightnessForBlock(renderer.blockAccess, x1 + 1, y1, z1);
            renderer.aoBrightnessYZNP = block.getMixedBrightnessForBlock(renderer.blockAccess, x1, y1 - 1, z1);
            renderer.aoBrightnessYZPP = block.getMixedBrightnessForBlock(renderer.blockAccess, x1, y1 + 1, z1);
            flag2 = renderer.blockAccess.getBlock(x1 + 1, y1, z1 + 1).getCanBlockGrass();
            flag3 = renderer.blockAccess.getBlock(x1 - 1, y1, z1 + 1).getCanBlockGrass();
            flag4 = renderer.blockAccess.getBlock(x1, y1 + 1, z1 + 1).getCanBlockGrass();
            flag5 = renderer.blockAccess.getBlock(x1, y1 - 1, z1 + 1).getCanBlockGrass();

            if (flag3 || flag5) {
                renderer.aoLightValueScratchXYZNNP = renderer.blockAccess.getBlock(x1 - 1, y1 - 1, z1).getAmbientOcclusionLightValue();
                renderer.aoBrightnessXYZNNP = block.getMixedBrightnessForBlock(renderer.blockAccess, x1 - 1, y1 - 1, z1);
            } else {
                renderer.aoLightValueScratchXYZNNP = renderer.aoLightValueScratchXZNP;
                renderer.aoBrightnessXYZNNP = renderer.aoBrightnessXZNP;
            }

            if (flag3 || flag4) {
                renderer.aoLightValueScratchXYZNPP = renderer.blockAccess.getBlock(x1 - 1, y1 + 1, z1).getAmbientOcclusionLightValue();
                renderer.aoBrightnessXYZNPP = block.getMixedBrightnessForBlock(renderer.blockAccess, x1 - 1, y1 + 1, z1);
            } else {
                renderer.aoLightValueScratchXYZNPP = renderer.aoLightValueScratchXZNP;
                renderer.aoBrightnessXYZNPP = renderer.aoBrightnessXZNP;
            }

            if (flag2 || flag5) {
                renderer.aoLightValueScratchXYZPNP = renderer.blockAccess.getBlock(x1 + 1, y1 - 1, z1).getAmbientOcclusionLightValue();
                renderer.aoBrightnessXYZPNP = block.getMixedBrightnessForBlock(renderer.blockAccess, x1 + 1, y1 - 1, z1);
            } else {
                renderer.aoLightValueScratchXYZPNP = renderer.aoLightValueScratchXZPP;
                renderer.aoBrightnessXYZPNP = renderer.aoBrightnessXZPP;
            }

            if (flag2 || flag4) {
                renderer.aoLightValueScratchXYZPPP = renderer.blockAccess.getBlock(x1 + 1, y1 + 1, z1).getAmbientOcclusionLightValue();
                renderer.aoBrightnessXYZPPP = block.getMixedBrightnessForBlock(renderer.blockAccess, x1 + 1, y1 + 1, z1);
            } else {
                renderer.aoLightValueScratchXYZPPP = renderer.aoLightValueScratchXZPP;
                renderer.aoBrightnessXYZPPP = renderer.aoBrightnessXZPP;
            }

            if (renderer.renderMaxZ >= 1.0D) {
                --z1;
            }

            i1 = l;

            if (renderer.renderMaxZ >= 1.0D || !renderer.blockAccess.getBlock(x1, y1, z1 + 1).isOpaqueCube()) {
                i1 = block.getMixedBrightnessForBlock(renderer.blockAccess, x1, y1, z1 + 1);
            }

            f7 = renderer.blockAccess.getBlock(x1, y1, z1 + 1).getAmbientOcclusionLightValue();
            f8 = (renderer.aoLightValueScratchXZNP + renderer.aoLightValueScratchXYZNPP + f7 + renderer.aoLightValueScratchYZPP) / 4.0F;
            f9 = (f7 + renderer.aoLightValueScratchYZPP + renderer.aoLightValueScratchXZPP + renderer.aoLightValueScratchXYZPPP) / 4.0F;
            f10 = (renderer.aoLightValueScratchYZNP + f7 + renderer.aoLightValueScratchXYZPNP + renderer.aoLightValueScratchXZPP) / 4.0F;
            f11 = (renderer.aoLightValueScratchXYZNNP + renderer.aoLightValueScratchXZNP + renderer.aoLightValueScratchYZNP + f7) / 4.0F;
            f3 = (float) ((double) f8 * renderer.renderMaxY * (1.0D - renderer.renderMinX) + (double) f9 * renderer.renderMaxY * renderer.renderMinX + (double) f10 * (1.0D - renderer.renderMaxY) * renderer.renderMinX + (double) f11 * (1.0D - renderer.renderMaxY) * (1.0D - renderer.renderMinX));
            f4 = (float) ((double) f8 * renderer.renderMinY * (1.0D - renderer.renderMinX) + (double) f9 * renderer.renderMinY * renderer.renderMinX + (double) f10 * (1.0D - renderer.renderMinY) * renderer.renderMinX + (double) f11 * (1.0D - renderer.renderMinY) * (1.0D - renderer.renderMinX));
            f5 = (float) ((double) f8 * renderer.renderMinY * (1.0D - renderer.renderMaxX) + (double) f9 * renderer.renderMinY * renderer.renderMaxX + (double) f10 * (1.0D - renderer.renderMinY) * renderer.renderMaxX + (double) f11 * (1.0D - renderer.renderMinY) * (1.0D - renderer.renderMaxX));
            f6 = (float) ((double) f8 * renderer.renderMaxY * (1.0D - renderer.renderMaxX) + (double) f9 * renderer.renderMaxY * renderer.renderMaxX + (double) f10 * (1.0D - renderer.renderMaxY) * renderer.renderMaxX + (double) f11 * (1.0D - renderer.renderMaxY) * (1.0D - renderer.renderMaxX));
            j1 = renderer.getAoBrightness(renderer.aoBrightnessXZNP, renderer.aoBrightnessXYZNPP, renderer.aoBrightnessYZPP, i1);
            k1 = renderer.getAoBrightness(renderer.aoBrightnessYZPP, renderer.aoBrightnessXZPP, renderer.aoBrightnessXYZPPP, i1);
            l1 = renderer.getAoBrightness(renderer.aoBrightnessYZNP, renderer.aoBrightnessXYZPNP, renderer.aoBrightnessXZPP, i1);
            i2 = renderer.getAoBrightness(renderer.aoBrightnessXYZNNP, renderer.aoBrightnessXZNP, renderer.aoBrightnessYZNP, i1);
            renderer.brightnessTopLeft = renderer.mixAoBrightness(j1, i2, l1, k1, renderer.renderMaxY * (1.0D - renderer.renderMinX), (1.0D - renderer.renderMaxY) * (1.0D - renderer.renderMinX), (1.0D - renderer.renderMaxY) * renderer.renderMinX, renderer.renderMaxY * renderer.renderMinX);
            renderer.brightnessBottomLeft = renderer.mixAoBrightness(j1, i2, l1, k1, renderer.renderMinY * (1.0D - renderer.renderMinX), (1.0D - renderer.renderMinY) * (1.0D - renderer.renderMinX), (1.0D - renderer.renderMinY) * renderer.renderMinX, renderer.renderMinY * renderer.renderMinX);
            renderer.brightnessBottomRight = renderer.mixAoBrightness(j1, i2, l1, k1, renderer.renderMinY * (1.0D - renderer.renderMaxX), (1.0D - renderer.renderMinY) * (1.0D - renderer.renderMaxX), (1.0D - renderer.renderMinY) * renderer.renderMaxX, renderer.renderMinY * renderer.renderMaxX);
            renderer.brightnessTopRight = renderer.mixAoBrightness(j1, i2, l1, k1, renderer.renderMaxY * (1.0D - renderer.renderMaxX), (1.0D - renderer.renderMaxY) * (1.0D - renderer.renderMaxX), (1.0D - renderer.renderMaxY) * renderer.renderMaxX, renderer.renderMaxY * renderer.renderMaxX);

            if (flag1) {
                renderer.colorRedTopLeft = renderer.colorRedBottomLeft = renderer.colorRedBottomRight = renderer.colorRedTopRight = r * 0.8F;
                renderer.colorGreenTopLeft = renderer.colorGreenBottomLeft = renderer.colorGreenBottomRight = renderer.colorGreenTopRight = g * 0.8F;
                renderer.colorBlueTopLeft = renderer.colorBlueBottomLeft = renderer.colorBlueBottomRight = renderer.colorBlueTopRight = b * 0.8F;
            } else {
                renderer.colorRedTopLeft = renderer.colorRedBottomLeft = renderer.colorRedBottomRight = renderer.colorRedTopRight = 0.8F;
                renderer.colorGreenTopLeft = renderer.colorGreenBottomLeft = renderer.colorGreenBottomRight = renderer.colorGreenTopRight = 0.8F;
                renderer.colorBlueTopLeft = renderer.colorBlueBottomLeft = renderer.colorBlueBottomRight = renderer.colorBlueTopRight = 0.8F;
            }

            renderer.colorRedTopLeft *= f3;
            renderer.colorGreenTopLeft *= f3;
            renderer.colorBlueTopLeft *= f3;
            renderer.colorRedBottomLeft *= f4;
            renderer.colorGreenBottomLeft *= f4;
            renderer.colorBlueBottomLeft *= f4;
            renderer.colorRedBottomRight *= f5;
            renderer.colorGreenBottomRight *= f5;
            renderer.colorBlueBottomRight *= f5;
            renderer.colorRedTopRight *= f6;
            renderer.colorGreenTopRight *= f6;
            renderer.colorBlueTopRight *= f6;
            renderFaceZPos(renderer, block, metadata, (double) x1, (double) y1, (double) z1);
            flag = true;
        }

        if (renderer.renderAllFaces || block.shouldSideBeRendered(renderer.blockAccess, x1 - 1, y1, z1, 4)) {
            if (renderer.renderMinX <= 0.0D) {
                --x1;
            }

            renderer.aoLightValueScratchXYNN = renderer.blockAccess.getBlock(x1, y1 - 1, z1).getAmbientOcclusionLightValue();
            renderer.aoLightValueScratchXZNN = renderer.blockAccess.getBlock(x1, y1, z1 - 1).getAmbientOcclusionLightValue();
            renderer.aoLightValueScratchXZNP = renderer.blockAccess.getBlock(x1, y1, z1 + 1).getAmbientOcclusionLightValue();
            renderer.aoLightValueScratchXYNP = renderer.blockAccess.getBlock(x1, y1 + 1, z1).getAmbientOcclusionLightValue();
            renderer.aoBrightnessXYNN = block.getMixedBrightnessForBlock(renderer.blockAccess, x1, y1 - 1, z1);
            renderer.aoBrightnessXZNN = block.getMixedBrightnessForBlock(renderer.blockAccess, x1, y1, z1 - 1);
            renderer.aoBrightnessXZNP = block.getMixedBrightnessForBlock(renderer.blockAccess, x1, y1, z1 + 1);
            renderer.aoBrightnessXYNP = block.getMixedBrightnessForBlock(renderer.blockAccess, x1, y1 + 1, z1);
            flag2 = renderer.blockAccess.getBlock(x1 - 1, y1 + 1, z1).getCanBlockGrass();
            flag3 = renderer.blockAccess.getBlock(x1 - 1, y1 - 1, z1).getCanBlockGrass();
            flag4 = renderer.blockAccess.getBlock(x1 - 1, y1, z1 - 1).getCanBlockGrass();
            flag5 = renderer.blockAccess.getBlock(x1 - 1, y1, z1 + 1).getCanBlockGrass();

            if (flag4 || flag3) {
                renderer.aoLightValueScratchXYZNNN = renderer.blockAccess.getBlock(x1, y1 - 1, z1 - 1).getAmbientOcclusionLightValue();
                renderer.aoBrightnessXYZNNN = block.getMixedBrightnessForBlock(renderer.blockAccess, x1, y1 - 1, z1 - 1);
            } else {
                renderer.aoLightValueScratchXYZNNN = renderer.aoLightValueScratchXZNN;
                renderer.aoBrightnessXYZNNN = renderer.aoBrightnessXZNN;
            }

            if (flag5 || flag3) {
                renderer.aoLightValueScratchXYZNNP = renderer.blockAccess.getBlock(x1, y1 - 1, z1 + 1).getAmbientOcclusionLightValue();
                renderer.aoBrightnessXYZNNP = block.getMixedBrightnessForBlock(renderer.blockAccess, x1, y1 - 1, z1 + 1);
            } else {
                renderer.aoLightValueScratchXYZNNP = renderer.aoLightValueScratchXZNP;
                renderer.aoBrightnessXYZNNP = renderer.aoBrightnessXZNP;
            }

            if (flag4 || flag2) {
                renderer.aoLightValueScratchXYZNPN = renderer.blockAccess.getBlock(x1, y1 + 1, z1 - 1).getAmbientOcclusionLightValue();
                renderer.aoBrightnessXYZNPN = block.getMixedBrightnessForBlock(renderer.blockAccess, x1, y1 + 1, z1 - 1);
            } else {
                renderer.aoLightValueScratchXYZNPN = renderer.aoLightValueScratchXZNN;
                renderer.aoBrightnessXYZNPN = renderer.aoBrightnessXZNN;
            }

            if (flag5 || flag2) {
                renderer.aoLightValueScratchXYZNPP = renderer.blockAccess.getBlock(x1, y1 + 1, z1 + 1).getAmbientOcclusionLightValue();
                renderer.aoBrightnessXYZNPP = block.getMixedBrightnessForBlock(renderer.blockAccess, x1, y1 + 1, z1 + 1);
            } else {
                renderer.aoLightValueScratchXYZNPP = renderer.aoLightValueScratchXZNP;
                renderer.aoBrightnessXYZNPP = renderer.aoBrightnessXZNP;
            }

            if (renderer.renderMinX <= 0.0D) {
                ++x1;
            }

            i1 = l;

            if (renderer.renderMinX <= 0.0D || !renderer.blockAccess.getBlock(x1 - 1, y1, z1).isOpaqueCube()) {
                i1 = block.getMixedBrightnessForBlock(renderer.blockAccess, x1 - 1, y1, z1);
            }

            f7 = renderer.blockAccess.getBlock(x1 - 1, y1, z1).getAmbientOcclusionLightValue();
            f8 = (renderer.aoLightValueScratchXYNN + renderer.aoLightValueScratchXYZNNP + f7 + renderer.aoLightValueScratchXZNP) / 4.0F;
            f9 = (f7 + renderer.aoLightValueScratchXZNP + renderer.aoLightValueScratchXYNP + renderer.aoLightValueScratchXYZNPP) / 4.0F;
            f10 = (renderer.aoLightValueScratchXZNN + f7 + renderer.aoLightValueScratchXYZNPN + renderer.aoLightValueScratchXYNP) / 4.0F;
            f11 = (renderer.aoLightValueScratchXYZNNN + renderer.aoLightValueScratchXYNN + renderer.aoLightValueScratchXZNN + f7) / 4.0F;
            f3 = (float) ((double) f9 * renderer.renderMaxY * renderer.renderMaxZ + (double) f10 * renderer.renderMaxY * (1.0D - renderer.renderMaxZ) + (double) f11 * (1.0D - renderer.renderMaxY) * (1.0D - renderer.renderMaxZ) + (double) f8 * (1.0D - renderer.renderMaxY) * renderer.renderMaxZ);
            f4 = (float) ((double) f9 * renderer.renderMaxY * renderer.renderMinZ + (double) f10 * renderer.renderMaxY * (1.0D - renderer.renderMinZ) + (double) f11 * (1.0D - renderer.renderMaxY) * (1.0D - renderer.renderMinZ) + (double) f8 * (1.0D - renderer.renderMaxY) * renderer.renderMinZ);
            f5 = (float) ((double) f9 * renderer.renderMinY * renderer.renderMinZ + (double) f10 * renderer.renderMinY * (1.0D - renderer.renderMinZ) + (double) f11 * (1.0D - renderer.renderMinY) * (1.0D - renderer.renderMinZ) + (double) f8 * (1.0D - renderer.renderMinY) * renderer.renderMinZ);
            f6 = (float) ((double) f9 * renderer.renderMinY * renderer.renderMaxZ + (double) f10 * renderer.renderMinY * (1.0D - renderer.renderMaxZ) + (double) f11 * (1.0D - renderer.renderMinY) * (1.0D - renderer.renderMaxZ) + (double) f8 * (1.0D - renderer.renderMinY) * renderer.renderMaxZ);
            j1 = renderer.getAoBrightness(renderer.aoBrightnessXYNN, renderer.aoBrightnessXYZNNP, renderer.aoBrightnessXZNP, i1);
            k1 = renderer.getAoBrightness(renderer.aoBrightnessXZNP, renderer.aoBrightnessXYNP, renderer.aoBrightnessXYZNPP, i1);
            l1 = renderer.getAoBrightness(renderer.aoBrightnessXZNN, renderer.aoBrightnessXYZNPN, renderer.aoBrightnessXYNP, i1);
            i2 = renderer.getAoBrightness(renderer.aoBrightnessXYZNNN, renderer.aoBrightnessXYNN, renderer.aoBrightnessXZNN, i1);
            renderer.brightnessTopLeft = renderer.mixAoBrightness(k1, l1, i2, j1, renderer.renderMaxY * renderer.renderMaxZ, renderer.renderMaxY * (1.0D - renderer.renderMaxZ), (1.0D - renderer.renderMaxY) * (1.0D - renderer.renderMaxZ), (1.0D - renderer.renderMaxY) * renderer.renderMaxZ);
            renderer.brightnessBottomLeft = renderer.mixAoBrightness(k1, l1, i2, j1, renderer.renderMaxY * renderer.renderMinZ, renderer.renderMaxY * (1.0D - renderer.renderMinZ), (1.0D - renderer.renderMaxY) * (1.0D - renderer.renderMinZ), (1.0D - renderer.renderMaxY) * renderer.renderMinZ);
            renderer.brightnessBottomRight = renderer.mixAoBrightness(k1, l1, i2, j1, renderer.renderMinY * renderer.renderMinZ, renderer.renderMinY * (1.0D - renderer.renderMinZ), (1.0D - renderer.renderMinY) * (1.0D - renderer.renderMinZ), (1.0D - renderer.renderMinY) * renderer.renderMinZ);
            renderer.brightnessTopRight = renderer.mixAoBrightness(k1, l1, i2, j1, renderer.renderMinY * renderer.renderMaxZ, renderer.renderMinY * (1.0D - renderer.renderMaxZ), (1.0D - renderer.renderMinY) * (1.0D - renderer.renderMaxZ), (1.0D - renderer.renderMinY) * renderer.renderMaxZ);

            if (flag1) {
                renderer.colorRedTopLeft = renderer.colorRedBottomLeft = renderer.colorRedBottomRight = renderer.colorRedTopRight = r * 0.6F;
                renderer.colorGreenTopLeft = renderer.colorGreenBottomLeft = renderer.colorGreenBottomRight = renderer.colorGreenTopRight = g * 0.6F;
                renderer.colorBlueTopLeft = renderer.colorBlueBottomLeft = renderer.colorBlueBottomRight = renderer.colorBlueTopRight = b * 0.6F;
            } else {
                renderer.colorRedTopLeft = renderer.colorRedBottomLeft = renderer.colorRedBottomRight = renderer.colorRedTopRight = 0.6F;
                renderer.colorGreenTopLeft = renderer.colorGreenBottomLeft = renderer.colorGreenBottomRight = renderer.colorGreenTopRight = 0.6F;
                renderer.colorBlueTopLeft = renderer.colorBlueBottomLeft = renderer.colorBlueBottomRight = renderer.colorBlueTopRight = 0.6F;
            }

            renderer.colorRedTopLeft *= f3;
            renderer.colorGreenTopLeft *= f3;
            renderer.colorBlueTopLeft *= f3;
            renderer.colorRedBottomLeft *= f4;
            renderer.colorGreenBottomLeft *= f4;
            renderer.colorBlueBottomLeft *= f4;
            renderer.colorRedBottomRight *= f5;
            renderer.colorGreenBottomRight *= f5;
            renderer.colorBlueBottomRight *= f5;
            renderer.colorRedTopRight *= f6;
            renderer.colorGreenTopRight *= f6;
            renderer.colorBlueTopRight *= f6;
            renderFaceXNeg(renderer, block, metadata, (double) x1, (double) y1, (double) z1);
            flag = true;
        }

        if (renderer.renderAllFaces || block.shouldSideBeRendered(renderer.blockAccess, x1 + 1, y1, z1, 5)) {
            if (renderer.renderMaxX >= 1.0D) {
                ++x1;
            }

            renderer.aoLightValueScratchXYPN = renderer.blockAccess.getBlock(x1, y1 - 1, z1).getAmbientOcclusionLightValue();
            renderer.aoLightValueScratchXZPN = renderer.blockAccess.getBlock(x1, y1, z1 - 1).getAmbientOcclusionLightValue();
            renderer.aoLightValueScratchXZPP = renderer.blockAccess.getBlock(x1, y1, z1 + 1).getAmbientOcclusionLightValue();
            renderer.aoLightValueScratchXYPP = renderer.blockAccess.getBlock(x1, y1 + 1, z1).getAmbientOcclusionLightValue();
            renderer.aoBrightnessXYPN = block.getMixedBrightnessForBlock(renderer.blockAccess, x1, y1 - 1, z1);
            renderer.aoBrightnessXZPN = block.getMixedBrightnessForBlock(renderer.blockAccess, x1, y1, z1 - 1);
            renderer.aoBrightnessXZPP = block.getMixedBrightnessForBlock(renderer.blockAccess, x1, y1, z1 + 1);
            renderer.aoBrightnessXYPP = block.getMixedBrightnessForBlock(renderer.blockAccess, x1, y1 + 1, z1);
            flag2 = renderer.blockAccess.getBlock(x1 + 1, y1 + 1, z1).getCanBlockGrass();
            flag3 = renderer.blockAccess.getBlock(x1 + 1, y1 - 1, z1).getCanBlockGrass();
            flag4 = renderer.blockAccess.getBlock(x1 + 1, y1, z1 + 1).getCanBlockGrass();
            flag5 = renderer.blockAccess.getBlock(x1 + 1, y1, z1 - 1).getCanBlockGrass();

            if (flag3 || flag5) {
                renderer.aoLightValueScratchXYZPNN = renderer.blockAccess.getBlock(x1, y1 - 1, z1 - 1).getAmbientOcclusionLightValue();
                renderer.aoBrightnessXYZPNN = block.getMixedBrightnessForBlock(renderer.blockAccess, x1, y1 - 1, z1 - 1);
            } else {
                renderer.aoLightValueScratchXYZPNN = renderer.aoLightValueScratchXZPN;
                renderer.aoBrightnessXYZPNN = renderer.aoBrightnessXZPN;
            }

            if (flag3 || flag4) {
                renderer.aoLightValueScratchXYZPNP = renderer.blockAccess.getBlock(x1, y1 - 1, z1 + 1).getAmbientOcclusionLightValue();
                renderer.aoBrightnessXYZPNP = block.getMixedBrightnessForBlock(renderer.blockAccess, x1, y1 - 1, z1 + 1);
            } else {
                renderer.aoLightValueScratchXYZPNP = renderer.aoLightValueScratchXZPP;
                renderer.aoBrightnessXYZPNP = renderer.aoBrightnessXZPP;
            }

            if (flag2 || flag5) {
                renderer.aoLightValueScratchXYZPPN = renderer.blockAccess.getBlock(x1, y1 + 1, z1 - 1).getAmbientOcclusionLightValue();
                renderer.aoBrightnessXYZPPN = block.getMixedBrightnessForBlock(renderer.blockAccess, x1, y1 + 1, z1 - 1);
            } else {
                renderer.aoLightValueScratchXYZPPN = renderer.aoLightValueScratchXZPN;
                renderer.aoBrightnessXYZPPN = renderer.aoBrightnessXZPN;
            }

            if (flag2 || flag4) {
                renderer.aoLightValueScratchXYZPPP = renderer.blockAccess.getBlock(x1, y1 + 1, z1 + 1).getAmbientOcclusionLightValue();
                renderer.aoBrightnessXYZPPP = block.getMixedBrightnessForBlock(renderer.blockAccess, x1, y1 + 1, z1 + 1);
            } else {
                renderer.aoLightValueScratchXYZPPP = renderer.aoLightValueScratchXZPP;
                renderer.aoBrightnessXYZPPP = renderer.aoBrightnessXZPP;
            }

            if (renderer.renderMaxX >= 1.0D) {
                --x1;
            }

            i1 = l;

            if (renderer.renderMaxX >= 1.0D || !renderer.blockAccess.getBlock(x1 + 1, y1, z1).isOpaqueCube()) {
                i1 = block.getMixedBrightnessForBlock(renderer.blockAccess, x1 + 1, y1, z1);
            }

            f7 = renderer.blockAccess.getBlock(x1 + 1, y1, z1).getAmbientOcclusionLightValue();
            f8 = (renderer.aoLightValueScratchXYPN + renderer.aoLightValueScratchXYZPNP + f7 + renderer.aoLightValueScratchXZPP) / 4.0F;
            f9 = (renderer.aoLightValueScratchXYZPNN + renderer.aoLightValueScratchXYPN + renderer.aoLightValueScratchXZPN + f7) / 4.0F;
            f10 = (renderer.aoLightValueScratchXZPN + f7 + renderer.aoLightValueScratchXYZPPN + renderer.aoLightValueScratchXYPP) / 4.0F;
            f11 = (f7 + renderer.aoLightValueScratchXZPP + renderer.aoLightValueScratchXYPP + renderer.aoLightValueScratchXYZPPP) / 4.0F;
            f3 = (float) ((double) f8 * (1.0D - renderer.renderMinY) * renderer.renderMaxZ + (double) f9 * (1.0D - renderer.renderMinY) * (1.0D - renderer.renderMaxZ) + (double) f10 * renderer.renderMinY * (1.0D - renderer.renderMaxZ) + (double) f11 * renderer.renderMinY * renderer.renderMaxZ);
            f4 = (float) ((double) f8 * (1.0D - renderer.renderMinY) * renderer.renderMinZ + (double) f9 * (1.0D - renderer.renderMinY) * (1.0D - renderer.renderMinZ) + (double) f10 * renderer.renderMinY * (1.0D - renderer.renderMinZ) + (double) f11 * renderer.renderMinY * renderer.renderMinZ);
            f5 = (float) ((double) f8 * (1.0D - renderer.renderMaxY) * renderer.renderMinZ + (double) f9 * (1.0D - renderer.renderMaxY) * (1.0D - renderer.renderMinZ) + (double) f10 * renderer.renderMaxY * (1.0D - renderer.renderMinZ) + (double) f11 * renderer.renderMaxY * renderer.renderMinZ);
            f6 = (float) ((double) f8 * (1.0D - renderer.renderMaxY) * renderer.renderMaxZ + (double) f9 * (1.0D - renderer.renderMaxY) * (1.0D - renderer.renderMaxZ) + (double) f10 * renderer.renderMaxY * (1.0D - renderer.renderMaxZ) + (double) f11 * renderer.renderMaxY * renderer.renderMaxZ);
            j1 = renderer.getAoBrightness(renderer.aoBrightnessXYPN, renderer.aoBrightnessXYZPNP, renderer.aoBrightnessXZPP, i1);
            k1 = renderer.getAoBrightness(renderer.aoBrightnessXZPP, renderer.aoBrightnessXYPP, renderer.aoBrightnessXYZPPP, i1);
            l1 = renderer.getAoBrightness(renderer.aoBrightnessXZPN, renderer.aoBrightnessXYZPPN, renderer.aoBrightnessXYPP, i1);
            i2 = renderer.getAoBrightness(renderer.aoBrightnessXYZPNN, renderer.aoBrightnessXYPN, renderer.aoBrightnessXZPN, i1);
            renderer.brightnessTopLeft = renderer.mixAoBrightness(j1, i2, l1, k1, (1.0D - renderer.renderMinY) * renderer.renderMaxZ, (1.0D - renderer.renderMinY) * (1.0D - renderer.renderMaxZ), renderer.renderMinY * (1.0D - renderer.renderMaxZ), renderer.renderMinY * renderer.renderMaxZ);
            renderer.brightnessBottomLeft = renderer.mixAoBrightness(j1, i2, l1, k1, (1.0D - renderer.renderMinY) * renderer.renderMinZ, (1.0D - renderer.renderMinY) * (1.0D - renderer.renderMinZ), renderer.renderMinY * (1.0D - renderer.renderMinZ), renderer.renderMinY * renderer.renderMinZ);
            renderer.brightnessBottomRight = renderer.mixAoBrightness(j1, i2, l1, k1, (1.0D - renderer.renderMaxY) * renderer.renderMinZ, (1.0D - renderer.renderMaxY) * (1.0D - renderer.renderMinZ), renderer.renderMaxY * (1.0D - renderer.renderMinZ), renderer.renderMaxY * renderer.renderMinZ);
            renderer.brightnessTopRight = renderer.mixAoBrightness(j1, i2, l1, k1, (1.0D - renderer.renderMaxY) * renderer.renderMaxZ, (1.0D - renderer.renderMaxY) * (1.0D - renderer.renderMaxZ), renderer.renderMaxY * (1.0D - renderer.renderMaxZ), renderer.renderMaxY * renderer.renderMaxZ);

            if (flag1) {
                renderer.colorRedTopLeft = renderer.colorRedBottomLeft = renderer.colorRedBottomRight = renderer.colorRedTopRight = r * 0.6F;
                renderer.colorGreenTopLeft = renderer.colorGreenBottomLeft = renderer.colorGreenBottomRight = renderer.colorGreenTopRight = g * 0.6F;
                renderer.colorBlueTopLeft = renderer.colorBlueBottomLeft = renderer.colorBlueBottomRight = renderer.colorBlueTopRight = b * 0.6F;
            } else {
                renderer.colorRedTopLeft = renderer.colorRedBottomLeft = renderer.colorRedBottomRight = renderer.colorRedTopRight = 0.6F;
                renderer.colorGreenTopLeft = renderer.colorGreenBottomLeft = renderer.colorGreenBottomRight = renderer.colorGreenTopRight = 0.6F;
                renderer.colorBlueTopLeft = renderer.colorBlueBottomLeft = renderer.colorBlueBottomRight = renderer.colorBlueTopRight = 0.6F;
            }

            renderer.colorRedTopLeft *= f3;
            renderer.colorGreenTopLeft *= f3;
            renderer.colorBlueTopLeft *= f3;
            renderer.colorRedBottomLeft *= f4;
            renderer.colorGreenBottomLeft *= f4;
            renderer.colorBlueBottomLeft *= f4;
            renderer.colorRedBottomRight *= f5;
            renderer.colorGreenBottomRight *= f5;
            renderer.colorBlueBottomRight *= f5;
            renderer.colorRedTopRight *= f6;
            renderer.colorGreenTopRight *= f6;
            renderer.colorBlueTopRight *= f6;
            renderFaceXPos(renderer, block, metadata, (double) x1, (double) y1, (double) z1);
            flag = true;
        }

        renderer.enableAO = false;
        return flag;
    }

    private boolean renderLayeredBlockWithColorMultiplier(final RenderBlocks renderer, final Block block, final int metadata, final int x, final int y, final int z, final float r, final float g, final float b) {
        renderer.enableAO = false;
        final Tessellator tessellator = Tessellator.instance;
        boolean flag = false;
        final float f7 = 1.0F * r;
        final float f8 = 1.0F * g;
        final float f9 = 1.0F * b;
        final float f10 = 0.5F * r;
        final float f11 = 0.8F * r;
        final float f12 = 0.6F * r;
        final float f13 = 0.5F * g;
        final float f14 = 0.8F * g;
        final float f15 = 0.6F * g;
        final float f16 = 0.5F * b;
        final float f17 = 0.8F * b;
        final float f18 = 0.6F * b;

        final int l = block.getMixedBrightnessForBlock(renderer.blockAccess, x, y, z);

        if (renderer.renderAllFaces || block.shouldSideBeRendered(renderer.blockAccess, x, y - 1, z, 0)) {
            tessellator.setBrightness(renderer.renderMinY > 0.0D ? l : block.getMixedBrightnessForBlock(renderer.blockAccess, x, y - 1, z));
            tessellator.setColorOpaque_F(f10, f13, f16);
            renderFaceYNeg(renderer, block, metadata, (double) x, (double) y, (double) z);
            flag = true;
        }

        if (renderer.renderAllFaces || block.shouldSideBeRendered(renderer.blockAccess, x, y + 1, z, 1)) {
            tessellator.setBrightness(renderer.renderMaxY < 1.0D ? l : block.getMixedBrightnessForBlock(renderer.blockAccess, x, y + 1, z));
            tessellator.setColorOpaque_F(f7, f8, f9);
            renderFaceYPos(renderer, block, metadata, (double) x, (double) y, (double) z);
            flag = true;
        }

        if (renderer.renderAllFaces || block.shouldSideBeRendered(renderer.blockAccess, x, y, z - 1, 2)) {
            tessellator.setBrightness(renderer.renderMinZ > 0.0D ? l : block.getMixedBrightnessForBlock(renderer.blockAccess, x, y, z - 1));
            tessellator.setColorOpaque_F(f11, f14, f17);
            renderFaceZNeg(renderer, block, metadata, (double) x, (double) y, (double) z);
            flag = true;
        }

        if (renderer.renderAllFaces || block.shouldSideBeRendered(renderer.blockAccess, x, y, z + 1, 3)) {
            tessellator.setBrightness(renderer.renderMaxZ < 1.0D ? l : block.getMixedBrightnessForBlock(renderer.blockAccess, x, y, z + 1));
            tessellator.setColorOpaque_F(f11, f14, f17);
            renderFaceZPos(renderer, block, metadata, (double) x, (double) y, (double) z);
            flag = true;
        }

        if (renderer.renderAllFaces || block.shouldSideBeRendered(renderer.blockAccess, x - 1, y, z, 4)) {
            tessellator.setBrightness(renderer.renderMinX > 0.0D ? l : block.getMixedBrightnessForBlock(renderer.blockAccess, x - 1, y, z));
            tessellator.setColorOpaque_F(f12, f15, f18);
            renderFaceXNeg(renderer, block, metadata, (double) x, (double) y, (double) z);
            flag = true;
        }

        if (renderer.renderAllFaces || block.shouldSideBeRendered(renderer.blockAccess, x + 1, y, z, 5)) {
            tessellator.setBrightness(renderer.renderMaxX < 1.0D ? l : block.getMixedBrightnessForBlock(renderer.blockAccess, x + 1, y, z));
            tessellator.setColorOpaque_F(f12, f15, f18);
            renderFaceXPos(renderer, block, metadata, (double) x, (double) y, (double) z);
            flag = true;
        }

        return flag;
    }
    
    private void renderFaceYNeg(final RenderBlocks renderer, final Block block, final int metadata, final double x, final double y, final double z) {
        final ILayeredBlock layers = (ILayeredBlock) block;
        renderer.renderFaceYNeg(block, x, y, z, renderer.getBlockIconFromSideAndMetadata(block, 0, metadata));
        for (int i = 1; i <= layers.numLayers(renderer.blockAccess, block, (int) x, (int) y, (int) z, metadata); i++) {
            final IIcon layer = layers.getLayer(renderer.blockAccess, block, (int) x, (int) y, (int) z, 0, metadata, i);
            if (layer != null) {
                renderer.renderFaceYNeg(block, x, y - i * 0.00001D, z, layer);
            }
        }
    }

    private void renderFaceYPos(final RenderBlocks renderer, final Block block, final int metadata, final double x, final double y, final double z) {
        final ILayeredBlock layers = (ILayeredBlock) block;
        renderer.renderFaceYPos(block, x, y, z, renderer.getBlockIconFromSideAndMetadata(block, 1, metadata));
        for (int i = 1; i <= layers.numLayers(renderer.blockAccess, block, (int) x, (int) y, (int) z, metadata); i++) {
            final IIcon layer = layers.getLayer(renderer.blockAccess, block, (int) x, (int) y, (int) z, 1, metadata, i);
            if (layer != null) {
                renderer.renderFaceYPos(block, x, y + i * 0.00001D, z, layer);
            }
        }
    }

    private void renderFaceZNeg(final RenderBlocks renderer, final Block block, final int metadata, final double x, final double y, final double z) {
        final ILayeredBlock layers = (ILayeredBlock) block;
        renderer.renderFaceZNeg(block, x, y, z, renderer.getBlockIconFromSideAndMetadata(block, 2, metadata));
        for (int i = 1; i <= layers.numLayers(renderer.blockAccess, block, (int) x, (int) y, (int) z, metadata); i++) {
            final IIcon layer = layers.getLayer(renderer.blockAccess, block, (int) x, (int) y, (int) z, 2, metadata, i);
            if (layer != null) {
                renderer.renderFaceZNeg(block, x, y, z - i * 0.00001D, layer);
            }
        }
    }
    
    private void renderFaceZPos(final RenderBlocks renderer, final Block block, final int metadata, final double x, final double y, final double z) {
        final ILayeredBlock layers = (ILayeredBlock) block;
        renderer.renderFaceZPos(block, x, y, z, renderer.getBlockIconFromSideAndMetadata(block, 3, metadata));
        for (int i = 1; i <= layers.numLayers(renderer.blockAccess, block, (int) x, (int) y, (int) z, metadata); i++) {
            final IIcon layer = layers.getLayer(renderer.blockAccess, block, (int) x, (int) y, (int) z, 3, metadata, i);
            if (layer != null) {
                renderer.renderFaceZPos(block, x, y, z + i * 0.00001D, layer);
            }
        }
    }
    
    private void renderFaceXNeg(final RenderBlocks renderer, final Block block, final int metadata, final double x, final double y, final double z) {
        final ILayeredBlock layers = (ILayeredBlock) block;
        renderer.renderFaceXNeg(block, x, y, z, renderer.getBlockIconFromSideAndMetadata(block, 4, metadata));
        for (int i = 1; i <= layers.numLayers(renderer.blockAccess, block, (int) x, (int) y, (int) z, metadata); i++) {
            final IIcon layer = layers.getLayer(renderer.blockAccess, block, (int) x, (int) y, (int) z, 4, metadata, i);
            if (layer != null) {
                renderer.renderFaceXNeg(block, x - i * 0.00001D, y, z, layer);
            }
        }
    }
    
    private void renderFaceXPos(final RenderBlocks renderer, final Block block, final int metadata, final double x, final double y, final double z) {
        final ILayeredBlock layers = (ILayeredBlock) block;
        renderer.renderFaceXPos(block, x, y, z, renderer.getBlockIconFromSideAndMetadata(block, 5, metadata));
        for (int i = 1; i <= layers.numLayers(renderer.blockAccess, block, (int) x, (int) y, (int) z, metadata); i++) {
            final IIcon layer = layers.getLayer(renderer.blockAccess, block, (int) x, (int) y, (int) z, 5, metadata, i);
            if (layer != null) {
                renderer.renderFaceXPos(block, x + i * 0.00001D, y, z, layer);
            }
        }
    }
    
    private void renderFaceYNegInv(final RenderBlocks renderer, final Block block, final int metadata) {
        final ILayeredBlock layers = (ILayeredBlock) block;
        renderer.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 0, metadata));
        for (int i = 1; i <= layers.numLayers(block, metadata); i++) {
            if (layers.getLayer(block, 0, metadata, i) != null) {
                renderer.renderFaceYNeg(block, 0.0D, 0.0D - i * 0.00001D, 0.0D, layers.getLayer(block, 0, metadata, i));
            }
        }
    }

    private void renderFaceYPosInv(final RenderBlocks renderer, final Block block, final int metadata) {
        final ILayeredBlock layers = (ILayeredBlock) block;
        renderer.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 1, metadata));
        for (int i = 1; i <= layers.numLayers(block, metadata); i++) {
            if (layers.getLayer(block, 1, metadata, i) != null) {
                renderer.renderFaceYPos(block, 0.0D, 0.0D + i * 0.00001D, 0.0D, layers.getLayer(block, 1, metadata, i));
            }
        }
    }

    private void renderFaceZNegInv(final RenderBlocks renderer, final Block block, final int metadata) {
        final ILayeredBlock layers = (ILayeredBlock) block;
        renderer.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 2, metadata));
        for (int i = 1; i <= layers.numLayers(block, metadata); i++) {
            if (layers.getLayer(block, 2, metadata, i) != null) {
                renderer.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D - i * 0.00001D, layers.getLayer(block, 2, metadata, i));
            }
        }
    }
    
    private void renderFaceZPosInv(final RenderBlocks renderer, final Block block, final int metadata) {
        final ILayeredBlock layers = (ILayeredBlock) block;
        renderer.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 3, metadata));
        for (int i = 1; i <= layers.numLayers(block, metadata); i++) {
            if (layers.getLayer(block, 3, metadata, i) != null) {
                renderer.renderFaceZPos(block, 0.0D, 0.0D, 0.0D + i * 0.00001D, layers.getLayer(block, 3, metadata, i));
            }
        }
    }
    
    private void renderFaceXNegInv(final RenderBlocks renderer, final Block block, final int metadata) {
        final ILayeredBlock layers = (ILayeredBlock) block;
        renderer.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 4, metadata));
        for (int i = 1; i <= layers.numLayers(block, metadata); i++) {
            if (layers.getLayer(block, 4, metadata, i) != null) {
                renderer.renderFaceXNeg(block, 0.0D - i * 0.00001D, 0.0D, 0.0D, layers.getLayer(block, 4, metadata, i));
            }
        }
    }
    
    private void renderFaceXPosInv(final RenderBlocks renderer, final Block block, final int metadata) {
        final ILayeredBlock layers = (ILayeredBlock) block;
        renderer.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 5, metadata));
        for (int i = 1; i <= layers.numLayers(block, metadata); i++) {
            if (layers.getLayer(block, 5, metadata, i) != null) {
                renderer.renderFaceXPos(block, 0.0D + i * 0.00001D, 0.0D, 0.0D, layers.getLayer(block, 5, metadata, i));
            }
        }
    }

}
