package com.tty.function;

import com.tty.Ari;
import com.tty.entity.sql.ServerHome;
import com.tty.lib.Lib;
import com.tty.lib.dto.Page;
import com.tty.lib.dto.SqlKey;
import com.tty.lib.dto.SqlOrderByKey;
import com.tty.lib.tool.Log;
import com.tty.tool.SQLInstance;
import org.sql2o.Connection;
import org.sql2o.Query;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class HomeManager implements BaseManager<ServerHome> {

    @Override
    public CompletableFuture<List<ServerHome>> asyncGetList(Page page, List<SqlKey> sqlKeys, List<SqlOrderByKey> orderByKeys) {
        CompletableFuture<List<ServerHome>> future = new CompletableFuture<>();
        Lib.Scheduler.runAsync(Ari.instance, i -> {
            String sql = this.buildWhereSql("SELECT * FROM %splayer_home", page, sqlKeys, orderByKeys);
            try (Connection connection = SQLInstance.SESSION_FACTORY.open()) {
                Query query = connection.createQuery(sql);
                for (SqlKey key : sqlKeys) {
                    query.addParameter(key.getValueKey(), key.getValue());
                }
                future.complete(query.executeAndFetch(ServerHome.class));
            } catch (Exception e) {
                Log.error("query home error!", e);
                future.complete(List.of());
            }
        });
        return future;
    }

    @Override
    public CompletableFuture<ServerHome> asyncGetInstance(List<SqlKey> sqlKeys) {
        CompletableFuture<ServerHome> future = new CompletableFuture<>();
        Lib.Scheduler.runAsync(Ari.instance, i -> {
            String sql = this.buildWhereSql("select * from %splayer_home", null, sqlKeys, null);
            try (Connection connection = SQLInstance.SESSION_FACTORY.open()) {
                Query query = connection.createQuery(sql);
                for (SqlKey key : sqlKeys) {
                    query.addParameter(key.getValueKey(), key.getValue());
                }
                future.complete(query.executeAndFetchFirst(ServerHome.class));
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
