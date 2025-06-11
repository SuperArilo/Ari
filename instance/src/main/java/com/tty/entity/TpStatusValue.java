package com.tty.entity;

import com.tty.lib.enum_type.TeleportType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TpStatusValue {

    public static final List<TeleportStatus> statusList = new ArrayList<>();

    /**
     * 添加指定玩家的传送状态
     * @param teleportStatus 传送状态
     */
    public static synchronized void addStatus(TeleportStatus teleportStatus) {
        statusList.add(teleportStatus);
    }

    /**
     * 移除指定玩家已经保存的传送状态
     * @param player 玩家
     * @param type 传送类型
     */
    public static synchronized boolean remove(Player player, TeleportType type) {
        return statusList.removeIf(obj -> obj.getPlayUUID().equals(player.getUniqueId()) && obj.getType().equals(type));
    }

}
