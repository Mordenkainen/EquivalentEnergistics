package com.mordenkainen.equivalentenergistics.render;

import com.mordenkainen.equivalentenergistics.lib.Reference;

import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.IIcon;

public enum TextureManager {

	EMCCONDENSER(TextureType.BLOCK, "EMCCondenserTop", "EMCCondenserSide"),
	EMCBOOK(TextureType.ITEM, "EMCBook"),
	EMCCRYSTAL(TextureType.ITEM, "EMCCrystal");
	
	private enum TextureType {
		ITEM, BLOCK, PART
	}

	private TextureType textureType;
	private String[] textureNames;

	private IIcon[] textures;

	TextureManager(TextureType _textureType, String... _textureName) {
		this.textureType = _textureType;
		this.textureNames = _textureName;
		this.textures = new IIcon[this.textureNames.length];
	}

	public IIcon getTexture() {
		return this.textures[0];
	}
	
	public IIcon getTexture(int id) {
		return this.textures[id];
	}

	public IIcon[] getTextures() {
		return this.textures;
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
