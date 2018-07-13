package com.mordenkainen.equivalentenergistics.blocks.base.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public final class ModelHollowCube extends ModelBase {

    private final ModelRenderer frame;
    private final ModelRenderer corner;
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

}
