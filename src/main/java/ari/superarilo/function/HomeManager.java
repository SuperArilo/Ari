package ari.superarilo.function;

import ari.superarilo.entity.sql.ServerHome;
import ari.superarilo.function.impl.HomeManagerImpl;


public interface HomeManager extends BaseManager<ServerHome> {

    static HomeManager create(String playerUUID) {
        return new HomeManagerImpl(playerUUID);
    }
}
