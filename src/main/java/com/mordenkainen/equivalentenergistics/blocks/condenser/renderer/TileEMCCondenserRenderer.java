package com.mordenkainen.equivalentenergistics.blocks.condenser.renderer;

import java.util.List;

import org.lwjgl.opengl.GL11;

import com.mordenkainen.equivalentenergistics.blocks.base.model.ModelConnector;
import com.mordenkainen.equivalentenergistics.blocks.base.render.HollowTileRenderer;
import com.mordenkainen.equivalentenergistics.blocks.condenser.tiles.TileEMCCondenserBase;
import com.mordenkainen.equivalentenergistics.blocks.condenser.tiles.TileEMCCondenserExt;
import com.mordenkainen.equivalentenergistics.core.Reference;
import com.mordenkainen.equivalentenergistics.core.textures.TextureEnum;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEMCCondenserRenderer extends HollowTileRenderer {

    protected static final ModelConnector INPUTCONNECTOR = new ModelConnector(24, 14);
    protected static final ModelConnector OUTPUTCONNECTOR = new ModelConnector(0, 21);
    
    private static final ResourceLocation[] MODELTEXTURES = {
            new ResourceLocation(Reference.MOD_ID + ":textures/models/EMCCondenser.png"),
            new ResourceLocation(Reference.MOD_ID + ":textures/models/EMCCondenserAdv.png"),
            new ResourceLocation(Reference.MOD_ID + ":textures/models/EMCCondenserExt.png"),
            new ResourceLocation(Reference.MOD_ID + ":textures/models/EMCCondenserUlt.png")
    };

    @Override
    public void renderTileEntityAt(final TileEntity tile, final double x, final double y, final double z, final float partialTicks) {
        if (!(tile instanceof TileEMCCondenserBase)) {
            return;
        }
        
        final TileEMCCondenserBase condenser = (TileEMCCondenserBase) tile;
                
        renderFrame(MODELTEXTURES[condenser.getBlockMetadata()], x, y, z, condenser.getBlockMetadata());
        
        if (condenser.getWorldObj() != null) {
            if (condenser.isActive()) {
                bindTexture(TextureMap.locationBlocksTexture);
                final IIcon tex = condenser.getState().isError() ? TextureEnum.EMCCONDENSER.getTexture(1) : TextureEnum.EMCCONDENSER.getTexture(0);
                renderLights(tex, x, y, z);
            }
            
            renderConnectors(condenser, MODELTEXTURES[tile.getBlockMetadata()], x, y, z);
            
            renderContent(condenser, x, y, z, partialTicks);
            
        }
    }
    
    protected void renderConnectors(final TileEntity tile, final ResourceLocation tex, final double x, final double y, final double z) {
        if(tile instanceof TileEMCCondenserExt) {
            final TileEMCCondenserExt condenser = (TileEMCCondenserExt) tile;
            GL11.glPushMatrix();
            GL11.glTranslatef((float) x, (float) y, (float) z);
            GL11.glScalef(-1F, -1F, 1F);
            bindTexture(tex);
            for (final ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
                switch (condenser.getSide(side.ordinal())) {
                    case NONE:
                        if (isCableConnected(tile.getWorldObj(), tile.xCoord, tile.yCoord, tile.zCoord, side)) {
                            CABLECONNECTOR.render(side);
                        }
                        break;
                    case INPUT:
                        INPUTCONNECTOR.render(side);
                        break;
                    case OUTPUT:
                        OUTPUTCONNECTOR.render(side);
                        break;
                    default:
                        break;
                }
            }
            GL11.glPopMatrix();
        } else {
            super.renderConnectors(tile, tex, x, y, z);
        }
    }
    
    private void renderContent(final TileEMCCondenserBase tile, final double x, final double y, final double z, final float partialTicks) {
        final List<ItemStack> stacks = tile.getDisplayStacks();
        final float time = Minecraft.getMinecraft().renderViewEntity.ticksExisted + partialTicks;
        renderRing(tile, stacks, 0.2F, x, y, z, time, false, false);
    }

}
