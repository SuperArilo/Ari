package com.tty.function;

import com.tty.entity.TeleportStatus;
import com.tty.enumType.AriCommand;
import com.tty.function.impl.TeleportCheckImpl;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface TeleportCheck {
    /**
     * 检查是否已经向目标玩家发送过传送请求
     * @param player 被传送玩家
     * @param targetPlayer 目标玩家
     */
    void preCheckStatus(Player player, Player targetPlayer, AriCommand ariCommand);
    /**
     * 检查被传送玩家是否已经发起过传送请求
     * @param player 被传送玩家
     * @param location 目标位置
     * @param delay 传送冷却
     */
    boolean preCheckStatus(Player player, Location location, long delay);
    /**
     * 查询玩家之间传送的请求
     * @param player 被传送的玩家
     * @param targetPlayer 目标玩家
     * @return 传送请求类
     */
    TeleportStatus checkHaveTeleportStatus(Player player, Player targetPlayer);
    /**
     * 查询玩家定点传送的请求
     * @param player 被传送的玩家
     * @param location 目标地方
     * @return 传送请求类
     */
    TeleportStatus checkHaveTeleportStatus(Player player, Location location);

    static TeleportCheck create() {
        return new TeleportCheckImpl();
    }
}
