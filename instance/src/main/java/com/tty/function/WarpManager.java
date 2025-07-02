package com.tty.function;

import com.tty.Ari;
import com.tty.entity.sql.ServerWarp;
import com.tty.lib.Lib;
import com.tty.lib.dto.Page;
import com.tty.tool.SQLInstance;
import org.sql2o.Connection;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class WarpManager implements BaseManager<ServerWarp> {

    @Override
    public CompletableFuture<List<ServerWarp>> asyncGetList(Page page) {
        CompletableFuture<List<ServerWarp>> future = new CompletableFuture<>();
        Lib.Scheduler.runAsync(Ari.instance, i -> {
            try (Connection connection = SQLInstance.SESSION_FACTORY.open()) {
                List<ServerWarp> serverWarps = connection.createQuery("""
                                    select * from %swarps
                                    order by top_slot desc
                                    limit :limit offset :offset
                                """.formatted(SQLInstance.getTablePrefix()))
                        .addParameter("limit", page.getLimit())
                        .addParameter("offset", page.getOffset())
                        .executeAndFetch(ServerWarp.class);
                future.complete(serverWarps);
            }  catch (Exception e) {
                future.completeExceptionally(e);
            }
        });
        return future;
    }

    public CompletableFuture<List<ServerWarp>> asyncGetCountByPlayer(String uuid) {
        CompletableFuture<List<ServerWarp>> future = new CompletableFuture<>();
        Lib.Scheduler.runAsync(Ari.instance, i -> {
            try (Connection connection = SQLInstance.SESSION_FACTORY.open()) {
                List<ServerWarp> serverWarps = connection.createQuery("""
                                    select * from %swarps
                                    where create_by = :uuid
                                """.formatted(SQLInstance.getTablePrefix()))
                        .addParameter("uuid", uuid)
                        .executeAndFetch(ServerWarp.class);
                future.complete(serverWarps);
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        });
        return future;
    }

    public CompletableFuture<ServerWarp> asyncGetInstance(String warpId) {
        CompletableFuture<ServerWarp> future = new CompletableFuture<>();
        Lib.Scheduler.runAsync(Ari.instance, i -> {
            try (Connection connection = SQLInstance.SESSION_FACTORY.open()) {
                ServerWarp serverWarp = connection.createQuery("""
                                    select * from %swarps
                                    where warp_id = :warpId
                                """.formatted(SQLInstance.getTablePrefix()))
                        .addParameter("warpId", warpId)
                        .executeAndFetchFirst(ServerWarp.class);
                future.complete(serverWarp);
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
                future.complete(update == 1);
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        });
        return future;
    }
}
