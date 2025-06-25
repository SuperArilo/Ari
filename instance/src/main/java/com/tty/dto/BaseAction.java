package com.tty.dto;

import com.tty.lib.task.CancellableTask;
import com.tty.lib.tool.Log;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public abstract class BaseAction {

    public final Player player;
    public CancellableTask task;
    public Entity entity;

    public BaseAction(Player player) {
        this.player = player;
    }

    public void cancel() {
        this.player.eject();
        if (this.entity != null) {
            this.entity.remove();
        }
        if (this.task != null) {
            this.task.cancel();
            this.task = null;
        }
        if (!this.player.isOnline()) {
            this.player.saveData();
        }
        Log.debug("player: " + this.player.getName() + " action remove");
    }

    public abstract boolean action(@Nullable Entity entity);

    public abstract boolean check();

    protected abstract void createEntity(Location location);
}
