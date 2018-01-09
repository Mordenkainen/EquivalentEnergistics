package com.mordenkainen.equivalentenergistics.blocks.condenser.render;

import com.mordenkainen.equivalentenergistics.EquivalentEnergistics;
import com.mordenkainen.equivalentenergistics.blocks.condenser.tiles.TileEMCCondenserExt;
import com.mordenkainen.equivalentenergistics.core.proxy.ClientProxy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.model.animation.FastTESR;

public class CondenserRenderer extends FastTESR<TileEMCCondenserExt> {
    @Override
    public void renderTileEntityFast(TileEMCCondenserExt te, double x, double y, double z, float partialTicks, int destroyStage, float partial, BufferBuilder buffer) {
        BlockPos pos = te.getPos();
        int lightCoords = Minecraft.getMinecraft().world.getBlockState(pos).getPackedLightmapCoords(Minecraft.getMinecraft().world, pos);
        int lightX = lightCoords >> 16 & 65535;
        int lightY = lightCoords & 65535;
        
        
        TextureAtlasSprite sprite = getTexForSide(te, EnumFacing.UP);
        if(sprite != null) {
	        buffer.pos(x - 0, y + 1.001, z + 1).color(255, 255, 255, 255).tex(sprite.getMinU(), sprite.getMaxV()).lightmap(lightX, lightY).endVertex();
	        buffer.pos(x + 1, y + 1.001, z + 1).color(255, 255, 255, 255).tex(sprite.getMaxU(), sprite.getMaxV()).lightmap(lightX, lightY).endVertex();
	        buffer.pos(x + 1, y + 1.001, z - 0).color(255, 255, 255, 255).tex(sprite.getMaxU(), sprite.getMinV()).lightmap(lightX, lightY).endVertex();
	        buffer.pos(x - 0, y + 1.001, z - 0).color(255, 255, 255, 255).tex(sprite.getMinU(), sprite.getMinV()).lightmap(lightX, lightY).endVertex();
        }
        
        sprite = getTexForSide(te, EnumFacing.DOWN);
        if(sprite != null) {
	        buffer.pos(x - 0, y - .001, z + 1).color(255, 255, 255, 255).tex(sprite.getMinU(), sprite.getMaxV()).lightmap(lightX, lightY).endVertex();
	        buffer.pos(x + 1, y - .001, z + 1).color(255, 255, 255, 255).tex(sprite.getMaxU(), sprite.getMaxV()).lightmap(lightX, lightY).endVertex();
	        buffer.pos(x + 1, y - .001, z - 0).color(255, 255, 255, 255).tex(sprite.getMaxU(), sprite.getMinV()).lightmap(lightX, lightY).endVertex();
	        buffer.pos(x - 0, y - .001, z - 0).color(255, 255, 255, 255).tex(sprite.getMinU(), sprite.getMinV()).lightmap(lightX, lightY).endVertex();
        }
        
        sprite = getTexForSide(te, EnumFacing.EAST);
        if(sprite != null) {
	        buffer.pos(x + 1.001, y - 0, z + 1).color(255, 255, 255, 255).tex(sprite.getMinU(), sprite.getMaxV()).lightmap(lightX, lightY).endVertex();
	        buffer.pos(x + 1.001, y + 1, z + 1).color(255, 255, 255, 255).tex(sprite.getMaxU(), sprite.getMaxV()).lightmap(lightX, lightY).endVertex();
	        buffer.pos(x + 1.001, y + 1, z - 0).color(255, 255, 255, 255).tex(sprite.getMaxU(), sprite.getMinV()).lightmap(lightX, lightY).endVertex();
	        buffer.pos(x + 1.001, y - 0, z - 0).color(255, 255, 255, 255).tex(sprite.getMinU(), sprite.getMinV()).lightmap(lightX, lightY).endVertex();
        }
        
        sprite = getTexForSide(te, EnumFacing.WEST);
        if(sprite != null) {
	        buffer.pos(x - .001, y - 0, z + 1).color(255, 255, 255, 255).tex(sprite.getMinU(), sprite.getMaxV()).lightmap(lightX, lightY).endVertex();
	        buffer.pos(x - .001, y + 1, z + 1).color(255, 255, 255, 255).tex(sprite.getMaxU(), sprite.getMaxV()).lightmap(lightX, lightY).endVertex();
	        buffer.pos(x - .001, y + 1, z - 0).color(255, 255, 255, 255).tex(sprite.getMaxU(), sprite.getMinV()).lightmap(lightX, lightY).endVertex();
	        buffer.pos(x - .001, y - 0, z - 0).color(255, 255, 255, 255).tex(sprite.getMinU(), sprite.getMinV()).lightmap(lightX, lightY).endVertex();
        }
        
        sprite = getTexForSide(te, EnumFacing.SOUTH);
        if(sprite != null) {
	        buffer.pos(x - 0, y + 1, z + 1.001).color(255, 255, 255, 255).tex(sprite.getMinU(), sprite.getMaxV()).lightmap(lightX, lightY).endVertex();
	        buffer.pos(x + 1, y + 1, z + 1.001).color(255, 255, 255, 255).tex(sprite.getMaxU(), sprite.getMaxV()).lightmap(lightX, lightY).endVertex();
	        buffer.pos(x + 1, y - 0, z + 1.001).color(255, 255, 255, 255).tex(sprite.getMaxU(), sprite.getMinV()).lightmap(lightX, lightY).endVertex();
	        buffer.pos(x - 0, y - 0, z + 1.001).color(255, 255, 255, 255).tex(sprite.getMinU(), sprite.getMinV()).lightmap(lightX, lightY).endVertex();
        }
        
        sprite = getTexForSide(te, EnumFacing.NORTH);
        if(sprite != null) {
	        buffer.pos(x - 0, y + 1, z - .001).color(255, 255, 255, 255).tex(sprite.getMinU(), sprite.getMaxV()).lightmap(lightX, lightY).endVertex();
	        buffer.pos(x + 1, y + 1, z - .001).color(255, 255, 255, 255).tex(sprite.getMaxU(), sprite.getMaxV()).lightmap(lightX, lightY).endVertex();
	        buffer.pos(x + 1, y - 0, z - .001).color(255, 255, 255, 255).tex(sprite.getMaxU(), sprite.getMinV()).lightmap(lightX, lightY).endVertex();
	        buffer.pos(x - 0, y - 0, z - .001).color(255, 255, 255, 255).tex(sprite.getMinU(), sprite.getMinV()).lightmap(lightX, lightY).endVertex();
        }
    }

	private TextureAtlasSprite getTexForSide(TileEMCCondenserExt te, EnumFacing side) {
		switch (te.getSide(side)) {
		case INPUT:
			return ((ClientProxy) EquivalentEnergistics.proxy).condenserInput;
		case OUTPUT:
			return ((ClientProxy) EquivalentEnergistics.proxy).condenserOutput;
		default:
			return null;
		}
	}
	
}
