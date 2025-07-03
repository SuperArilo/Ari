package com.tty.dto;

import com.tty.lib.task.CancellableTask;
import com.tty.lib.tool.Log;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class BaseAction {

    public static final Map<Player, BaseAction> PLAYER_ACTION_MAP = new ConcurrentHashMap<>();

    public final Player action_player;
    public CancellableTask task;
    public Entity entity;

    public BaseAction(Player action_player) {
        this.action_player = action_player;
    }

    public void cancel() {
        this.action_player.eject();
        if (this.entity != null) {
            this.entity.remove();
        }
        if (this.task != null) {
            this.task.cancel();
            this.task = null;
        }
        if (!this.action_player.isOnline()) {
            this.action_player.saveData();
        }
        PLAYER_ACTION_MAP.remove(this.action_player);
        Log.debug("action_player: " + this.action_player.getName() + " action remove");
    }

    public abstract void action();

    public abstract boolean check();

    protected abstract void createEntity(Location location);

    public void add() {
        PLAYER_ACTION_MAP.put(action_player, this);
    }
}
