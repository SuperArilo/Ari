package com.tty.listener.player;

import com.tty.Ari;
import com.tty.entity.sql.ServerPlayer;
import com.tty.entity.sql.WhitelistInstance;
import com.tty.enumType.FilePath;
import com.tty.function.PlayerManager;
import com.tty.function.Teleport;
import com.tty.function.WhitelistManager;
import com.tty.lib.enum_type.LangType;
import com.tty.lib.tool.ComponentUtils;
import com.tty.lib.tool.Log;
import com.tty.tool.ConfigUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.WorldSaveEvent;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;


public class OnPlayerJoinAndLeaveListener implements Listener {

    /**
     * 记录玩家进入服务器的时间戳
     */
    public static final Map<UUID, Long> PLAYER_LOGIN_TIMES = new ConcurrentHashMap<>();

    @EventHandler
    public void whitelist(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        if (Ari.instance.getConfig().getBoolean("server.whitelist.enable", false) && !player.isOp()) {
            WhitelistManager manager = new WhitelistManager(false);
            try {
                WhitelistInstance instance = manager.getInstance(player.getUniqueId().toString()).get(3, TimeUnit.SECONDS);
                if (instance == null) {
                    event.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, ComponentUtils.text(ConfigUtils.getValue("server.message.on-whitelist-login", FilePath.Lang)));
                }
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                Log.error("whitelist error", e);
                event.disallow(PlayerLoginEvent.Result.KICK_OTHER, ComponentUtils.text(e.getMessage()));
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        boolean first = Ari.instance.getConfig().getBoolean("server.message.on-first-login", false);
        boolean login = Ari.instance.getConfig().getBoolean("server.message.on-login", false);

        PlayerManager manager = new PlayerManager(true);
        if (first || login) {
            event.joinMessage(null);
        }
        PLAYER_LOGIN_TIMES.put(player.getUniqueId(), System.currentTimeMillis());
        manager.getInstance(player.getUniqueId().toString())
                .thenAccept(i -> {
                    //表示此玩家是第一次进入服务器
                    if (i == null || !player.hasPlayedBefore()) {
                        if (ConfigUtils.getValue("main.first-join", FilePath.SpawnConfig, Boolean.class, false) &&
                                ConfigUtils.getValue("main.enable", FilePath.SpawnConfig, Boolean.class, false)) {
                            Location value = ConfigUtils.getValue("main.location", FilePath.SpawnConfig, Location.class);
                            if (value != null) {
                                Teleport.create(player, value, 0).teleport();
                            }
                        }
                        if(first) {
                            Bukkit.broadcast(
                                    ComponentUtils.text(
                                            ConfigUtils.getValue("server.message.on-first-login", FilePath.Lang)
                                                    .replace(LangType.PLAYERNAME.getType(), player.getName()), player));
                        }
                        ServerPlayer serverPlayer = new ServerPlayer();
                        serverPlayer.setPlayerName(player.getName());
                        serverPlayer.setPlayerUUID(player.getUniqueId().toString());
                        manager.createInstance(serverPlayer);
                    } else {
                        if(login) {
                            Bukkit.broadcast(
                                    ComponentUtils.text(
                                            ConfigUtils.getValue("server.message.on-login", FilePath.Lang)
                                                    .replace(LangType.PLAYERNAME.getType(), player.getName()), player));
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
            event.quitMessage(
                    ComponentUtils.text(
                            ConfigUtils.getValue("server.message.on-leave", FilePath.Lang)
                                    .replace(LangType.PLAYERNAME.getType(), player.getName()), player));
        }
        SavePlayerData(player, true, true);
    }

    @EventHandler
    public void onWorldSave(WorldSaveEvent event) {
        Collection<? extends Player> onlinePlayers = Ari.instance.getServer().getOnlinePlayers();
        for (Player onlinePlayer : onlinePlayers) {
            SavePlayerData(onlinePlayer, true, false);
        }
    }

    /**
     * 保存玩家在线时长数据数据
     * @param player 被保存的玩家
     * @param asyncMode 保存模式。同步和异步
     * @param needRemove 保存完成是否移除玩家进服时间
     */
    public static void SavePlayerData(Player player, boolean asyncMode, boolean needRemove) {
        // 临时设置执行模式（针对本次调用）
        PlayerManager playerManager = new PlayerManager(asyncMode);
        playerManager.setExecutionMode(asyncMode);
        String uuid = player.getUniqueId().toString();
        long currentTime = System.currentTimeMillis();
        Long loginTime = PLAYER_LOGIN_TIMES.get(player.getUniqueId());
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
                        if (needRemove) {
                            PLAYER_LOGIN_TIMES.remove(player.getUniqueId());
                        }
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
