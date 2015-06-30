package com.mordenkainen.equivalentenergistics.items;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.mordenkainen.equivalentenergistics.EquivalentEnergistics;
import com.mordenkainen.equivalentenergistics.lib.Ref;
import com.mordenkainen.equivalentenergistics.util.TransmutationNbt;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import moze_intel.projecte.playerData.Transmutation;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
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
		setCreativeTab(EquivalentEnergistics.tabEE);
		setUnlocalizedName(Ref.getId("EMCBook"));
	}
	
	@Override
	public void registerIcons(IIconRegister reg) {
		itemIcon = Items.book.getIconFromDamage(0);
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
			ArrayList<ItemStack> currentTransmutations = TransmutationNbt.getPlayerKnowledge(stackNBT.getString("OwnerUUID"));
			ArrayList<ItemStack> newTransmutations = getPlayerKnowledge(player);
			boolean result = new HashSet(currentTransmutations).equals(new HashSet(newTransmutations));
			if(!result) {
				TransmutationNbt.setPlayerKnowledge(playerUUID, newTransmutations);
				EquivalentEnergistics.transmutations.markDirty();
			}
			player.addChatComponentMessage(new ChatComponentText(StatCollector.translateToLocal("message.book.link")));
		}
        return stack;
    }

	private ArrayList<ItemStack> getPlayerKnowledge(EntityPlayer player) {
		int methodType = 2;
		Method gkMethod = null;
		List<ItemStack> tmpTransmutations = null;
		ArrayList<ItemStack> transmutations = new ArrayList<ItemStack>();

		try {
			gkMethod = Transmutation.class.getDeclaredMethod("getKnowledge", new Class[] {String.class});
			methodType = 0;
		} catch (Exception e) {
			try {
				gkMethod = Transmutation.class.getDeclaredMethod("getKnowledge", new Class[] {EntityPlayer.class});
				methodType = 1;
			} catch (Exception e1) {} 			
		}
		
		try {
			switch(methodType){
				case 0:
					tmpTransmutations = (List<ItemStack>) gkMethod.invoke(null, new Object[] {player.getCommandSenderName()});
					break;
				case 1:
					tmpTransmutations = (List<ItemStack>) gkMethod.invoke(null, new Object[] {player});
					break;
			}
		} catch (Exception e) {}
		
		if(tmpTransmutations != null) {
			for(ItemStack currentItem : tmpTransmutations) {
				if(currentItem.getItem() != EquivalentEnergistics.itemEMCCrystal) {
					transmutations.add(currentItem);
				}
			}
		}
		
		return transmutations;
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
