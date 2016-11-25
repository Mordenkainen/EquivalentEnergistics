package com.mordenkainen.equivalentenergistics.registries;

import com.mordenkainen.equivalentenergistics.lib.Reference;

import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.IIcon;

public enum TextureEnum {

    EMCCONDENSER(TextureType.BLOCK, "EMCCondenserTop", "EMCCondenserSide"),
    EMCBOOK(TextureType.ITEM, "EMCBook"),
    EMCCRYSTAL(TextureType.ITEM, "EMCNugget", "EMCShard", "EMCCrystal", "DenseEMCCrystal", "SuperDenseEMCCrystal"),
    EMCCRYSTALOLD(TextureType.ITEM, "EMCCrystal"),
    EMCCELL(TextureType.ITEM, "EMCCellTier0", "EMCCellTier1", "EMCCellTier2", "EMCCellTier3", "EMCCellTier4", "EMCCellTier5", "EMCCellTier6", "EMCCellTier7"),
    MISCITEM(TextureType.ITEM, "EMCCellHousing", "EMCTotal"),
    EMCSTORAGECOMPONENT(TextureType.ITEM, "EMCStorageComponent0", "EMCStorageComponent1", "EMCStorageComponent2", "EMCStorageComponent3", "EMCStorageComponent4", "EMCStorageComponent5", "EMCStorageComponent6", "EMCStorageComponent7");

    private enum TextureType {
        ITEM,
        BLOCK,
        PART
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

    public static void registerTextures(final TextureMap textureMap) {
    	for (final TextureEnum currentTexture : TextureEnum.values()) {
    		if (!(textureMap.getTextureType() == 0 && (currentTexture.textureType == TextureType.BLOCK || currentTexture.textureType == TextureType.PART)) && !(textureMap.getTextureType() == 1 && currentTexture.textureType == TextureType.ITEM)) {
                continue;
            }

            String header = Reference.MOD_ID + ":";

            if (currentTexture.textureType == TextureType.PART) {
                header += "part/";
            }

            for (int i = 0; i < currentTexture.textureNames.length; i++) {
            	currentTexture.textures[i] = textureMap.registerIcon(header + currentTexture.textureNames[i]);
            }
    	}
    }

}
