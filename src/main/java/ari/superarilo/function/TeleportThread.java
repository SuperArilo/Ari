package ari.superarilo.function;

import ari.superarilo.function.impl.TeleportThreadImpl;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface TeleportThread {
    /**
     * 在延迟多少秒后开始开始传送
     * @param delay 延迟，单位秒
     */
    void teleport(int delay);

    /**
     * 在延迟多少秒后开始开始传送
     * @param delay 延迟，单位秒
     * @param callback 回调类
     */
    void teleport(int delay, TeleportCallback callback);

    /**
     * 取消传送，必须在callback类before内调用
     */
    void cancel();
    /**
     * 玩家定点传送
     * @param player 被传送的玩家
     * @param location 目标位置
     */
    static TeleportThread playerToLocation(Player player, Location location) {
        return new TeleportThreadImpl(player, location);
    }
    /**
     * 玩家之间的传送
     * @param player 被传送玩家
     * @param targetPlayer 目标玩家
     */
    static TeleportThread playerToPlayer(Player player, Player targetPlayer) {
        return new TeleportThreadImpl(player, targetPlayer);
    }
}
