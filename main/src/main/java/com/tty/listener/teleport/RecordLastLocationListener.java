package com.tty.listener.teleport;

import com.tty.dto.event.CustomPlayerRespawnEvent;
import com.tty.function.TeleportThread;
import com.tty.tool.TextTool;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class RecordLastLocationListener implements Listener {
    @EventHandler
    public void lastLocation(PlayerTeleportEvent event) {
        TeleportThread.lastLocation.put(event.getPlayer().getUniqueId(), event.getFrom());
    }
    @EventHandler
    public void lastDeathLocation(PlayerDeathEvent event) {
        TeleportThread.lastLocation.put(event.getPlayer().getUniqueId(), event.getPlayer().getLocation());
    }
    @EventHandler
    public void onRespawn(CustomPlayerRespawnEvent event) {
        Player player = event.getPlayer();
        Location deathLocation = TeleportThread.lastLocation.get(player.getUniqueId());
        if (deathLocation == null) return;
        String s = TextTool.XYZText(deathLocation.getX(), deathLocation.getY(), deathLocation.getZ());
        event.getPlayer().sendMessage(TextTool.setHEXColorText("&b上次死亡的地点: " + s + " &7输入/back返回"));
    }
    @EventHandler
    public void cleanPlayerLastLocation(PlayerQuitEvent event) {
        TeleportThread.lastLocation.remove(event.getPlayer().getUniqueId());
    }
}
