package com.tty.listener.player;

import com.tty.tool.PermissionUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class KeepInventoryAndExperience implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getPlayer();
        boolean a = PermissionUtils.hasPermission(player, "ari.keepinventory");
        boolean b = PermissionUtils.hasPermission(player, "ari.keepexperience");
        event.setKeepInventory(a);
        event.setKeepLevel(b);
        if (a) {
            event.getDrops().clear();
        }
        if (b) {
            event.setDroppedExp(0);
        }
    }
}
