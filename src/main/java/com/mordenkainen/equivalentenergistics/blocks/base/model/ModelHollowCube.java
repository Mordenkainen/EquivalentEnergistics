package com.mordenkainen.equivalentenergistics.blocks.base.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraftforge.common.util.ForgeDirection;

public final class ModelHollowCube extends ModelBase {

    private final ModelRenderer frame;
    private final ModelRenderer corner;
    private final ModelRenderer connector;
    private final ModelRenderer glass;
    
    public ModelHollowCube() {
        super();
        textureWidth = 64;
        textureHeight = 32;
        frame = new ModelRenderer(this, 0, 0);
        frame.setRotationPoint(-8F, -8F, 8F);
        frame.addBox(-8.0F, 6.0F, -6.0F, 2, 2, 12, 0.0F);
        corner = new ModelRenderer(this, 0, 4);
        corner.setRotationPoint(-8F, -8F, 8F);
        corner.addBox(-8.0F, 6.0F, 6.0F, 2, 2, 2, 0.0F);
        connector = new ModelRenderer(this, 0, 14);
        connector.setRotationPoint(-8F, -8F, 8F);
        connector.addBox(-3.0F, -8.0F, -3.0F, 6, 1, 6, 0.0F);
        final ModelRenderer connector2 = new ModelRenderer(this, 0, 0);
        connector2.setRotationPoint(0F, 0F, 0F);
        connector2.addBox(-1.5F, -7.0F, -1.5F, 3, 1, 3, 0.0F);
        connector2.addBox(-1.0F, -6.0F, -1.0F, 2, 1, 2, 0.0F);
        connector2.addBox(-0.5F, -5.0F, -0.5F, 1, 1, 1, 0.0F);
        connector.addChild(connector2);
        glass = new ModelRenderer(this, 28, 0);
        glass.setRotationPoint(-8F, -8F, 8F);
        glass.addBox(-6.0F, -6F, -7F, 12, 12, 1, 0.0F);
    }

    public void setRotateAngle(final ModelRenderer modelRenderer, final float x, final float y, final float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }

    public void render() {
        final float scale = 1F / 16F;
        for (final float f1 : new float[] {0.0F, 1.5707963267948966F, 3.141592653589793F}) {
            for (final float f2 : new float[] {0.0F, 1.5707963267948966F, 3.141592653589793F, 4.7123889803846898F}) {
                setRotateAngle(frame, f1, f2, 0.0F);
                frame.render(scale);
            }
        }
        
        for (final float f1 : new float[] {0.0F, 1.5707963267948966F}) {
            for (final float f2 : new float[] {0.0F, 1.5707963267948966F, 3.141592653589793F, 4.7123889803846898F}) {
                setRotateAngle(corner, f1, f2, 0.0F);
                corner.render(scale);
            }
        }
        
        for (final float f1 : new float[] {0.0F, 1.5707963267948966F, 3.141592653589793F, 4.7123889803846898F}) {
            setRotateAngle(glass, 0.0F, f1, 0.0F);
            glass.render(scale);
        }
        setRotateAngle(glass, 1.5707963267948966F, 0.0F, 0.0F);
        glass.render(scale);
        setRotateAngle(glass, 4.7123889803846898F, 0.0F, 0.0F);
        glass.render(scale);
    }

    public void renderConnector(final ForgeDirection side) {
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
