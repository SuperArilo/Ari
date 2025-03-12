package ari.superarilo.function;

import ari.superarilo.entity.sql.PlayerHome;
import ari.superarilo.function.impl.HomeManagerImpl;
import org.bukkit.entity.Player;


public interface HomeManager extends BaseManager<PlayerHome> {

    /**
     * 修改家的信息
     * @param modify 被修改的家对象
     * @return 修改成功状态。true：成功，false：失败
     */
    boolean modifyHome(PlayerHome modify);
    static HomeManager create(Player player) {
        return new HomeManagerImpl(player);
    }
}
