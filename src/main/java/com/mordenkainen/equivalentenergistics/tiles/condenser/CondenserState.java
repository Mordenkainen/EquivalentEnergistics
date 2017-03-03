package com.mordenkainen.equivalentenergistics.tiles.condenser;

import appeng.api.networking.ticking.TickRateModulation;
import net.minecraft.util.StatCollector;

public enum CondenserState {
    IDLE(TickRateModulation.IDLE, "message.condenser.statename.idle"),
    ACTIVE(TickRateModulation.URGENT, "message.condenser.statename.active"),
    BLOCKED(TickRateModulation.IDLE, "message.condenser.statename.blocked"),
    UNPOWERED(TickRateModulation.IDLE, "message.condenser.statename.no_power"),
    MISSING_CHANNEL(TickRateModulation.IDLE, "message.condenser.statename.missing_channel");

    public final String stateName;
    public final TickRateModulation tickRate;

    CondenserState(final TickRateModulation tickRate, final String stateName) {
        this.stateName = StatCollector.translateToLocal(stateName);
        this.tickRate = tickRate;
    }
    
    public String getStateName() {
        return stateName;
    }

    
    public TickRateModulation getTickRate() {
        return tickRate;
    }
}
