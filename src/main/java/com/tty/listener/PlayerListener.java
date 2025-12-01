package com.tty.listener;

import com.tty.dto.event.CustomPlayerRespawnEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;

public class PlayerListener implements Listener {
    @EventHandler
    public void onRespawn(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        if (event.getInventory().getType() != InventoryType.CRAFTING || !player.isDead() || !player.isConnected() || player.getHealth() > 0) return;
        // do stuff
        Bukkit.getPluginManager().callEvent(new CustomPlayerRespawnEvent(player, player.getLocation()));
    }
}
