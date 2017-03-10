package com.mordenkainen.equivalentenergistics.core.textures;

import com.mordenkainen.equivalentenergistics.core.Reference;

import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.IIcon;

public enum TextureEnum {

    EMCCONDENSER(TextureType.BLOCK, "EMCCondenserTop", "EMCCondenserSide", "EMCCondenserAdvTop", "EMCCondenserAdvSide", "EMCCondenserExtTop", "EMCCondenserExtSide", "EMCCondenserExtOutput", "EMCCondenserExtInput"),
    EMCCONDENSERADV(TextureType.BLOCK, "EMCCondenserExtTop", "EMCCondenserExtSide", "EMCCondenserExtInput", "EMCCondenserExtOutput", "EMCCondenserUltTop", "EMCCondenserUltSide", "EMCCondenserUltInput", "EMCCondenserUltOutput"),
    EMCBOOK(TextureType.ITEM, "EMCBook"),
    EMCCRYSTAL(TextureType.ITEM, "EMCNugget", "EMCShard", "EMCCrystal", "DenseEMCCrystal", "SuperDenseEMCCrystal"),
    EMCCRYSTALOLD(TextureType.ITEM, "EMCCrystal"),
    EMCCELL(TextureType.ITEM, "EMCCellTier0", "EMCCellTier1", "EMCCellTier2", "EMCCellTier3", "EMCCellTier4", "EMCCellTier5", "EMCCellTier6", "EMCCellTier7"),
    EMCCELLCREATIVE(TextureType.ITEM, "EMCCellCreative"),
    MISCITEM(TextureType.ITEM, "EMCCellHousing", "EMCTotal"),
    EMCSTORAGECOMPONENT(TextureType.ITEM, "EMCStorageComponent0", "EMCStorageComponent1", "EMCStorageComponent2", "EMCStorageComponent3", "EMCStorageComponent4", "EMCStorageComponent5", "EMCStorageComponent6", "EMCStorageComponent7");

    private enum TextureType {
        ITEM(1),
        BLOCK(0),
        PART(0);

        private int textureType;

        TextureType(final int textureType) {
            this.textureType = textureType;
        }

        public int getTextureType() {
            return textureType;
        }
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
            if (textureMap.getTextureType() != currentTexture.textureType.getTextureType()) {
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
