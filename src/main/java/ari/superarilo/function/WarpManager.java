package ari.superarilo.function;

import ari.superarilo.entity.sql.ServerWarp;
import ari.superarilo.function.impl.WarpManagerImpl;
import org.bukkit.entity.Player;

public interface WarpManager extends BaseManager<ServerWarp> {

    static WarpManager create(String playerUUID) {
        return new WarpManagerImpl(playerUUID);
    }
}
