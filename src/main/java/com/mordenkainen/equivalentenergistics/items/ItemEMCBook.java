package com.mordenkainen.equivalentenergistics.items;

import java.util.List;

import com.mordenkainen.equivalentenergistics.core.textures.TextureEnum;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemEMCBook extends ItemBase {

    private static final String OWNER_TAG = "Owner";
    private static final String UUID_TAG = "OwnerUUID";

    public ItemEMCBook() {
        super();
        setMaxStackSize(1);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(final int damage) {
        return TextureEnum.EMCBOOK.getTexture();
    }

    @Override
    public ItemStack onItemRightClick(final ItemStack stack, final World world, final EntityPlayer player) {
        if (!world.isRemote) {
            if (!stack.hasTagCompound()) {
                stack.setTagCompound(new NBTTagCompound());
            }
            final NBTTagCompound stackNBT = stack.getTagCompound();
            if (stackNBT.hasKey(OWNER_TAG) && player.isSneaking()) {
                stackNBT.removeTag(OWNER_TAG);
                stackNBT.removeTag(UUID_TAG);
                player.addChatComponentMessage(new ChatComponentText(StatCollector.translateToLocal("message.book.clear")));
                return stack;
            }
            final String playerUUID = player.getUniqueID().toString();
            if (stackNBT.hasKey(UUID_TAG) && !stackNBT.getString(UUID_TAG).equals(playerUUID)) {
                player.addChatComponentMessage(new ChatComponentText(StatCollector.translateToLocal("message.book.wrongowner")));
                return stack;
            }
            stackNBT.setString(OWNER_TAG, player.getCommandSenderName());
            stackNBT.setString(UUID_TAG, playerUUID);
            player.addChatComponentMessage(new ChatComponentText(StatCollector.translateToLocal("message.book.link")));
        }
        return stack;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(final ItemStack stack, final EntityPlayer player, final List list, final boolean par4) {
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey(OWNER_TAG)) {
            list.add(StatCollector.translateToLocal("message.book.owner") + " " + stack.getTagCompound().getString(OWNER_TAG));
        } else {
            list.add(StatCollector.translateToLocal("message.book.no_owner"));
        }
    }

}
