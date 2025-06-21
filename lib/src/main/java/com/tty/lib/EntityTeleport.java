package com.tty.lib;

import io.papermc.paper.entity.TeleportFlag;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.concurrent.ExecutionException;

public class EntityTeleport {

    public static boolean teleport(Entity entity, Location targetLocation) {
        if (ServerPlatform.isFolia()) {
            try {
                Location clone = entity.getLocation().clone();
                entity.teleportAsync(targetLocation,
                        PlayerTeleportEvent.TeleportCause.PLUGIN,
                        TeleportFlag.EntityState.RETAIN_VEHICLE);
                Boolean status = entity.teleportAsync(targetLocation).get();
                if (status && entity instanceof Player player) {
                    Bukkit.getPluginManager().callEvent(
                            new PlayerTeleportEvent(
                                    player,
                                    clone,
                                    targetLocation,
                                    PlayerTeleportEvent.TeleportCause.PLUGIN));
                    entity.playSound(Sound.sound(org.bukkit.Sound.ENTITY_ENDER_EYE_DEATH, SoundCategory.PLAYERS, 1.0f, 1.0f));
                }
                return status;
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        } else {
            boolean status = entity.teleport(targetLocation,
                    PlayerTeleportEvent.TeleportCause.PLUGIN,
                    TeleportFlag.EntityState.RETAIN_VEHICLE);
            if (status) {
                entity.playSound(Sound.sound(org.bukkit.Sound.ENTITY_ENDER_EYE_DEATH, SoundCategory.PLAYERS, 1.0f, 1.0f));
                return true;
            } else {
                return false;
            }
        }
    }
}
