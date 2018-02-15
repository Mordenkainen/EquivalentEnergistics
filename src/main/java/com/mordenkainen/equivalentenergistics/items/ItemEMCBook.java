package com.mordenkainen.equivalentenergistics.items;

import java.util.List;

import com.mordenkainen.equivalentenergistics.core.Names;
import com.mordenkainen.equivalentenergistics.items.base.ItemBase;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemEMCBook extends ItemBase {

    private static final String OWNER_TAG = "Owner";
    private static final String UUID_TAG = "OwnerUUID";

    public ItemEMCBook() {
        super(Names.BOOK);
        setMaxStackSize(1);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(final ItemStack stack, final World world, final EntityPlayer player, final EnumHand hand) {
        if (!world.isRemote && player != null && stack != null) {
            if (!stack.hasTagCompound()) {
                stack.setTagCompound(new NBTTagCompound());
            }
            final NBTTagCompound stackNBT = stack.getTagCompound();
            if (stackNBT.hasKey(OWNER_TAG) && player.isSneaking()) {
                stackNBT.removeTag(OWNER_TAG);
                stackNBT.removeTag(UUID_TAG);
                player.addChatComponentMessage(new TextComponentTranslation("message.book.clear", new Object[0]));
                return new ActionResult<ItemStack>(EnumActionResult.PASS, stack);
            }
            final String playerUUID = player.getUniqueID().toString();
            if (stackNBT.hasKey(UUID_TAG) && !stackNBT.getString(UUID_TAG).equals(playerUUID)) {
                player.addChatComponentMessage(new TextComponentTranslation("message.book.wrongowner", new Object[0]));
                return new ActionResult<ItemStack>(EnumActionResult.PASS, stack);
            }
            stackNBT.setString(OWNER_TAG, player.getName());
            stackNBT.setString(UUID_TAG, playerUUID);
            player.addChatComponentMessage(new TextComponentTranslation("message.book.link", new Object[0]));
        }
        return super.onItemRightClick(stack, world, player, hand);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(final ItemStack stack, final EntityPlayer player, final List<String> tooltip, final boolean flag) {
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey(OWNER_TAG)) {
            tooltip.add(I18n.format("message.book.owner", new Object[0]) + " " + stack.getTagCompound().getString(OWNER_TAG));
        } else {
            tooltip.add(I18n.format("message.book.no_owner", new Object[0]));
        }
    }

}
