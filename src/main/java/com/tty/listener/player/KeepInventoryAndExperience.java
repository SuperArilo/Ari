package com.tty.listener.player;

import com.tty.lib.tool.PermissionUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class KeepInventoryAndExperience implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getPlayer();
        if (PermissionUtils.hasPermission(player, "ari.keepinventory")) {
            event.setKeepInventory(true);
            event.getDrops().clear();
        }
        if (PermissionUtils.hasPermission(player, "ari.keepexperience")) {
            event.setKeepLevel(true);
            event.setDroppedExp(0);
        }
    }
}
