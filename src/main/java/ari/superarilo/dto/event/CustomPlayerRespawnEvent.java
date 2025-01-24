package ari.superarilo.dto.event;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class CustomPlayerRespawnEvent extends PlayerEvent implements Cancellable {
    private final static HandlerList handlerList = new HandlerList();
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

    public static HandlerList getHandlerList(){
        return handlerList;
    }

    public Location getRespawnLocation() {
        return respawnLocation;
    }

    public void setRespawnLocation(@NotNull Location respawnLocation) {
        this.respawnLocation = respawnLocation;
    }
}
