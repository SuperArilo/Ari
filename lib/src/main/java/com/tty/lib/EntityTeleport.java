package com.tty.lib;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

import java.util.concurrent.ExecutionException;

public class EntityTeleport {

    public static boolean teleport(Entity entity, Location targetLocation) {
        if (ServerPlatform.isFolia()) {
            try {
                return entity.teleportAsync(targetLocation).get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        } else {
            return entity.teleport(targetLocation);
        }
    }

    public static boolean teleport(Entity entity, Entity targetEntity) {
        return entity.teleport(targetEntity);
    }
}
