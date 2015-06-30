package com.mordenkainen.equivalentenergistics.util;

import java.util.ArrayList;
import java.util.HashMap;

import com.mordenkainen.equivalentenergistics.tiles.TileEMCCrafter;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldSavedData;

public class TransmutationNbt extends WorldSavedData {

	private static HashMap<String, ArrayList<ItemStack>> transmutations = new HashMap<String, ArrayList<ItemStack>>();
	
	public TransmutationNbt(String name) {
		super(name);
	}

	@Override
	public void readFromNBT(NBTTagCompound comp) {
		int playerCount = comp.getInteger("NumRecords");
		for(int playerNum = 0; playerNum < playerCount; playerNum++) {
			ArrayList<ItemStack> playerData = new ArrayList<ItemStack>();
			NBTTagCompound playerNBT = (NBTTagCompound) comp.getTag("Player-" + playerNum);
			int stackCount = playerNBT.getInteger("StackCount");
			for(int currentStack = 0; currentStack < stackCount; currentStack++) {
				ItemStack newStack = ItemStack.loadItemStackFromNBT((NBTTagCompound) playerNBT.getTag("Stack" + currentStack));
				playerData.add(newStack);
			}
			transmutations.put(playerNBT.getString("UUID"), playerData);
		}

	}

	@Override
	public void writeToNBT(NBTTagCompound comp) {
		int playerCount = 0;
		for(String player : transmutations.keySet()) {
			NBTTagCompound playerNBT = new NBTTagCompound();
			playerNBT.setString("UUID", player);
			ArrayList<ItemStack> transmutationData = transmutations.get(player);
			int currentStack = 0;
			for(ItemStack stack : transmutationData) {
				NBTTagCompound stackNBT = new NBTTagCompound();
				stack.writeToNBT(stackNBT);
				playerNBT.setTag("Stack" + currentStack++, stackNBT);
			}
			playerNBT.setInteger("StackCount", currentStack);
			comp.setTag("Player-" + playerCount++, playerNBT);
		}
		comp.setInteger("NumRecords", playerCount);
	}
	
	public static void setPlayerKnowledge(String UUID, ArrayList<ItemStack> knowledge) {
		transmutations.put(UUID, knowledge);
		for(TileEMCCrafter crafter : TileEMCCrafter.crafterTiles) {
			crafter.playerKnowledgeChange(UUID);
		}
	}
	
	public static ArrayList<ItemStack> getPlayerKnowledge(String UUID) {
		if(transmutations.containsKey(UUID)) {
			return transmutations.get(UUID);
		} else {
			return new ArrayList<ItemStack>();
		}
	}

}
