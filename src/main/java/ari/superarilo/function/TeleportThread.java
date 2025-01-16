package ari.superarilo.function;

import ari.superarilo.function.impl.TeleportThreadImpl;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface TeleportThread {
    void teleport(int delay);
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
