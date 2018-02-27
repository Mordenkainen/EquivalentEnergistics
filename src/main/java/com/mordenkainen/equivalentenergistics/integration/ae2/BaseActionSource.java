package com.mordenkainen.equivalentenergistics.integration.ae2;

import java.util.Optional;

import appeng.api.networking.security.IActionHost;
import appeng.api.networking.security.IActionSource;
import net.minecraft.entity.player.EntityPlayer;

public class BaseActionSource implements IActionSource {

    @Override
    public Optional<EntityPlayer> player() {
        return Optional.empty();
    }

    @Override
    public Optional<IActionHost> machine() {
        return Optional.empty();
    }

    @Override
    public <T> Optional<T> context(Class<T> key) {
        return Optional.empty();
    }
    
}
