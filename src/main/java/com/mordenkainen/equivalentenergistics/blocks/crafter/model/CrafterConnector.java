package com.mordenkainen.equivalentenergistics.blocks.crafter.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.EnumFacing;

public class CrafterConnector extends ModelBase {

    private final ModelRenderer connector;

    public CrafterConnector() {
        super();
        textureWidth = 24;
        textureHeight = 16;
        connector = new ModelRenderer(this, 0, 4);
        connector.setRotationPoint(-8F, -8F, 8F);
        connector.addBox(-3.0F, -8.0F, -3.0F, 6, 1, 6, 0.0F);
        final ModelRenderer connector2 = new ModelRenderer(this, 0, 0);
        connector2.setRotationPoint(0F, 0F, 0F);
        connector2.addBox(-1.5F, -7.0F, -1.5F, 3, 1, 3, 0.0F);
        connector2.addBox(-1.0F, -6.0F, -1.0F, 2, 1, 2, 0.0F);
        connector2.addBox(-0.5F, -5.0F, -0.5F, 1, 1, 1, 0.0F);
        connector.addChild(connector2);
    }

    public void setRotateAngle(final ModelRenderer modelRenderer, final float x, final float y, final float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }

    public void renderConnector(final EnumFacing side) {
        final float scale = 1F / 16F;
        switch (side) {
        case UP:
            setRotateAngle(connector, 0.0F, 0.0F, 0.0F);
            break;
        case DOWN:
            setRotateAngle(connector, 3.141592653589793F, 0.0F, 0.0F);
            break;
        case EAST:
            setRotateAngle(connector, 0.0F, 0.0F, 4.7123889803846898F);
            break;
        case NORTH:
            setRotateAngle(connector, 1.5707963267948966F, 0.0F, 0.0F);
            break;
        case SOUTH:
            setRotateAngle(connector, 4.7123889803846898F, 0.0F, 0.0F);
            break;
        case WEST:
            setRotateAngle(connector, 0.0F, 0.0F, 1.5707963267948966F);
            break;
        default:
        }
        connector.render(scale);
    }


}
