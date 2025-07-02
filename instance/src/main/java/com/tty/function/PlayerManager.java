package com.tty.function;

import com.tty.Ari;
import com.tty.entity.sql.ServerPlayer;
import com.tty.lib.Lib;
import com.tty.lib.dto.Page;
import com.tty.tool.SQLInstance;
import org.sql2o.Connection;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class PlayerManager implements BaseManager<ServerPlayer> {

    @Override
    public CompletableFuture<List<ServerPlayer>> asyncGetList(Page page) {
        return null;
    }

    public CompletableFuture<ServerPlayer> asyncGetInstance(String uuid) {
        CompletableFuture<ServerPlayer> future = new CompletableFuture<>();
        Lib.Scheduler.runAsync(Ari.instance, i -> {
            try (Connection connection = SQLInstance.SESSION_FACTORY.open()) {
                ServerPlayer serverPlayer = connection.createQuery("""
                                    select * from %splayers
                                    where player_uuid = :uuid
                                """.formatted(SQLInstance.getTablePrefix())).addParameter("uuid", uuid)
                        .executeAndFetchFirst(ServerPlayer.class);
                future.complete(serverPlayer);
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        });
        return future;
    }


    @Override
    public CompletableFuture<Boolean> createInstance(ServerPlayer instance) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        Lib.Scheduler.runAsync(Ari.instance, i -> {
            try (Connection connection = SQLInstance.SESSION_FACTORY.open()) {
                int result = connection.createQuery("""
                                    insert into %splayers
                                    (player_name, player_uuid, first_login_time, last_login_off_time, total_online_time, name_prefix, name_suffix)
                                    values
                                    (:playerName, :playerUUID, :firstLoginTime, :lastLoginOffTime, :totalOnlineTime, :namePrefix, :nameSuffix)
                                """.formatted(SQLInstance.getTablePrefix()))
                        .bind(instance).executeUpdate().getResult();
                future.complete(result == 1);
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        });
        return future;
    }

    @Override
    public CompletableFuture<Boolean> deleteInstance(ServerPlayer instance) {
        return null;
    }

    @Override
    public CompletableFuture<Boolean> modify(ServerPlayer instance) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        Lib.Scheduler.runAsync(Ari.instance, i -> {
            try (Connection connection = SQLInstance.SESSION_FACTORY.open()) {
                int result = connection.createQuery("""
                                    update %splayers set
                                        first_login_time = :firstLoginTime,
                                        last_login_off_time = :lastLoginOffTime,
                                        total_online_time = :totalOnlineTime,
                                        name_prefix = :namePrefix,
                                        name_suffix = :nameSuffix
                                    where player_uuid  = :playerUUID
                                """.formatted(SQLInstance.getTablePrefix()))
                        .bind(instance)
                        .executeUpdate()
                        .getResult();
                future.complete(result == 1);
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        });
        return future;
    }
}
