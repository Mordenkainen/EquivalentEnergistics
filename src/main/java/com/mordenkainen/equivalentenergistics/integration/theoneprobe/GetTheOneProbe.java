package com.mordenkainen.equivalentenergistics.integration.theoneprobe;

import com.google.common.base.Function;

import mcjty.theoneprobe.api.ITheOneProbe;

public class GetTheOneProbe implements Function<ITheOneProbe, Void> {

    @Override
    public Void apply(final ITheOneProbe probe) {
        probe.registerProvider(new ProbeAEBaseHUDHandler());
        probe.registerProvider(new ProbeCondenserHUDHandler());
        probe.registerProvider(new ProbeCrafterHUDHandler());
        
        
        return null;
    }

}
