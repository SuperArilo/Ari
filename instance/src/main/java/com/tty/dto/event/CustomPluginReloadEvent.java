package com.tty.dto.event;

import lombok.Getter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class CustomPluginReloadEvent<T> extends Event implements Cancellable {
    @Getter
    private final static HandlerList handlerList = new HandlerList();
    private boolean isCancelled = false;
    @Getter
    private T entity;

    public CustomPluginReloadEvent(T entity) {
        this.entity = entity;
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

}
