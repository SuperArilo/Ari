package com.tty.function;

import com.tty.entity.sql.WhitelistInstance;
import com.tty.lib.dto.Page;
import com.tty.tool.SQLInstance;
import org.sql2o.Connection;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class WhitelistManager extends BaseManager<WhitelistInstance> {

    public WhitelistManager(boolean isAsync) {
        super(isAsync);
    }

    @Override
    public CompletableFuture<List<WhitelistInstance>> getList(Page page) {
        return null;
    }

    @Override
    public CompletableFuture<Boolean> createInstance(WhitelistInstance instance) {
        return this.executeTask(() -> {
           try (Connection connection = SQLInstance.SESSION_FACTORY.open()) {
               int result = connection.createQuery("""
                                   insert into %swhitelist
                                   (player_uuid, add_time)
                                   values
                                   (:playerUUID, :addTime)
                               """.formatted(SQLInstance.getTablePrefix()))
                       .bind(instance)
                       .executeUpdate()
                       .getResult();
               return result == 1;
           }
        });
    }

    @Override
    public CompletableFuture<Boolean> deleteInstance(WhitelistInstance instance) {
        return this.executeTask(() -> {
            try (Connection connection = SQLInstance.SESSION_FACTORY.open()) {
                int result = connection.createQuery("""
                                    delete from %swhitelist where player_uuid = :uuid
                                """.formatted(SQLInstance.getTablePrefix()))
                        .addParameter("uuid", instance.getPlayerUUID())
                        .executeUpdate().getResult();
                return result >= 1;
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> modify(WhitelistInstance instance) {
        return null;
    }

    public CompletableFuture<WhitelistInstance> getInstance(String uuid) {
        return this.executeTask(() -> {
           try (Connection connection = SQLInstance.SESSION_FACTORY.open()) {
               return connection.createQuery("""
                    select * from %swhitelist where player_uuid = :uuid
                """.formatted(SQLInstance.getTablePrefix()))
                       .addParameter("uuid", uuid)
                       .executeAndFetchFirst(WhitelistInstance.class);
           }
        });
    }
}
