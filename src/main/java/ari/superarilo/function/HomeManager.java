package ari.superarilo.function;

import ari.superarilo.entity.sql.PlayerHome;
import ari.superarilo.function.impl.HomeManagerImpl;
import org.bukkit.entity.Player;

import java.util.List;

public interface HomeManager {
    List<PlayerHome> asyncGetHomeList();
    void createNewHome(String homeId);
    void deleteHome(String homeName);
    boolean modifyHome(PlayerHome modify);
    static HomeManager create(Player player) {
        return new HomeManagerImpl(player);
    }
}
