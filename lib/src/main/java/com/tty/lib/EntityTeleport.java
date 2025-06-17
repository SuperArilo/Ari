package com.tty.lib;

import io.papermc.paper.entity.TeleportFlag;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.concurrent.ExecutionException;

public class EntityTeleport {

    public static boolean teleport(Entity entity, Location targetLocation) {
        if (ServerPlatform.isFolia()) {
            try {
                entity.teleportAsync(targetLocation,
                        PlayerTeleportEvent.TeleportCause.PLUGIN,
                        TeleportFlag.EntityState.RETAIN_VEHICLE);
                return entity.teleportAsync(targetLocation).get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        } else {
            return entity.teleport(targetLocation);
        }
    }
}
