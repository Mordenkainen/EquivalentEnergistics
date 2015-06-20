package com.mordenkainen.equivalentenergistics.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraftforge.common.util.ForgeDirection;

public class ModelEMCCrafter extends ModelBase {
	public ModelRenderer Frame1;
    public ModelRenderer Frame2;
    public ModelRenderer Frame3;
    public ModelRenderer Frame4;
    public ModelRenderer Frame5;
    public ModelRenderer Frame6;
    public ModelRenderer Frame7;
    public ModelRenderer Frame8;
    public ModelRenderer Frame9;
    public ModelRenderer Frame10;
    public ModelRenderer Frame11;
    public ModelRenderer Frame12;
    public ModelRenderer ConnectorBottom;
    public ModelRenderer ConnectorTop;
    public ModelRenderer ConnectorEast;
    public ModelRenderer ConnectorNorth;
    public ModelRenderer ConnectorWest;
    public ModelRenderer ConnectorSouth;
    public ModelRenderer CB2;
    public ModelRenderer CB3;
    public ModelRenderer CB4;
    public ModelRenderer CT2;
    public ModelRenderer CT3;
    public ModelRenderer CT4;
    public ModelRenderer CE2;
    public ModelRenderer CE3;
    public ModelRenderer CE4;
    public ModelRenderer CN2;
    public ModelRenderer CN3;
    public ModelRenderer CN4;
    public ModelRenderer CW2;
    public ModelRenderer CW3;
    public ModelRenderer CW4;
    public ModelRenderer CS2;
    public ModelRenderer CS3;
    public ModelRenderer CS4;

    public ModelEMCCrafter() {
        this.textureWidth = 64;
        this.textureHeight = 32;
        this.CW4 = new ModelRenderer(this, 4, 2);
        this.CW4.setRotationPoint(0.0F, 2.0F, 0.0F);
        this.CW4.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
        this.CT2 = new ModelRenderer(this, 0, 0);
        this.CT2.setRotationPoint(0.0F, 2.0F, 0.0F);
        this.CT2.addBox(-1.5F, -1.5F, -1.5F, 3, 1, 3, 0.0F);
        this.CE2 = new ModelRenderer(this, 0, 0);
        this.CE2.setRotationPoint(0.0F, 2.0F, 0.0F);
        this.CE2.addBox(-1.5F, -1.5F, -1.5F, 3, 1, 3, 0.0F);
        this.Frame9 = new ModelRenderer(this, 16, 0);
        this.Frame9.setRotationPoint(0.0F, 9.0F, 7.0F);
        this.Frame9.addBox(-8.0F, -1.0F, -1.0F, 16, 2, 2, 0.0F);
        this.setRotateAngle(Frame9, 1.5707963267948966F, 0.0F, 0.0F);
        this.CN4 = new ModelRenderer(this, 4, 2);
        this.CN4.setRotationPoint(0.0F, 2.0F, 0.0F);
        this.CN4.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
        this.ConnectorSouth = new ModelRenderer(this, 16, 4);
        this.ConnectorSouth.setRotationPoint(0.0F, 16.0F, 8.0F);
        this.ConnectorSouth.addBox(-3.0F, 0.0F, -3.0F, 6, 1, 6, 0.0F);
        this.setRotateAngle(ConnectorSouth, 1.5707963267948966F, 3.141592653589793F, 0.0F);
        this.CE4 = new ModelRenderer(this, 4, 2);
        this.CE4.setRotationPoint(0.0F, 2.0F, 0.0F);
        this.CE4.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
        this.Frame5 = new ModelRenderer(this, 0, 0);
        this.Frame5.setRotationPoint(-7.0F, 16.0F, 7.0F);
        this.Frame5.addBox(-1.0F, -1.0F, -6.0F, 2, 2, 12, 0.0F);
        this.setRotateAngle(Frame5, 1.5707963267948966F, 0.0F, 0.0F);
        this.CW2 = new ModelRenderer(this, 0, 0);
        this.CW2.setRotationPoint(0.0F, 2.0F, 0.0F);
        this.CW2.addBox(-1.5F, -1.5F, -1.5F, 3, 1, 3, 0.0F);
        this.Frame12 = new ModelRenderer(this, 0, 0);
        this.Frame12.setRotationPoint(7.0F, 9.0F, 0.0F);
        this.Frame12.addBox(-1.0F, -1.0F, -6.0F, 2, 2, 12, 0.0F);
        this.setRotateAngle(Frame12, 0.0F, 0.0F, 3.141592653589793F);
        this.Frame6 = new ModelRenderer(this, 0, 0);
        this.Frame6.setRotationPoint(7.0F, 16.0F, 7.0F);
        this.Frame6.addBox(-1.0F, -1.0F, -6.0F, 2, 2, 12, 0.0F);
        this.setRotateAngle(Frame6, 1.5707963267948966F, 1.5707963267948966F, 0.0F);
        this.CB4 = new ModelRenderer(this, 4, 2);
        this.CB4.setRotationPoint(0.0F, 2.0F, 0.0F);
        this.CB4.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
        this.Frame2 = new ModelRenderer(this, 0, 0);
        this.Frame2.setRotationPoint(7.0F, 23.0F, 0.0F);
        this.Frame2.addBox(-1.0F, -1.0F, -6.0F, 2, 2, 12, 0.0F);
        this.setRotateAngle(Frame2, 0.0F, 3.141592653589793F, 0.0F);
        this.Frame8 = new ModelRenderer(this, 0, 0);
        this.Frame8.setRotationPoint(-7.0F, 16.0F, -7.0F);
        this.Frame8.addBox(-1.0F, -1.0F, -6.0F, 2, 2, 12, 0.0F);
        this.setRotateAngle(Frame8, 1.5707963267948966F, 4.71238898038469F, 0.0F);
        this.CS4 = new ModelRenderer(this, 4, 2);
        this.CS4.setRotationPoint(0.0F, 2.0F, 0.0F);
        this.CS4.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
        this.CT3 = new ModelRenderer(this, 2, 1);
        this.CT3.setRotationPoint(0.0F, 2.0F, 0.0F);
        this.CT3.addBox(-1.0F, -1.0F, -1.0F, 2, 1, 2, 0.0F);
        this.CS3 = new ModelRenderer(this, 2, 1);
        this.CS3.setRotationPoint(0.0F, 2.0F, 0.0F);
        this.CS3.addBox(-1.0F, -1.0F, -1.0F, 2, 1, 2, 0.0F);
        this.Frame4 = new ModelRenderer(this, 16, 0);
        this.Frame4.setRotationPoint(0.0F, 23.0F, -7.0F);
        this.Frame4.addBox(-8.0F, -1.0F, -1.0F, 16, 2, 2, 0.0F);
        this.setRotateAngle(Frame4, 0.0F, 3.141592653589793F, 0.0F);
        this.CS2 = new ModelRenderer(this, 0, 0);
        this.CS2.setRotationPoint(0.0F, 2.0F, 0.0F);
        this.CS2.addBox(-1.5F, -1.5F, -1.5F, 3, 1, 3, 0.0F);
        this.ConnectorTop = new ModelRenderer(this, 16, 4);
        this.ConnectorTop.setRotationPoint(0.0F, 8.0F, 0.0F);
        this.ConnectorTop.addBox(-3.0F, 0.0F, -3.0F, 6, 1, 6, 0.0F);
        this.CE3 = new ModelRenderer(this, 2, 1);
        this.CE3.setRotationPoint(0.0F, 2.0F, 0.0F);
        this.CE3.addBox(-1.0F, -1.0F, -1.0F, 2, 1, 2, 0.0F);
        this.ConnectorBottom = new ModelRenderer(this, 16, 4);
        this.ConnectorBottom.setRotationPoint(0.0F, 24.0F, 0.0F);
        this.ConnectorBottom.addBox(-3.0F, 0.0F, -3.0F, 6, 1, 6, 0.0F);
        this.setRotateAngle(ConnectorBottom, 3.141592653589793F, 0.0F, 0.0F);
        this.CN3 = new ModelRenderer(this, 2, 1);
        this.CN3.setRotationPoint(0.0F, 2.0F, 0.0F);
        this.CN3.addBox(-1.0F, -1.0F, -1.0F, 2, 1, 2, 0.0F);
        this.Frame10 = new ModelRenderer(this, 16, 0);
        this.Frame10.setRotationPoint(0.0F, 9.0F, -7.0F);
        this.Frame10.addBox(-8.0F, -1.0F, -1.0F, 16, 2, 2, 0.0F);
        this.setRotateAngle(Frame10, 1.5707963267948966F, 3.141592653589793F, 0.0F);
        this.Frame3 = new ModelRenderer(this, 16, 0);
        this.Frame3.setRotationPoint(0.0F, 23.0F, 7.0F);
        this.Frame3.addBox(-8.0F, -1.0F, -1.0F, 16, 2, 2, 0.0F);
        this.CW3 = new ModelRenderer(this, 2, 1);
        this.CW3.setRotationPoint(0.0F, 2.0F, 0.0F);
        this.CW3.addBox(-1.0F, -1.0F, -1.0F, 2, 1, 2, 0.0F);
        this.Frame1 = new ModelRenderer(this, 0, 0);
        this.Frame1.setRotationPoint(-7.0F, 23.0F, 0.0F);
        this.Frame1.addBox(-1.0F, -1.0F, -6.0F, 2, 2, 12, 0.0F);
        this.ConnectorEast = new ModelRenderer(this, 16, 4);
        this.ConnectorEast.setRotationPoint(-8.0F, 16.0F, 0.0F);
        this.ConnectorEast.addBox(-3.0F, 0.0F, -3.0F, 6, 1, 6, 0.0F);
        this.setRotateAngle(ConnectorEast, 1.5707963267948966F, 1.5707963267948966F, 0.0F);
        this.ConnectorNorth = new ModelRenderer(this, 16, 4);
        this.ConnectorNorth.setRotationPoint(0.0F, 16.0F, -8.0F);
        this.ConnectorNorth.addBox(-3.0F, 0.0F, -3.0F, 6, 1, 6, 0.0F);
        this.setRotateAngle(ConnectorNorth, 1.5707963267948966F, 0.0F, 0.0F);
        this.CT4 = new ModelRenderer(this, 4, 2);
        this.CT4.setRotationPoint(0.0F, 2.0F, 0.0F);
        this.CT4.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
        this.Frame11 = new ModelRenderer(this, 0, 0);
        this.Frame11.setRotationPoint(-7.0F, 9.0F, 0.0F);
        this.Frame11.addBox(-1.0F, -1.0F, -6.0F, 2, 2, 12, 0.0F);
        this.setRotateAngle(Frame11, 0.0F, 0.0F, 1.5707963267948966F);
        this.CB3 = new ModelRenderer(this, 2, 1);
        this.CB3.setRotationPoint(0.0F, 2.0F, 0.0F);
        this.CB3.addBox(-1.0F, -1.0F, -1.0F, 2, 1, 2, 0.0F);
        this.CB2 = new ModelRenderer(this, 0, 0);
        this.CB2.setRotationPoint(0.0F, 2.0F, 0.0F);
        this.CB2.addBox(-1.5F, -1.5F, -1.5F, 3, 1, 3, 0.0F);
        this.CN2 = new ModelRenderer(this, 0, 0);
        this.CN2.setRotationPoint(0.0F, 2.0F, 0.0F);
        this.CN2.addBox(-1.5F, -1.5F, -1.5F, 3, 1, 3, 0.0F);
        this.ConnectorWest = new ModelRenderer(this, 16, 4);
        this.ConnectorWest.setRotationPoint(8.0F, 16.0F, 0.0F);
        this.ConnectorWest.addBox(-3.0F, 0.0F, -3.0F, 6, 1, 6, 0.0F);
        this.setRotateAngle(ConnectorWest, 1.5707963267948966F, 4.71238898038469F, 0.0F);
        this.Frame7 = new ModelRenderer(this, 0, 0);
        this.Frame7.setRotationPoint(7.0F, 16.0F, -7.0F);
        this.Frame7.addBox(-1.0F, -1.0F, -6.0F, 2, 2, 12, 0.0F);
        this.setRotateAngle(Frame7, 1.5707963267948966F, 3.141592653589793F, 0.0F);
        this.ConnectorWest.addChild(this.CW4);
        this.ConnectorTop.addChild(this.CT2);
        this.ConnectorEast.addChild(this.CE2);
        this.ConnectorNorth.addChild(this.CN4);
        this.ConnectorEast.addChild(this.CE4);
        this.ConnectorWest.addChild(this.CW2);
        this.ConnectorBottom.addChild(this.CB4);
        this.ConnectorSouth.addChild(this.CS4);
        this.ConnectorTop.addChild(this.CT3);
        this.ConnectorSouth.addChild(this.CS3);
        this.ConnectorSouth.addChild(this.CS2);
        this.ConnectorEast.addChild(this.CE3);
        this.ConnectorNorth.addChild(this.CN3);
        this.ConnectorWest.addChild(this.CW3);
        this.ConnectorTop.addChild(this.CT4);
        this.ConnectorBottom.addChild(this.CB3);
        this.ConnectorBottom.addChild(this.CB2);
        this.ConnectorNorth.addChild(this.CN2);
    }

    /**
     * This is a helper function from Tabula to set the rotation of model parts
     */
    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }

    public void render() {
    	final float scale = 1F/16F;
    	this.Frame6.render(scale);
        this.Frame5.render(scale);
        this.Frame1.render(scale);
        this.Frame10.render(scale);
        this.Frame4.render(scale);
        this.Frame9.render(scale);
        this.Frame7.render(scale);
        this.Frame3.render(scale);
        this.Frame12.render(scale);
        this.Frame8.render(scale);
        this.Frame11.render(scale);
        this.Frame2.render(scale);
    }

	public void renderConnector(ForgeDirection side) {
		final float scale = 1F/16F;
		switch(side) {
			case UP:
				this.ConnectorTop.render(scale);
				break;
			case DOWN:
				this.ConnectorBottom.render(scale);
				break;
			case EAST:
				this.ConnectorEast.render(scale);
				break;
			case NORTH:
				this.ConnectorNorth.render(scale);
				break;
			case SOUTH:
				this.ConnectorSouth.render(scale);
				break;
			case WEST:
				this.ConnectorWest.render(scale);
				break;
			default:
		}
	}
}
