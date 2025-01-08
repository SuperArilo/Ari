package ari.superarilo.function;

import ari.superarilo.entity.sql.PlayerHome;
import ari.superarilo.function.impl.HomeManagerImpl;
import org.bukkit.entity.Player;

import java.util.List;

public interface HomeManager {
    /**
     * 异步获取玩家保存的家列表
     * @return 家列表
     */
    List<PlayerHome> asyncGetHomeList();

    /**
     * 保存家
     * @param homeId 家的ID，不能重复
     */
    void createNewHome(String homeId);

    /**
     * 删除指定ID的家
     * @param homeId 指定homeId
     */
    void deleteHome(String homeId);

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
