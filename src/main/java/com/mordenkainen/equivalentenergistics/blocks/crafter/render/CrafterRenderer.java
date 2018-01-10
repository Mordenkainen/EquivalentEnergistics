package com.mordenkainen.equivalentenergistics.blocks.crafter.render;

import com.mordenkainen.equivalentenergistics.blocks.crafter.models.CrafterConnector;
import com.mordenkainen.equivalentenergistics.blocks.crafter.tiles.TileEMCCrafter;
import com.mordenkainen.equivalentenergistics.core.Reference;

import appeng.api.implementations.parts.IPartCable;
import appeng.api.networking.IGridHost;
import appeng.api.parts.IPart;
import appeng.api.parts.IPartHost;
import appeng.api.util.AEPartLocation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CrafterRenderer extends TileEntitySpecialRenderer<TileEMCCrafter> {

    private static final ResourceLocation texture = new ResourceLocation(Reference.MOD_ID + ":" + "textures/models/emc_crafter_connector.png");
    final CrafterConnector model = new CrafterConnector();

    @Override
    public void render(TileEMCCrafter te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.scale(-1F, -1F, 1F);
        Minecraft.getMinecraft().renderEngine.bindTexture(texture);
        for(EnumFacing face : EnumFacing.VALUES) {
            if (isCableConnected(te.getWorld(), te.getPos(),  face)) {
                model.renderConnector(face);
            }
        }
        GlStateManager.popMatrix();

        NonNullList<ItemStack> stacks;
        if(te.isCrafting()) {
            stacks = te.getDisplayStacks();
        } else {
            stacks = NonNullList.withSize(1, te.getCurrentTome());
        }
        if (stacks.size() > 0) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(x, y, z);
            final float time = Minecraft.getMinecraft().getRenderViewEntity().ticksExisted + partialTicks;
            final float anglePer = 360F / stacks.size();

            for(int i = 0; i < stacks.size(); i++) {
                final ItemStack stack = stacks.get(i);
                if (stack.isEmpty()) {
                    continue;
                }
                GlStateManager.pushMatrix();
                if (stacks.size() > 1) {
                    GlStateManager.translate(0.5, 0.3, 0.5);
                    GlStateManager.scale(0.5F, 0.5F, 0.5F);
                    GlStateManager.rotate(anglePer * i + time, 0F, 1F, 0F);
                    GlStateManager.translate(0.2, 0, 0.25);
                } else {
                    GlStateManager.translate(0.5, 0.0, 0.5);
                }
                final EntityItem entityitem = new EntityItem(te.getWorld(), 0.0D, 0.0D, 0.0D, stack);
                GlStateManager.rotate(time % 360.0F, 0.0F, 1.0F, 0.0F);
                entityitem.hoverStart = 0.0F;
                Minecraft.getMinecraft().getRenderManager().renderEntity(entityitem,  0.0D, 0.0D, 0.0D, 0.0F, 0.0F, false);
                GlStateManager.popMatrix();
            }
            GlStateManager.popMatrix();
        }
    }

    private boolean isCableConnected(World world, BlockPos pos, EnumFacing face) {
        final int tileYPos = pos.getY() + face.getFrontOffsetY();
        if (tileYPos < 0 || tileYPos > 256) {
            return false;
        }

        final TileEntity tile = world.getTileEntity(pos.offset(face));
        if (!(tile instanceof IGridHost && tile instanceof IPartHost)) {
            return false;
        }

        final IPartHost host = (IPartHost) tile;
        final IPart part = host.getPart(AEPartLocation.INTERNAL);
        if (part instanceof IPartCable) {
            final IPartCable cable = (IPartCable) part;
            return cable.isConnected(face.getOpposite());
        }

        return false;
    }

}
