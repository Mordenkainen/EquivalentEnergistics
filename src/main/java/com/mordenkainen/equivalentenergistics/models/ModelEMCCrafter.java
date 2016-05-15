package com.mordenkainen.equivalentenergistics.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

import net.minecraftforge.common.util.ForgeDirection;

public final class ModelEMCCrafter extends ModelBase {
	private static class Frames {
		public static ModelRenderer frame1;
	    public static ModelRenderer frame2;
	    public static ModelRenderer frame3;
	    public static ModelRenderer frame4;
	    public static ModelRenderer frame5;
	    public static ModelRenderer frame6;
	    public static ModelRenderer frame7;
	    public static ModelRenderer frame8;
	    public static ModelRenderer frame9;
	    public static ModelRenderer frame10;
	    public static ModelRenderer frame11;
	    public static ModelRenderer frame12;
	}
	
	private static class ConnectorTop {
		public static ModelRenderer top;
		public static ModelRenderer cbTop2;
	    public static ModelRenderer cbTop3;
	    public static ModelRenderer cbTop4;
	}
	
	private static class ConnectorBottom {
		public static ModelRenderer bottom;
		public static ModelRenderer cbBottom2;
	    public static ModelRenderer cbBottom3;
	    public static ModelRenderer cbBottom4;
	}
	
	private static class ConnectorEast {
		public static ModelRenderer east;
		public static ModelRenderer cbEast2;
	    public static ModelRenderer cbEast3;
	    public static ModelRenderer cbEast4;
	}
	
	private static class ConnectorNorth {
		public static ModelRenderer north;
		public static ModelRenderer cbNorth2;
	    public static ModelRenderer cbNorth3;
	    public static ModelRenderer cbNorth4;
	}
	
	private static class ConnectorWest {
		public static ModelRenderer west;
		public static ModelRenderer cbWest2;
	    public static ModelRenderer cbWest3;
	    public static ModelRenderer cbWest4;
	}
	
	private static class ConnectorSouth {
		public static ModelRenderer south;
		public static ModelRenderer cbSouth2;
	    public static ModelRenderer cbSouth3;
	    public static ModelRenderer cbSouth4;
	}

    public ModelEMCCrafter() {
    	super();
        textureWidth = 64;
        textureHeight = 32;
        buildFrames();
        buildConnectors();
    }
    
    private void buildFrames() {
    	Frames.frame9 = new ModelRenderer(this, 16, 0);
        Frames.frame9.setRotationPoint(0.0F, 9.0F, 7.0F);
        Frames.frame9.addBox(-8.0F, -1.0F, -1.0F, 16, 2, 2, 0.0F);
        setRotateAngle(Frames.frame9, 1.5707963267948966F, 0.0F, 0.0F);
        Frames.frame5 = new ModelRenderer(this, 0, 0);
        Frames.frame5.setRotationPoint(-7.0F, 16.0F, 7.0F);
        Frames.frame5.addBox(-1.0F, -1.0F, -6.0F, 2, 2, 12, 0.0F);
        setRotateAngle(Frames.frame5, 1.5707963267948966F, 0.0F, 0.0F);
        Frames.frame12 = new ModelRenderer(this, 0, 0);
        Frames.frame12.setRotationPoint(7.0F, 9.0F, 0.0F);
        Frames.frame12.addBox(-1.0F, -1.0F, -6.0F, 2, 2, 12, 0.0F);
        setRotateAngle(Frames.frame12, 0.0F, 0.0F, 3.141592653589793F);
        Frames.frame6 = new ModelRenderer(this, 0, 0);
        Frames.frame6.setRotationPoint(7.0F, 16.0F, 7.0F);
        Frames.frame6.addBox(-1.0F, -1.0F, -6.0F, 2, 2, 12, 0.0F);
        setRotateAngle(Frames.frame6, 1.5707963267948966F, 1.5707963267948966F, 0.0F);
        Frames.frame2 = new ModelRenderer(this, 0, 0);
        Frames.frame2.setRotationPoint(7.0F, 23.0F, 0.0F);
        Frames.frame2.addBox(-1.0F, -1.0F, -6.0F, 2, 2, 12, 0.0F);
        setRotateAngle(Frames.frame2, 0.0F, 3.141592653589793F, 0.0F);
        Frames.frame8 = new ModelRenderer(this, 0, 0);
        Frames.frame8.setRotationPoint(-7.0F, 16.0F, -7.0F);
        Frames.frame8.addBox(-1.0F, -1.0F, -6.0F, 2, 2, 12, 0.0F);
        setRotateAngle(Frames.frame8, 1.5707963267948966F, 4.71238898038469F, 0.0F);
        Frames.frame4 = new ModelRenderer(this, 16, 0);
        Frames.frame4.setRotationPoint(0.0F, 23.0F, -7.0F);
        Frames.frame4.addBox(-8.0F, -1.0F, -1.0F, 16, 2, 2, 0.0F);
        setRotateAngle(Frames.frame4, 0.0F, 3.141592653589793F, 0.0F);
        Frames.frame10 = new ModelRenderer(this, 16, 0);
        Frames.frame10.setRotationPoint(0.0F, 9.0F, -7.0F);
        Frames.frame10.addBox(-8.0F, -1.0F, -1.0F, 16, 2, 2, 0.0F);
        setRotateAngle(Frames.frame10, 1.5707963267948966F, 3.141592653589793F, 0.0F);
        Frames.frame3 = new ModelRenderer(this, 16, 0);
        Frames.frame3.setRotationPoint(0.0F, 23.0F, 7.0F);
        Frames.frame3.addBox(-8.0F, -1.0F, -1.0F, 16, 2, 2, 0.0F);
        Frames.frame1 = new ModelRenderer(this, 0, 0);
        Frames.frame1.setRotationPoint(-7.0F, 23.0F, 0.0F);
        Frames.frame1.addBox(-1.0F, -1.0F, -6.0F, 2, 2, 12, 0.0F);
        Frames.frame11 = new ModelRenderer(this, 0, 0);
        Frames.frame11.setRotationPoint(-7.0F, 9.0F, 0.0F);
        Frames.frame11.addBox(-1.0F, -1.0F, -6.0F, 2, 2, 12, 0.0F);
        setRotateAngle(Frames.frame11, 0.0F, 0.0F, 1.5707963267948966F);
        Frames.frame7 = new ModelRenderer(this, 0, 0);
        Frames.frame7.setRotationPoint(7.0F, 16.0F, -7.0F);
        Frames.frame7.addBox(-1.0F, -1.0F, -6.0F, 2, 2, 12, 0.0F);
        setRotateAngle(Frames.frame7, 1.5707963267948966F, 3.141592653589793F, 0.0F);
    }
    
    private void buildConnectors() {
    	ConnectorWest.cbWest4 = new ModelRenderer(this, 4, 2);
        ConnectorWest.cbWest4.setRotationPoint(0.0F, 2.0F, 0.0F);
        ConnectorWest.cbWest4.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
        ConnectorTop.cbTop2 = new ModelRenderer(this, 0, 0);
        ConnectorTop.cbTop2.setRotationPoint(0.0F, 2.0F, 0.0F);
        ConnectorTop.cbTop2.addBox(-1.5F, -1.5F, -1.5F, 3, 1, 3, 0.0F);
        ConnectorEast.cbEast2 = new ModelRenderer(this, 0, 0);
        ConnectorEast.cbEast2.setRotationPoint(0.0F, 2.0F, 0.0F);
        ConnectorEast.cbEast2.addBox(-1.5F, -1.5F, -1.5F, 3, 1, 3, 0.0F);
        ConnectorNorth.cbNorth4 = new ModelRenderer(this, 4, 2);
        ConnectorNorth.cbNorth4.setRotationPoint(0.0F, 2.0F, 0.0F);
        ConnectorNorth.cbNorth4.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
        ConnectorSouth.south = new ModelRenderer(this, 16, 4);
        ConnectorSouth.south.setRotationPoint(0.0F, 16.0F, 8.0F);
        ConnectorSouth.south.addBox(-3.0F, 0.0F, -3.0F, 6, 1, 6, 0.0F);
        setRotateAngle(ConnectorSouth.south, 1.5707963267948966F, 3.141592653589793F, 0.0F);
        ConnectorEast.cbEast4 = new ModelRenderer(this, 4, 2);
        ConnectorEast.cbEast4.setRotationPoint(0.0F, 2.0F, 0.0F);
        ConnectorEast.cbEast4.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
        ConnectorWest.cbWest2 = new ModelRenderer(this, 0, 0);
        ConnectorWest.cbWest2.setRotationPoint(0.0F, 2.0F, 0.0F);
        ConnectorWest.cbWest2.addBox(-1.5F, -1.5F, -1.5F, 3, 1, 3, 0.0F);
        ConnectorBottom.cbBottom4 = new ModelRenderer(this, 4, 2);
        ConnectorBottom.cbBottom4.setRotationPoint(0.0F, 2.0F, 0.0F);
        ConnectorBottom.cbBottom4.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
        ConnectorSouth.cbSouth4 = new ModelRenderer(this, 4, 2);
        ConnectorSouth.cbSouth4.setRotationPoint(0.0F, 2.0F, 0.0F);
        ConnectorSouth.cbSouth4.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
        ConnectorTop.cbTop3 = new ModelRenderer(this, 2, 1);
        ConnectorTop.cbTop3.setRotationPoint(0.0F, 2.0F, 0.0F);
        ConnectorTop.cbTop3.addBox(-1.0F, -1.0F, -1.0F, 2, 1, 2, 0.0F);
        ConnectorSouth.cbSouth3 = new ModelRenderer(this, 2, 1);
        ConnectorSouth.cbSouth3.setRotationPoint(0.0F, 2.0F, 0.0F);
        ConnectorSouth.cbSouth3.addBox(-1.0F, -1.0F, -1.0F, 2, 1, 2, 0.0F);
        ConnectorSouth.cbSouth2 = new ModelRenderer(this, 0, 0);
        ConnectorSouth.cbSouth2.setRotationPoint(0.0F, 2.0F, 0.0F);
        ConnectorSouth.cbSouth2.addBox(-1.5F, -1.5F, -1.5F, 3, 1, 3, 0.0F);
        ConnectorTop.top = new ModelRenderer(this, 16, 4);
        ConnectorTop.top.setRotationPoint(0.0F, 8.0F, 0.0F);
        ConnectorTop.top.addBox(-3.0F, 0.0F, -3.0F, 6, 1, 6, 0.0F);
        ConnectorEast.cbEast3 = new ModelRenderer(this, 2, 1);
        ConnectorEast.cbEast3.setRotationPoint(0.0F, 2.0F, 0.0F);
        ConnectorEast.cbEast3.addBox(-1.0F, -1.0F, -1.0F, 2, 1, 2, 0.0F);
        ConnectorBottom.bottom = new ModelRenderer(this, 16, 4);
        ConnectorBottom.bottom.setRotationPoint(0.0F, 24.0F, 0.0F);
        ConnectorBottom.bottom.addBox(-3.0F, 0.0F, -3.0F, 6, 1, 6, 0.0F);
        setRotateAngle(ConnectorBottom.bottom, 3.141592653589793F, 0.0F, 0.0F);
        ConnectorNorth.cbNorth3 = new ModelRenderer(this, 2, 1);
        ConnectorNorth.cbNorth3.setRotationPoint(0.0F, 2.0F, 0.0F);
        ConnectorNorth.cbNorth3.addBox(-1.0F, -1.0F, -1.0F, 2, 1, 2, 0.0F);
        ConnectorWest.cbWest3 = new ModelRenderer(this, 2, 1);
        ConnectorWest.cbWest3.setRotationPoint(0.0F, 2.0F, 0.0F);
        ConnectorWest.cbWest3.addBox(-1.0F, -1.0F, -1.0F, 2, 1, 2, 0.0F);
        ConnectorEast.east = new ModelRenderer(this, 16, 4);
        ConnectorEast.east.setRotationPoint(-8.0F, 16.0F, 0.0F);
        ConnectorEast.east.addBox(-3.0F, 0.0F, -3.0F, 6, 1, 6, 0.0F);
        setRotateAngle(ConnectorEast.east, 1.5707963267948966F, 1.5707963267948966F, 0.0F);
        ConnectorNorth.north = new ModelRenderer(this, 16, 4);
        ConnectorNorth.north.setRotationPoint(0.0F, 16.0F, -8.0F);
        ConnectorNorth.north.addBox(-3.0F, 0.0F, -3.0F, 6, 1, 6, 0.0F);
        setRotateAngle(ConnectorNorth.north, 1.5707963267948966F, 0.0F, 0.0F);
        ConnectorTop.cbTop4 = new ModelRenderer(this, 4, 2);
        ConnectorTop.cbTop4.setRotationPoint(0.0F, 2.0F, 0.0F);
        ConnectorTop.cbTop4.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
        ConnectorBottom.cbBottom3 = new ModelRenderer(this, 2, 1);
        ConnectorBottom.cbBottom3.setRotationPoint(0.0F, 2.0F, 0.0F);
        ConnectorBottom.cbBottom3.addBox(-1.0F, -1.0F, -1.0F, 2, 1, 2, 0.0F);
        ConnectorBottom.cbBottom2 = new ModelRenderer(this, 0, 0);
        ConnectorBottom.cbBottom2.setRotationPoint(0.0F, 2.0F, 0.0F);
        ConnectorBottom.cbBottom2.addBox(-1.5F, -1.5F, -1.5F, 3, 1, 3, 0.0F);
        ConnectorNorth.cbNorth2 = new ModelRenderer(this, 0, 0);
        ConnectorNorth.cbNorth2.setRotationPoint(0.0F, 2.0F, 0.0F);
        ConnectorNorth.cbNorth2.addBox(-1.5F, -1.5F, -1.5F, 3, 1, 3, 0.0F);
        ConnectorWest.west = new ModelRenderer(this, 16, 4);
        ConnectorWest.west.setRotationPoint(8.0F, 16.0F, 0.0F);
        ConnectorWest.west.addBox(-3.0F, 0.0F, -3.0F, 6, 1, 6, 0.0F);
        setRotateAngle(ConnectorWest.west, 1.5707963267948966F, 4.71238898038469F, 0.0F);
        ConnectorWest.west.addChild(ConnectorWest.cbWest4);
        ConnectorTop.top.addChild(ConnectorTop.cbTop2);
        ConnectorEast.east.addChild(ConnectorEast.cbEast2);
        ConnectorNorth.north.addChild(ConnectorNorth.cbNorth4);
        ConnectorEast.east.addChild(ConnectorEast.cbEast4);
        ConnectorWest.west.addChild(ConnectorWest.cbWest2);
        ConnectorBottom.bottom.addChild(ConnectorBottom.cbBottom4);
        ConnectorSouth.south.addChild(ConnectorSouth.cbSouth4);
        ConnectorTop.top.addChild(ConnectorTop.cbTop3);
        ConnectorSouth.south.addChild(ConnectorSouth.cbSouth3);
        ConnectorSouth.south.addChild(ConnectorSouth.cbSouth2);
        ConnectorEast.east.addChild(ConnectorEast.cbEast3);
        ConnectorNorth.north.addChild(ConnectorNorth.cbNorth3);
        ConnectorWest.west.addChild(ConnectorWest.cbWest3);
        ConnectorTop.top.addChild(ConnectorTop.cbTop4);
        ConnectorBottom.bottom.addChild(ConnectorBottom.cbBottom3);
        ConnectorBottom.bottom.addChild(ConnectorBottom.cbBottom2);
        ConnectorNorth.north.addChild(ConnectorNorth.cbNorth2);
    }

    public void setRotateAngle(final ModelRenderer modelRenderer, final float x, final float y, final float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }

    public void render() {
    	final float scale = 1F/16F;
    	Frames.frame6.render(scale);
    	Frames.frame5.render(scale);
    	Frames.frame1.render(scale);
    	Frames.frame10.render(scale);
    	Frames.frame4.render(scale);
    	Frames.frame9.render(scale);
    	Frames.frame7.render(scale);
    	Frames.frame3.render(scale);
    	Frames.frame12.render(scale);
    	Frames.frame8.render(scale);
    	Frames.frame11.render(scale);
    	Frames.frame2.render(scale);
    }

	public void renderConnector(final ForgeDirection side) {
		final float scale = 1F/16F;
		switch(side) {
			case UP:
				ConnectorTop.top.render(scale);
				break;
			case DOWN:
				ConnectorBottom.bottom.render(scale);
				break;
			case EAST:
				ConnectorEast.east.render(scale);
				break;
			case NORTH:
				ConnectorNorth.north.render(scale);
				break;
			case SOUTH:
				ConnectorSouth.south.render(scale);
				break;
			case WEST:
				ConnectorWest.west.render(scale);
				break;
			default:
		}
	}
}
