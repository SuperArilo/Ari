package ari.superarilo.function;

import ari.superarilo.entity.sql.ServerWarp;
import ari.superarilo.function.impl.WarpManagerImpl;
import org.bukkit.entity.Player;

import java.util.List;

public interface WarpManager {
    /**
     * 异步获取保存的地标列表
     * @param pageNum 页数
     * @param pageSize 每页的数量
     * @return 地标列表
     */
    List<ServerWarp> asyncGetWarpList(int pageNum, int pageSize);

    static WarpManager create(Player player) {
        return new WarpManagerImpl(player);
    }
}
