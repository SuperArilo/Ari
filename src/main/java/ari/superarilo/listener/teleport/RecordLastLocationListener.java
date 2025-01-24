package ari.superarilo.listener.teleport;

import ari.superarilo.Ari;
import ari.superarilo.dto.event.CustomPlayerRespawnEvent;
import ari.superarilo.function.impl.TeleportThreadImpl;
import ari.superarilo.tool.Log;
import ari.superarilo.tool.TextTool;
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
        TeleportThreadImpl.lastLocation.put(event.getPlayer().getUniqueId(), event.getFrom());
        Log.debug("TeleportStatusList: " + Ari.instance.tpStatusValue.getStatusList().size());
    }
    @EventHandler
    public void lastDeathLocation(PlayerDeathEvent event) {
        TeleportThreadImpl.lastLocation.put(event.getPlayer().getUniqueId(), event.getPlayer().getLocation());
    }
    @EventHandler
    public void onRespawn(CustomPlayerRespawnEvent event) {
        Player player = event.getPlayer();
        Location deathLocation = TeleportThreadImpl.lastLocation.get(player.getUniqueId());
        if (deathLocation == null) return;
        String s = TextTool.XYZText(deathLocation.getX(), deathLocation.getY(), deathLocation.getZ());
        event.getPlayer().sendMessage(TextTool.setHEXColorText("&b上次死亡的地点: " + s + " &7输入/back返回"));
    }
    @EventHandler
    public void cleanPlayerLastLocation(PlayerQuitEvent event) {
        TeleportThreadImpl.lastLocation.remove(event.getPlayer().getUniqueId());
    }
}
