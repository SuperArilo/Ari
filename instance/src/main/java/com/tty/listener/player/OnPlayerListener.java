package com.tty.listener.player;

import com.tty.Ari;
import com.tty.enumType.FilePath;
import com.tty.function.PlayerManager;
import com.tty.lib.tool.Log;
import com.tty.tool.TextTool;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;

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
        Player player = event.getPlayer();
        PlayerManager build = PlayerManager.build(player);
        event.joinMessage(null);
        build.asyncGetInstance(player.getUniqueId().toString()).thenAccept(i -> {
            if (i == null || !player.hasPlayedBefore()) {
                if(Ari.instance.getConfig().getBoolean("server.message.on-first-login")) {
                    Bukkit.broadcast(TextTool.setHEXColorText("server.message.on-first-login", FilePath.Lang, player));
                }
                build.createInstance(player.getUniqueId().toString());
            } else {
                if(Ari.instance.getConfig().getBoolean("server.message.on-login")) {
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
