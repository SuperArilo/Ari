package com.tty.papi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HomePAPI extends PlaceholderExpansion {


    public HomePAPI() {
    }

    @Override
    public @NotNull String getIdentifier() {
        return "ari";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Arilo007";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        return super.onPlaceholderRequest(player, params);
    }
}
