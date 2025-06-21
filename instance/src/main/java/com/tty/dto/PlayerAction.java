package com.tty.dto;

import com.tty.Ari;
import com.tty.lib.Lib;
import com.tty.lib.task.CancellableTask;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;

import static com.tty.listener.player.PlayerActionListener.PLAYER_SIT_ACTION_MAP;

public class PlayerAction {

    private final Player player;
    private CancellableTask task;

    public PlayerAction(Player player) {
        this.player = player;
    }

    public void sit(Location location) {
        this.player.getWorld().spawn(location, ArmorStand.class, entity -> {
            entity.setInvulnerable(true);
            entity.setGravity(false);
            entity.setSmall(true);
            entity.setMarker(true);
            entity.setInvisible(true);
            entity.setRotation(location.getYaw(), 0);
            entity.addPassenger(this.player);
        });
        this.player.setRotation(location.getYaw(), 0);
        this.task = Lib.Scheduler.runAtEntityFixedRate(Ari.instance, this.player, i -> {
            String name = this.player.getLocation().clone().subtract(0, -1, 0).getBlock().getType().name();
            if (this.player.isDead() || this.player.isFlying() || (!name.endsWith("_STAIRS") && !name.endsWith("_SLAB"))) {
                i.cancel();
                this.removeVehicle();
            }
        }, () -> {}, 20L, 20L);
    }

    public void cancel() {
        this.task.cancel();
        this.removeVehicle();
    }

    private void removeVehicle() {
        List<Entity> nearbyEntities = this.player.getNearbyEntities(0, this.player.getY(), 0);
        for (Entity entity : nearbyEntities) {
            if (entity instanceof ArmorStand armorStand && armorStand.isSmall()) {
                entity.remove();
            }
        }
        PLAYER_SIT_ACTION_MAP.remove(this.player);
    }

}
