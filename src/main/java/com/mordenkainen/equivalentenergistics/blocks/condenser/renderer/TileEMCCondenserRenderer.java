package com.mordenkainen.equivalentenergistics.blocks.condenser.renderer;

import com.mordenkainen.equivalentenergistics.blocks.base.render.HollowTileRenderer;
import com.mordenkainen.equivalentenergistics.blocks.condenser.tiles.TileEMCCondenserBase;
import com.mordenkainen.equivalentenergistics.core.Reference;
import com.mordenkainen.equivalentenergistics.core.textures.TextureEnum;

import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

public class TileEMCCondenserRenderer extends HollowTileRenderer {

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
            
        }
    }

}
