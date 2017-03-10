package com.mordenkainen.equivalentenergistics.integration.nei;

import com.mordenkainen.equivalentenergistics.blocks.BlockEnum;
import com.mordenkainen.equivalentenergistics.core.Reference;
import com.mordenkainen.equivalentenergistics.integration.Integration;
import com.mordenkainen.equivalentenergistics.items.ItemEnum;

import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class NEIEquivalentEnergisticsConfig implements IConfigureNEI {

    @Override
    public String getName() {
        return Reference.MOD_NAME;
    }

    @Override
    public String getVersion() {
        return Reference.MOD_VERSION;
    }

    @Override
    public void loadConfig() {
        if (Integration.Mods.NEI.isEnabled()) {
            for (final BlockEnum current : BlockEnum.values()) {
                if (current.isEnabled() && current.isHidden()) {
                    API.hideItem(new ItemStack(Item.getItemFromBlock(current.getBlock())));
                }
            }
            for (final ItemEnum current : ItemEnum.values()) {
                if (current.isEnabled() && current.isHidden()) {
                    API.hideItem(current.getSizedStack(1));
                }
            }

            API.hideItem(ItemEnum.MISCITEM.getDamagedStack(1));
        }
    }

}
