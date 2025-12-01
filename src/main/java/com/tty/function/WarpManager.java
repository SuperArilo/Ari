package com.tty.function;

import com.tty.Ari;
import com.tty.entity.sql.ServerWarp;
import com.tty.lib.Lib;
import com.tty.lib.dto.Page;
import com.tty.tool.SQLInstance;
import org.sql2o.Connection;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class WarpManager extends BaseManager<ServerWarp> {

    public WarpManager(boolean isAsync) {
        super(isAsync);
    }

    @Override
    public CompletableFuture<List<ServerWarp>> getList(Page page) {
        return this.executeTask(() -> {
            try (Connection connection = SQLInstance.SESSION_FACTORY.open()) {
                return connection.createQuery("""
                                    select * from %swarps
                                    order by top_slot desc
                                    limit :limit offset :offset
                                """.formatted(SQLInstance.getTablePrefix()))
                        .addParameter("limit", page.getLimit())
                        .addParameter("offset", page.getOffset())
                        .executeAndFetch(ServerWarp.class);
            }
        });
    }

    public CompletableFuture<List<ServerWarp>> getCountByPlayer(String uuid) {
        return this.executeTask(() -> {
            try (Connection connection = SQLInstance.SESSION_FACTORY.open()) {
                return connection.createQuery("""
                                    select * from %swarps
                                    where create_by = :uuid
                                """.formatted(SQLInstance.getTablePrefix()))
                        .addParameter("uuid", uuid)
                        .executeAndFetch(ServerWarp.class);
            }
        });
    }

    public CompletableFuture<ServerWarp> getInstance(String warpId) {
        return this.executeTask(() -> {
            try (Connection connection = SQLInstance.SESSION_FACTORY.open()) {
                return connection.createQuery("""
                                    select * from %swarps
                                    where warp_id = :warpId
                                """.formatted(SQLInstance.getTablePrefix()))
                        .addParameter("warpId", warpId)
                        .executeAndFetchFirst(ServerWarp.class);
            }
        });
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
        return this.executeTask(() -> {
            try (Connection connection = SQLInstance.SESSION_FACTORY.open()) {
                int update = connection.createQuery("""
                                    update %swarps set
                                        warp_name = :warpName,
                                        location = :location,
                                        show_material = :showMaterial,
                                        permission = :permission,
                                        cost = :cost,
                                        top_slot = :topSlot
                                    where warp_id = :warpId and create_by = :createBy
                                """.formatted(SQLInstance.getTablePrefix()))
                        .bind(instance)
                        .executeUpdate()
                        .getResult();
                return update == 1;
            }
        });
    }
}
