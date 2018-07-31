package com.mordenkainen.equivalentenergistics.core;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.datafix.IFixableData;

public class FixTE implements IFixableData {

    @Override
    public int getFixVersion() {
        return 1;
    }

    @Override
    public NBTTagCompound fixTagCompound(final NBTTagCompound compound) {
        final String oldName = compound.getString("id").replace("minecraft:", "");
        switch (oldName) {
            case "equivalentenergistics.emc_crafter":
                compound.setString("id", Reference.MOD_ID + ":emc_crafter");
                break;
            case "equivalentenergistics.emc_crafter_adv":
                compound.setString("id", Reference.MOD_ID + ":emc_crafter_adv");
                break;
            case "equivalentenergistics.emc_crafter_ext":
                compound.setString("id", Reference.MOD_ID + ":emc_crafter_ext");
                break;
            case "equivalentenergistics.emc_crafter_ult":
                compound.setString("id", Reference.MOD_ID + ":emc_crafter_ult");
                break;
            case "equivalentenergistics.emc_condenser":
                compound.setString("id", Reference.MOD_ID + ":emc_condenser");
                break;
            case "equivalentenergistics.emc_condenser_adv":
                compound.setString("id", Reference.MOD_ID + ":emc_condenser_adv");
                break;
            case "equivalentenergistics.emc_condenser_ext":
                compound.setString("id", Reference.MOD_ID + ":emc_condenser_ext");
                break;
            case "equivalentenergistics.emc_condenser_ult":
                compound.setString("id", Reference.MOD_ID + ":emc_condenser_ult");
                break;
            default:
                break;
        }
        return compound;
    }

}
