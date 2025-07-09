package com.tty.function;

import com.tty.entity.sql.ServerSpawn;
import com.tty.lib.dto.Page;
import com.tty.tool.SQLInstance;
import org.sql2o.Connection;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class SpawnManager extends BaseManager<ServerSpawn> {

    public SpawnManager(boolean isAsync) {
        super(isAsync);
    }

    @Override
    public CompletableFuture<List<ServerSpawn>> getList(Page page) {
        return this.executeTask(() -> {
           try (Connection connection = SQLInstance.SESSION_FACTORY.open()) {
               return connection.createQuery("""
                          select * from %sspawn
                          order by top_slot desc, id
                          limit :limit offset :offset
                      """.formatted(SQLInstance.getTablePrefix()))
                       .addParameter("limit", page.getLimit())
                       .addParameter("offset", page.getOffset())
                       .executeAndFetch(ServerSpawn.class);
           }
        });
    }

    @Override
    public CompletableFuture<Boolean> createInstance(ServerSpawn instance) {
        return this.executeTask(() -> {
           try (Connection connection = SQLInstance.SESSION_FACTORY.open()) {
               int result = connection.createQuery("""
                                   insert into %sspawn
                                   (spawn_id, spawn_name, world, location, create_by, show_material,create_time)
                                   values
                                   (:spawnId, :spawnName, :world, :location, :createBy, :showMaterial, :createTime)
                               """.formatted(SQLInstance.getTablePrefix()))
                       .bind(instance)
                       .executeUpdate()
                       .getResult();
               return result == 1;
           }
        });
    }

    @Override
    public CompletableFuture<Boolean> deleteInstance(ServerSpawn instance) {
        return this.executeTask(() -> {
            try (Connection connection = SQLInstance.SESSION_FACTORY.open()) {
                int result = connection.createQuery("delete from %sspawn where spawn_id == :id".formatted(SQLInstance.getTablePrefix()))
                        .addParameter("id", instance.getSpawnId())
                        .executeUpdate()
                        .getResult();
                return result == 1;
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> modify(ServerSpawn instance) {
        return this.executeTask(() -> {
           try (Connection connection = SQLInstance.SESSION_FACTORY.open()) {
               int result = connection.createQuery("""
                                   update %sspawn set
                                   spawn_name = :spawnName,
                                   world = :world,
                                   location = :location,
                                   permission = :permission,
                                   top_slot = :topSlot
                                   where spawn_id = :spawnId
                               """.formatted(SQLInstance.getTablePrefix()))
                       .bind(instance)
                       .executeUpdate()
                       .getResult();
               return result == 1;
           }
        });
    }

    public CompletableFuture<ServerSpawn> getInstance(String spawnId) {
        return this.executeTask(() -> {
           try (Connection connection = SQLInstance.SESSION_FACTORY.open()) {
               return connection.createQuery("select * from %sspawn where spawn_id = :id".formatted(SQLInstance.getTablePrefix()))
                       .addParameter("id", spawnId)
                       .executeAndFetchFirst(ServerSpawn.class);
           }
        });
    }
}
