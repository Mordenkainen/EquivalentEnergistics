package com.mordenkainen.equivalentenergistics.registries;

import com.mordenkainen.equivalentenergistics.lib.Reference;

import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.IIcon;

public enum TextureEnum {

	EMCCONDENSER(TextureType.BLOCK, "EMCCondenserTop", "EMCCondenserSide"),
	EMCBOOK(TextureType.ITEM, "EMCBook"),
	EMCCRYSTAL(TextureType.ITEM, "EMCNugget", "EMCShard", "EMCCrystal", "DenseEMCCrystal", "SuperDenseEMCCrystal"),
	EMCCRYSTALOLD(TextureType.ITEM, "EMCCrystal"),
	EMCCELL(TextureType.ITEM, "EMCCellTier0", "EMCCellTier1", "EMCCellTier2", "EMCCellTier3");
	
	private enum TextureType {
		ITEM, BLOCK, PART
	}

	private TextureType textureType;
	private String[] textureNames;

	private IIcon[] textures;

	TextureEnum(final TextureType type, final String... names) {
		textureType = type;
		textureNames = names;
		textures = new IIcon[textureNames.length];
	}

	public IIcon getTexture() {
		return textures[0];
	}
	
	public IIcon getTexture(final int id) {
		return textures[id];
	}

	public IIcon[] getTextures() {
		return textures.clone();
	}

	public void registerTexture(final TextureMap textureMap) {
		if (!(textureMap.getTextureType() == 0 && (textureType == TextureType.BLOCK || textureType == TextureType.PART))
				&& !(textureMap.getTextureType() == 1 && textureType == TextureType.ITEM)) {
			return;
		}

		String header = Reference.MOD_ID + ":";
		
		if (textureType == TextureType.PART) {
			header += "part/";
		}
		
		for (int i = 0; i < textureNames.length; i++) {
			textures[i] = textureMap.registerIcon(header + textureNames[i]);
		}
	}
}
