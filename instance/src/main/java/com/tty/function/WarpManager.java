package com.tty.function;

import com.tty.Ari;
import com.tty.entity.sql.ServerWarp;
import com.tty.lib.Lib;
import com.tty.lib.dto.Page;
import com.tty.lib.dto.SqlKey;
import com.tty.lib.dto.SqlOrderByKey;
import com.tty.tool.SQLInstance;
import org.sql2o.Connection;
import org.sql2o.Query;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class WarpManager implements BaseManager<ServerWarp> {

    @Override
    public CompletableFuture<List<ServerWarp>> asyncGetList(Page page, List<SqlKey> sqlKeys, List<SqlOrderByKey> orderByKeys) {
        CompletableFuture<List<ServerWarp>> future = new CompletableFuture<>();
        Lib.Scheduler.runAsync(Ari.instance, i -> {
            String sql = this.buildWhereSql("select * from %swarps", page, sqlKeys, orderByKeys);
            try (Connection connection = SQLInstance.SESSION_FACTORY.open()) {
                Query query = connection.createQuery(sql);
                for (SqlKey key : sqlKeys) {
                    query.addParameter(key.getValueKey(), key.getValue());
                }
                future.complete(query.executeAndFetch(ServerWarp.class));
            }  catch (Exception e) {
                future.completeExceptionally(e);
            }
        });
        return future;
    }

    @Override
    public CompletableFuture<ServerWarp> asyncGetInstance(List<SqlKey> sqlKeys) {
        CompletableFuture<ServerWarp> future = new CompletableFuture<>();
        Lib.Scheduler.runAsync(Ari.instance, i -> {
            String sql = this.buildWhereSql("select * from %swarps", null, sqlKeys, null);
            try (Connection connection = SQLInstance.SESSION_FACTORY.open()) {
                Query query = connection.createQuery(sql);
                for (SqlKey key : sqlKeys) {
                    query.addParameter(key.getValueKey(), key.getValue());
                }
                future.complete(query.executeAndFetchFirst(ServerWarp.class));
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        });
        return future;
    }

    @Override
    public CompletableFuture<Boolean> createInstance(ServerWarp instance) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        Lib.Scheduler.runAsync(Ari.instance, i -> {
            try (Connection connection = SQLInstance.SESSION_FACTORY.open()) {
                int result = connection.createQuery("""
                                    insert into %swarps
                                    (warp_id, warp_name, create_by, location, show_material, permission, cost)
                                    values
                                    (:warpId, :warpName, :createBy, :location, :showMaterial, :permission, :cost)
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
    public CompletableFuture<Boolean> deleteInstance(ServerWarp instance) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        Lib.Scheduler.runAsync(Ari.instance, i -> {
            try (Connection connection = SQLInstance.SESSION_FACTORY.open()) {
                int result = connection.createQuery("""
                                    delete from %swarps where create_by = :createBy and warp_id = :warpId
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
    public CompletableFuture<Boolean> modify(ServerWarp instance) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        Lib.Scheduler.run(Ari.instance, i -> {
            try (Connection connection = SQLInstance.SESSION_FACTORY.open()) {
                int update = connection.createQuery("""
                                    update %swarps set
                                        warp_name = :warpName, location = :location,
                                        show_material = :showMaterial, permission = :permission,
                                        cost = :cost
                                    where warp_id = :warpId and create_by = :createBy
                                """.formatted(SQLInstance.getTablePrefix()))
                        .bind(instance)
                        .executeUpdate()
                        .getResult();
                future.complete(update == 1);
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        });
        return future;
    }
}
