package com.mordenkainen.equivalentenergistics.integration.ae2;

import java.util.Optional;

import net.minecraft.entity.player.EntityPlayer;

import appeng.api.networking.security.IActionHost;
import appeng.api.networking.security.IActionSource;


public class MachineSource implements IActionSource
{

    private final IActionHost via;

    public MachineSource( final IActionHost v )
    {
        this.via = v;
    }

    @Override
    public Optional<EntityPlayer> player()
    {
        return Optional.empty();
    }

    @Override
    public Optional<IActionHost> machine()
    {
        return Optional.of( this.via );
    }

    @Override
    public <T> Optional<T> context(final Class<T> key)
    {
        return Optional.empty();
    }

}
