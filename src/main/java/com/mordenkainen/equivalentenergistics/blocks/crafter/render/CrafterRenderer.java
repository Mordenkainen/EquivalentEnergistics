package com.mordenkainen.equivalentenergistics.blocks.crafter.render;

import java.util.ArrayList;
import java.util.List;

import com.mordenkainen.equivalentenergistics.blocks.crafter.model.CrafterConnector;
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
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CrafterRenderer extends TileEntitySpecialRenderer<TileEMCCrafter> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(Reference.MOD_ID + ":" + "textures/models/emc_crafter_connector.png");
    private final CrafterConnector model = new CrafterConnector();

    @Override
    public void renderTileEntityAt(final TileEMCCrafter te, final double x, final double y, final double z, final float partialTicks, final int destroyStage) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.scale(-1F, -1F, 1F);
        Minecraft.getMinecraft().renderEngine.bindTexture(TEXTURE);
        for(final EnumFacing face : EnumFacing.VALUES) {
            if (isCableConnected(te.getWorld(), te.getPos(),  face)) {
                model.renderConnector(face);
            }
        }
        GlStateManager.popMatrix();

        List<ItemStack> stacks = new ArrayList<ItemStack>();
        if(te.isCrafting()) {
            stacks.addAll(te.getDisplayStacks());
        } else if (te.getCurrentTome() != null) {
            stacks.add(te.getCurrentTome());
        }
        if (stacks.size() > 0) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(x, y, z);
            final float time = Minecraft.getMinecraft().getRenderViewEntity().ticksExisted + partialTicks;
            final float anglePer = 360F / stacks.size();

            for(int i = 0; i < stacks.size(); i++) {
                final ItemStack stack = stacks.get(i);
                if (stack == null) {
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
                Minecraft.getMinecraft().getRenderManager().doRenderEntity(entityitem,  0.0D, 0.0D, 0.0D, 0.0F, 0.0F, false);
                GlStateManager.popMatrix();
            }
            GlStateManager.popMatrix();
        }
    }

    private boolean isCableConnected(final World world, final BlockPos pos, final EnumFacing face) {
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
