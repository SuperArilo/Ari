package ari.superarilo.function;

import ari.superarilo.entity.sql.PlayerHome;
import ari.superarilo.function.impl.HomeManagerImpl;
import org.bukkit.entity.Player;


public interface HomeManager extends BaseManager<PlayerHome> {

    static HomeManager create(String playerUUID) {
        return new HomeManagerImpl(playerUUID);
    }
}
