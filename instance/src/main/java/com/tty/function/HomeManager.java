package com.tty.function;

import com.tty.entity.sql.ServerHome;
import com.tty.lib.dto.Page;
import com.tty.tool.SQLInstance;
import org.bukkit.entity.Player;
import org.sql2o.Connection;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class HomeManager extends BaseManager<ServerHome> {

    private final Player player;

    public HomeManager(Player player, boolean isAsync) {
        super(isAsync);
        this.player = player;
    }

    @Override
    public CompletableFuture<List<ServerHome>> getList(Page page) {
        return this.executeTask(() -> {
            try (Connection connection = SQLInstance.SESSION_FACTORY.open()) {
                return connection.createQuery("""
                                    SELECT * FROM %splayer_home
                                    where player_uuid = :uuid
                                    order by top_slot desc,id
                                    limit :limit offset :offset
                                """.formatted(SQLInstance.getTablePrefix()))
                        .addParameter("uuid", this.player.getUniqueId().toString())
                        .addParameter("limit", page.getLimit())
                        .addParameter("offset", page.getOffset())
                        .executeAndFetch(ServerHome.class);
            }
        });
    }

    public CompletableFuture<ServerHome> getInstance(String homeId) {
        return this.executeTask(() -> {
            try (Connection connection = SQLInstance.SESSION_FACTORY.open()) {
                return connection.createQuery("""
                                    select * from %splayer_home
                                    where player_uuid = :uuid and home_id = :homeId
                                """.formatted(SQLInstance.getTablePrefix()))
                        .addParameter("uuid", this.player.getUniqueId().toString())
                        .addParameter("homeId", homeId)
                        .executeAndFetchFirst(ServerHome.class);
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> createInstance(ServerHome instance) {
        return this.executeTask(() -> {
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
                return result == 1;
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> deleteInstance(ServerHome instance) {
        return this.executeTask(() -> {
            try (Connection connection = SQLInstance.SESSION_FACTORY.open()) {
                int delete = connection.createQuery("""
                                    delete from %splayer_home
                                    where home_id = :home_id and player_uuid = :player_uuid
                                """.formatted(SQLInstance.getTablePrefix()))
                        .addParameter("home_id", instance.getHomeId())
                        .addParameter("player_uuid", instance.getPlayerUUID())
                        .executeUpdate().getResult();
                return delete == 1;
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> modify(ServerHome instance) {
        return this.executeTask(() -> {
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
                return result == 1;
            }
        });
    }
}
