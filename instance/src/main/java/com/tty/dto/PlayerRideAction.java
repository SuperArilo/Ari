package com.tty.dto;

import com.tty.Ari;
import com.tty.lib.Lib;
import com.tty.lib.task.CancellableTask;
import com.tty.lib.tool.Log;
import org.bukkit.Location;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.Player;


import static com.tty.listener.player.PlayerActionListener.PLAYER_RIDE_ACTION_MAP;

public class PlayerRideAction {

    private final Player player;
    private final AreaEffectCloud cloud;
    private CancellableTask task;

    public PlayerRideAction(Player player) {
        this.player = player;
        Location location = player.getEyeLocation();
        this.cloud = player.getWorld().spawn(location, AreaEffectCloud.class, entity -> {
            entity.setInvulnerable(true);
            entity.setGravity(false);
            entity.setInvisible(false);
            entity.setRadius(0);
            entity.setRotation(location.getYaw(), 0);
            player.addPassenger(entity);
            this.createTask();
        });
    }

    public void addPassenger(Player passenger) {
        this.cloud.addPassenger(passenger);
    }

    private void createTask() {
        this.task = Lib.Scheduler.runAtEntityFixedRate(Ari.instance, this.player, i -> {
            if (this.cloud.getPassengers().isEmpty()) {
                this.cancel();
                Log.debug("From " + this.player.getName() + " - " + "ride length: " + PLAYER_RIDE_ACTION_MAP.size());
            }
        }, () -> {}, 1L, 20L);
    }

    public void cancel() {
        if (this.cloud == null) return;
        if (this.task != null) {
            this.task.cancel();
            this.task = null;
        }
        this.cloud.eject();
        this.cloud.remove();
        PLAYER_RIDE_ACTION_MAP.remove(this.player);
        //同步移除实体和状态
        if (!this.player.isOnline()) {
            this.player.saveData();
        }
    }
}
