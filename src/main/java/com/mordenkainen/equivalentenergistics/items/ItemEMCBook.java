package com.mordenkainen.equivalentenergistics.items;

import java.util.List;

import com.mordenkainen.equivalentenergistics.lib.Ref;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemEMCBook extends Item {

	public ItemEMCBook() {
		super();
		setMaxStackSize(1);
	}
	
	@Override
	public void registerIcons(IIconRegister reg) {
		itemIcon = reg.registerIcon(Ref.getId("EMCBook"));
	}
	
	@Override
    public void onUpdate(ItemStack stack, World world, Entity player, int p_77663_4_, boolean p_77663_5_) {
		if(!stack.hasTagCompound()) {
			stack.setTagCompound(new NBTTagCompound());
		}
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		if(!world.isRemote) {
			if(!stack.hasTagCompound()) {
				stack.setTagCompound(new NBTTagCompound());
			}
			NBTTagCompound stackNBT = stack.getTagCompound();
			if(stackNBT.hasKey("Owner") && player.isSneaking()) {
				stackNBT.removeTag("Owner");
				stackNBT.removeTag("OwnerUUID");
				player.addChatComponentMessage(new ChatComponentText(StatCollector.translateToLocal("message.book.clear")));
				return stack;
			}
			String playerUUID = player.getUniqueID().toString();
			if(stackNBT.hasKey("OwnerUUID") && !stackNBT.getString("OwnerUUID").equals(playerUUID)) {
				player.addChatComponentMessage(new ChatComponentText(StatCollector.translateToLocal("message.book.wrongowner")));
				return stack;
			}
			stackNBT.setString("Owner", player.getCommandSenderName());
			stackNBT.setString("OwnerUUID", playerUUID);
			player.addChatComponentMessage(new ChatComponentText(StatCollector.translateToLocal("message.book.link")));
		}
        return stack;
    }
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) {
		if(stack.hasTagCompound() && stack.getTagCompound().hasKey("Owner")) {
			list.add("Owner: " + stack.getTagCompound().getString("Owner"));
		} else {
			list.add("No owner set.");
		}
	}
}
