package com.tty.listener.player;

import com.tty.Ari;
import com.tty.enumType.FilePath;
import com.tty.function.PlayerManager;
import com.tty.lib.EntityTeleport;
import com.tty.lib.Lib;
import com.tty.lib.tool.Log;
import com.tty.tool.TextTool;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class OnPlayerListener implements Listener {
    /**
     * 记录玩家进入服务器的时间戳
     */
    private static final Map<UUID, Long> playerLoginTimes = new HashMap<>();
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        boolean first = Ari.instance.getConfig().getBoolean("server.message.on-first-login", false);
        boolean login = Ari.instance.getConfig().getBoolean("server.message.on-login", false);
        Player player = event.getPlayer();
        PlayerManager build = PlayerManager.build(player);
        if (first || login) {
            event.joinMessage(null);
        }
        build.asyncGetInstance(player.getUniqueId().toString()).thenAccept(i -> {
            if (i == null || !player.hasPlayedBefore()) {
                if (Ari.instance.getConfig().getBoolean("server.spawn.first-join", false)) {
                    Lib.Scheduler.runAtEntity(Ari.instance, player, a -> {
                        Location spawnLocation = player.getWorld().getSpawnLocation();
                        EntityTeleport.teleport(player, spawnLocation);
                    }, () -> {});
                }
                if(first) {
                    Bukkit.broadcast(TextTool.setHEXColorText("server.message.on-first-login", FilePath.Lang, player));
                }
                build.createInstance(player.getUniqueId().toString());
            } else {
                if(login) {
                    Bukkit.broadcast(TextTool.setHEXColorText("server.message.on-login", FilePath.Lang, player));
                }
            }
        });
        playerLoginTimes.put(player.getUniqueId(), System.currentTimeMillis());
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if(Ari.instance.getConfig().getBoolean("server.message.on-leave")) {
            event.quitMessage(TextTool.setHEXColorText("server.message.on-leave", FilePath.Lang, player));
        }

        PlayerManager manager = PlayerManager.build(player);
        manager.asyncGetInstance(player.getUniqueId().toString())
                .thenAccept(p -> {
                    long l = System.currentTimeMillis();
                    if(p == null) {
                        Log.warning("player: " + player.getUniqueId() + " data is null, exiting");
                        return;
                    }
                    p.setLastLoginOffTime(l);
                    p.setTotalOnlineTime(p.getTotalOnlineTime() + l - playerLoginTimes.get(player.getUniqueId()));
                    manager.modify(p);
                    playerLoginTimes.remove(player.getUniqueId());
                });
    }

}
