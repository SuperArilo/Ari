package com.tty.listener.teleport;

import com.tty.dto.event.CustomPlayerRespawnEvent;
import com.tty.enumType.FilePath;
import com.tty.function.TeleportThread;
import com.tty.lib.ServerPlatform;
import com.tty.lib.enum_type.LangType;
import com.tty.tool.ConfigObjectUtils;
import com.tty.tool.TextTool;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class RecordLastLocationListener implements Listener {
    @EventHandler
    public void lastLocation(PlayerTeleportEvent event) {
        PlayerTeleportEvent.TeleportCause cause = event.getCause();
        if (!cause.equals(PlayerTeleportEvent.TeleportCause.PLUGIN)) return;
        TeleportThread.lastLocation.put(event.getPlayer().getUniqueId(), event.getFrom());
    }
    @EventHandler
    public void lastDeathLocation(PlayerDeathEvent event) {
        TeleportThread.lastLocation.put(event.getPlayer().getUniqueId(), event.getPlayer().getLocation());
    }
    @EventHandler
    public void onRespawn(CustomPlayerRespawnEvent event) {
        if (!ServerPlatform.isFolia()) return;
        this.setPlayerLastLocation(event);
    }
    @EventHandler
    public void onRespawnOnPaper(PlayerRespawnEvent event) {
        if (ServerPlatform.isFolia()) return;
        this.setPlayerLastLocation(event);
    }
    @EventHandler
    public void cleanPlayerLastLocation(PlayerQuitEvent event) {
        TeleportThread.lastLocation.remove(event.getPlayer().getUniqueId());
    }

    private void setPlayerLastLocation(PlayerEvent event) {
        Player player = event.getPlayer();
        Location deathLocation = TeleportThread.lastLocation.get(player.getUniqueId());
        if (deathLocation == null) return;
        //构建显示坐标
        String location = TextTool.XYZText(deathLocation.getX(), deathLocation.getY(), deathLocation.getZ());
        String value = ConfigObjectUtils.getValue("teleport.tips-back", FilePath.Lang.getName(), String.class, "null");
        event.getPlayer().sendMessage(TextTool.setClickEventText(value.replace(LangType.DEATHLOCATION.getType(), location), ClickEvent.Action.RUN_COMMAND, "/back"));
    }
}
