package com.tty.function;

import com.tty.Ari;
import com.tty.entity.sql.ServerPlayer;
import com.tty.lib.Lib;
import com.tty.tool.Log;
import com.tty.tool.SQLInstance;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.sql2o.Connection;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class PlayerManager implements BaseManager<ServerPlayer> {

    private final Player player;
    private final OfflinePlayer offlinePlayer;

    private PlayerManager(Player player) {
        this.player = player;
        this.offlinePlayer = null;
    }

    private PlayerManager(OfflinePlayer offlinePlayer) {
        this.offlinePlayer = offlinePlayer;
        this.player = null;
    }

    @Override
    public CompletableFuture<List<ServerPlayer>> asyncGetList(int pageNum, int pageSize) {
        return null;
    }

    @Override
    public CompletableFuture<List<String>> asyncGetIdList() {
        return null;
    }

    @Override
    public CompletableFuture<ServerPlayer> asyncGetInstance(String id) {
        CompletableFuture<ServerPlayer> future = new CompletableFuture<>();
        Lib.Scheduler.runAsync(Ari.instance, i -> {
            try (Connection connection = SQLInstance.SESSION_FACTORY.open()) {
                ServerPlayer serverPlayer = connection.createQuery(
                               """
                                    select * from %splayers
                                    where player_uuid = :id
                                """.formatted(SQLInstance.getTablePrefix()))
                        .addParameter("id", id).executeAndFetchFirst(ServerPlayer.class);
                future.complete(serverPlayer);
            } catch (Exception e) {
                future.complete(null);
                Log.error("error", e);
            }
        });
        return future;
    }

    @Override
    public void createInstance(String id) {
        long time = System.currentTimeMillis();
        assert this.player != null;
        Lib.Scheduler.runAsync(Ari.instance, i -> {
            try (Connection connection = SQLInstance.SESSION_FACTORY.open()) {
                connection.createQuery("""
                    insert into %splayers
                    (player_name, player_uuid, first_login_time, last_login_off_time, total_online_time, name_prefix, name_suffix)
                    values
                    (:player_name, :playerUUID, :firstLoginTime, :lastLoginOffTime, :totalOnlineTime, :namePrefix, :nameSuffix)
                """.formatted(SQLInstance.getTablePrefix()))
                        .addParameter("player_name", player.getName())
                        .addParameter("playerUUID", id)
                        .addParameter("firstLoginTime", time)
                        .addParameter("lastLoginOffTime", time)
                        .addParameter("totalOnlineTime", 0L)
                        .addParameter("namePrefix", "")
                        .addParameter("nameSuffix", "").executeUpdate();
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> deleteInstance(String id) {
        return null;
    }

    @Override
    public CompletableFuture<Boolean> modify(ServerPlayer instance) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        Lib.Scheduler.runAsync(Ari.instance, i -> {
            try (Connection connection = SQLInstance.SESSION_FACTORY.open()) {
                connection.createQuery("""
                                    update %splayers set
                                        first_login_time = :first_login_time,
                                        last_login_off_time = :last_login_off_time,
                                        total_online_time = :total_online_time,
                                        name_prefix = :name_prefix,
                                        name_suffix = :name_suffix
                                    where player_uuid  = :player_uuid
                                """.formatted(SQLInstance.getTablePrefix())).addParameter("first_login_time", instance.getFirstLoginTime())
                        .addParameter("last_login_off_time", instance.getLastLoginOffTime())
                        .addParameter("total_online_time", instance.getTotalOnlineTime())
                        .addParameter("name_prefix", instance.getNamePrefix())
                        .addParameter("name_suffix", instance.getNameSuffix())
                        .addParameter("player_uuid", instance.getPlayerUUID()).executeUpdate();
                future.complete(true);
            } catch (Exception e) {
                future.complete(false);
                Log.error("error", e);
            }
        });
        return future;
    }

    public static PlayerManager build(Player player) {
        return new PlayerManager(player);
    }

    public static PlayerManager build(OfflinePlayer offlinePlayer) {
        return new PlayerManager(offlinePlayer);
    }
}
