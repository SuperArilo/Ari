package com.tty.dto.event;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class CustomPlayerRespawnEvent extends PlayerEvent implements Cancellable {
    @Getter
    private final static HandlerList handlerList = new HandlerList();
    @Getter
    private Location respawnLocation;
    private boolean isCancelled = false;
    public CustomPlayerRespawnEvent(@NotNull Player who, @NotNull Location respawnLocation) {
        super(who);
        this.respawnLocation = respawnLocation;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.isCancelled = b;
    }
    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }

    public void setRespawnLocation(@NotNull Location respawnLocation) {
        this.respawnLocation = respawnLocation;
    }
}
