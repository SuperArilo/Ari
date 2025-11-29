package com.tty.listener.player;

import com.tty.Ari;
import com.tty.entity.sql.ServerPlayer;
import com.tty.entity.sql.WhitelistInstance;
import com.tty.enumType.FilePath;
import com.tty.function.PlayerManager;
import com.tty.function.Teleporting;
import com.tty.function.WhitelistManager;
import com.tty.lib.Log;
import com.tty.lib.enum_type.LangType;
import com.tty.lib.tool.ComponentUtils;
import com.tty.tool.ConfigUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

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
        if(!Ari.instance.getConfig().getBoolean("server.whitelist.enable", false)) return;
        WhitelistManager manager = new WhitelistManager(false);
        PlayerManager playerManager = new PlayerManager(true);
        manager.getInstance(player.getUniqueId().toString())
                .thenCompose(instance -> {
                    if (instance == null) {
                        if(player.isOp()) {
                            WhitelistInstance n = new WhitelistInstance();
                            n.setAddTime(System.currentTimeMillis());
                            n.setPlayerUUID(player.getUniqueId().toString());
                            manager.createInstance(n);
                        } else {
                            event.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, ConfigUtils.t("server.message.on-whitelist-login"));
                        }
                        return CompletableFuture.completedFuture(null);
                    } else {
                        return playerManager.getInstance(instance.getPlayerUUID());
                    }
                })
                .thenAccept(instance -> {
                    if (instance == null) return;
                    if (instance.getPlayerName().equals(player.getName())) return;
                    Log.debug("layer changed name. old: %s, new: %s", instance.getPlayerName(), player.getName());
                    instance.setPlayerName(player.getName());
                    playerManager.modify(instance);
                }).exceptionally(i -> {
                    Log.error(i, "whitelist error");
                    event.disallow(PlayerLoginEvent.Result.KICK_OTHER, ComponentUtils.text(i.getMessage()));
                    return null;
                });
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

        long nowLoginTime = System.currentTimeMillis();

        PLAYER_LOGIN_TIMES.put(player.getUniqueId(), nowLoginTime);
        manager.getInstance(player.getUniqueId().toString())

                .thenAccept(i -> {
                    if(!player.hasPlayedBefore()) {
                        if (Ari.C_INSTANCE.getValue("main.first-join", FilePath.SPAWN_CONFIG, Boolean.class, false) &&
                                Ari.C_INSTANCE.getValue("main.enable", FilePath.SPAWN_CONFIG, Boolean.class, false)) {
                            Location value = Ari.C_INSTANCE.getValue("main.location", FilePath.SPAWN_CONFIG, Location.class);
                            if (value != null) {
                                Teleporting.create(player, value).teleport();
                            }
                        }
                        if(first) {
                            Bukkit.broadcast(ConfigUtils.t("server.message.on-first-login", LangType.PLAYERNAME.getType(), player.getName()));
                        }
                    }
                    if(i == null) {
                        ServerPlayer serverPlayer = new ServerPlayer();
                        serverPlayer.setPlayerName(player.getName());
                        serverPlayer.setPlayerUUID(player.getUniqueId().toString());
                        serverPlayer.setFirstLoginTime(System.currentTimeMillis());
                        manager.createInstance(serverPlayer);
                    } else {
                        i.setLastLoginOffTime(nowLoginTime);
                        manager.modify(i);
                    }
                    if(login) {
                        Bukkit.broadcast(ConfigUtils.t("server.message.on-login", LangType.PLAYERNAME.getType(), player.getName()));
                    }
                }).exceptionally(i -> {
                   Log.error(i, "get player data error");
                   return null;
                });
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if(Ari.instance.getConfig().getBoolean("server.message.on-leave")) {
            event.quitMessage(ConfigUtils.t("server.message.on-leave", LangType.PLAYERNAME.getType(), player.getName()));
        }
        SavePlayerData(player, true, true);
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
        Long loginTime = PLAYER_LOGIN_TIMES.get(player.getUniqueId());
        if (loginTime == null) {
            Log.warn("No login time recorded for player: %s", player.getName());
            return;
        }
        long onlineDuration = System.currentTimeMillis() - loginTime;
        playerManager.getInstance(uuid)
                .thenCompose(serverPlayer -> {
                    if (serverPlayer == null) {
                        Log.error("Player data not found: %s", uuid);
                        return CompletableFuture.completedFuture(false);
                    }
                    serverPlayer.setTotalOnlineTime(serverPlayer.getTotalOnlineTime() + onlineDuration);
                    return playerManager.modify(serverPlayer);
                })
                .thenAccept(success -> {
                    if (success) {
                        Log.debug("Saved player data: %s", player.getName());
                        if (needRemove) {
                            PLAYER_LOGIN_TIMES.remove(player.getUniqueId());
                        }
                    } else {
                        Log.error("Failed to save player data: %s", player.getName());
                    }
                })
                .exceptionally(ex -> {
                    Log.error(ex, "Error saving player data for %s", player.getName());
                    return null;
                })
                .whenComplete((result, ex) -> playerManager.setExecutionMode(playerManager.isAsync));
    }
}
