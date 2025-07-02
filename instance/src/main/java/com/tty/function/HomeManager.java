package com.tty.function;

import com.tty.Ari;
import com.tty.entity.sql.ServerHome;
import com.tty.lib.Lib;
import com.tty.lib.dto.Page;
import com.tty.lib.tool.Log;
import com.tty.tool.SQLInstance;
import org.bukkit.entity.Player;
import org.sql2o.Connection;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class HomeManager implements BaseManager<ServerHome> {

    private final Player player;

    public HomeManager(Player player) {
        this.player = player;
    }

    @Override
    public CompletableFuture<List<ServerHome>> asyncGetList(Page page) {
        CompletableFuture<List<ServerHome>> future = new CompletableFuture<>();
        Lib.Scheduler.runAsync(Ari.instance, i -> {
            try (Connection connection = SQLInstance.SESSION_FACTORY.open()) {
                List<ServerHome> serverHomes = connection.createQuery("""
                                    SELECT * FROM %splayer_home
                                    where player_uuid = :uuid
                                    order by top_slot desc,id
                                    limit :limit offset :offset
                                """.formatted(SQLInstance.getTablePrefix()))
                        .addParameter("uuid", this.player.getUniqueId().toString())
                        .addParameter("limit", page.getLimit())
                        .addParameter("offset", page.getOffset())
                        .executeAndFetch(ServerHome.class);
                future.complete(serverHomes);
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        });
        return future;
    }

    public CompletableFuture<ServerHome> asyncGetInstance(String homeId) {
        CompletableFuture<ServerHome> future = new CompletableFuture<>();
        Lib.Scheduler.runAsync(Ari.instance, i -> {
            try (Connection connection = SQLInstance.SESSION_FACTORY.open()) {
                ServerHome serverHome = connection.createQuery("""
                                    select * from %splayer_home
                                    where player_uuid = :uuid and home_id = :homeId
                                """.formatted(SQLInstance.getTablePrefix()))
                        .addParameter("uuid", this.player.getUniqueId().toString())
                        .addParameter("homeId", homeId)
                        .executeAndFetchFirst(ServerHome.class);
                future.complete(serverHome);
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        });
        return future;
    }

    @Override
    public CompletableFuture<Boolean> createInstance(ServerHome instance) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        Lib.Scheduler.runAsync(Ari.instance, i -> {
            try (Connection connection = SQLInstance.SESSION_FACTORY.open()) {
                int result = connection.createQuery("""
                                    insert into %splayer_home
                                    (home_id, home_name, player_uuid, location, show_material)
                                    values
                                    (:homeId, :homeName, :playerUUID, :location, :showMaterial)
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

    @Override
    public CompletableFuture<Boolean> deleteInstance(ServerHome instance) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        long start = System.currentTimeMillis();
        Lib.Scheduler.runAsync(Ari.instance, i -> {
            try (Connection connection = SQLInstance.SESSION_FACTORY.open()) {
                int delete = connection.createQuery("""
                                    delete from %splayer_home
                                    where home_id = :home_id and player_uuid = :player_uuid
                                """.formatted(SQLInstance.getTablePrefix()))
                        .addParameter("home_id", instance.getHomeId())
                        .addParameter("player_uuid", instance.getPlayerUUID())
                        .executeUpdate().getResult();
                future.complete(delete == 1);
            } catch (Exception e) {
                future.completeExceptionally(e);
                Log.error("remove home fail, id: " + instance.getHomeId(), e);
            } finally {
                Log.debug(Level.INFO, "remove home time: " + (System.currentTimeMillis() - start) + "ms");
            }
        });
        return future;
    }

    @Override
    public CompletableFuture<Boolean> modify(ServerHome instance) {
        long start = System.currentTimeMillis();
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        Lib.Scheduler.runAsync(Ari.instance, i -> {
            try (Connection connection = SQLInstance.SESSION_FACTORY.open()) {
                int result = connection.createQuery("""
                                    update %splayer_home set
                                    home_name = :homeName,
                                    location = :location,
                                    show_material = :showMaterial,
                                    top_slot = :topSlot
                                    where home_id = :homeId and player_uuid = :playerUUID
                                """.formatted(SQLInstance.getTablePrefix()))
                        .bind(instance)
                        .executeUpdate()
                        .getResult();
                if (result == 1) {
                    future.complete(true);
                } else {
                    future.complete(false);
                    Log.error("save homeId: [" + instance.getHomeId() + "] error");
                }
            } catch (Exception e) {
                Log.error("save home error", e);
                future.complete(false);
            } finally {
                Log.debug("save time: " + (System.currentTimeMillis() - start) + "ms");
            }
        });
        return future;
    }
}
