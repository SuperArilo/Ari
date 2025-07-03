package com.tty.listener.player;

import com.tty.Ari;
import com.tty.entity.sql.ServerPlayer;
import com.tty.enumType.FilePath;
import com.tty.function.PlayerManager;
import com.tty.function.Teleport;
import com.tty.lib.tool.Log;
import com.tty.tool.TextTool;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.WorldSaveEvent;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;


public class OnPlayerJoinAndLeaveListener implements Listener {

    /**
     * 记录玩家进入服务器的时间戳
     */
    private static final Map<UUID, Long> playerLoginTimes = new HashMap<>();
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        boolean first = Ari.instance.getConfig().getBoolean("server.message.on-first-login", false);
        boolean login = Ari.instance.getConfig().getBoolean("server.message.on-login", false);
        Player player = event.getPlayer();
        PlayerManager build = new PlayerManager(true);
        if (first || login) {
            event.joinMessage(null);
        }
        playerLoginTimes.put(player.getUniqueId(), System.currentTimeMillis());
        build.getInstance(player.getUniqueId().toString())
                .thenAccept(i -> {
                    if (i == null || !player.hasPlayedBefore()) {
                        if (Ari.instance.getConfig().getBoolean("server.spawn.first-join", false)) {
                            Teleport.create(player, player.getWorld().getSpawnLocation(), 0).teleport();
                        }
                        if(first) {
                            Bukkit.broadcast(TextTool.setHEXColorText("server.message.on-first-login", FilePath.Lang, player));
                        }
                        if (i == null) {
                            ServerPlayer serverPlayer = new ServerPlayer();
                            serverPlayer.setPlayerName(player.getName());
                            serverPlayer.setPlayerUUID(player.getUniqueId().toString());
                            build.createInstance(serverPlayer);
                        }
                    } else {
                        if(login) {
                            Bukkit.broadcast(TextTool.setHEXColorText("server.message.on-login", FilePath.Lang, player));
                        }
                    }
                }).exceptionally(i -> {
                   Log.error("get player data error", i);
                   return null;
                });
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if(Ari.instance.getConfig().getBoolean("server.message.on-leave")) {
            event.quitMessage(TextTool.setHEXColorText("server.message.on-leave", FilePath.Lang, player));
        }
        SavePlayerData(player, true);
    }

    @EventHandler
    public void savePlayerData(WorldSaveEvent event) {
        Collection<? extends Player> onlinePlayers = Ari.instance.getServer().getOnlinePlayers();
        for (Player onlinePlayer : onlinePlayers) {
            SavePlayerData(onlinePlayer, true);
        }
    }

    public static void SavePlayerData(Player player, boolean asyncMode) {
        // 临时设置执行模式（针对本次调用）
        PlayerManager playerManager = new PlayerManager(asyncMode);
        playerManager.setExecutionMode(asyncMode);
        String uuid = player.getUniqueId().toString();
        long currentTime = System.currentTimeMillis();
        Long loginTime = playerLoginTimes.get(player.getUniqueId());
        if (loginTime == null) {
            Log.warning("No login time recorded for player: " + player.getName());
            return;
        }
        long onlineDuration = currentTime - loginTime;
        playerManager.getInstance(uuid)
                .thenCompose(serverPlayer -> {
                    if (serverPlayer == null) {
                        Log.error("Player data not found: " + uuid);
                        return CompletableFuture.completedFuture(false);
                    }
                    serverPlayer.setLastLoginOffTime(currentTime);
                    serverPlayer.setTotalOnlineTime(serverPlayer.getTotalOnlineTime() + onlineDuration);
                    return playerManager.modify(serverPlayer);
                })
                .thenAccept(success -> {
                    if (success) {
                        Log.debug("Saved player data: " + player.getName());
                        playerLoginTimes.remove(player.getUniqueId());
                    } else {
                        Log.error("Failed to save player data: " + player.getName());
                    }
                })
                .exceptionally(ex -> {
                    Log.error("Error saving player data for " + player.getName(), ex);
                    return null;
                })
                .whenComplete((result, ex) -> playerManager.setExecutionMode(playerManager.isAsync));
    }
}
