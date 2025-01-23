package ari.superarilo.entity;

import ari.superarilo.enumType.TeleportType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TpStatusValue {
    private final List<TeleportStatus> statusList = new ArrayList<>();

    public List<TeleportStatus> getStatusList() {
        return this.statusList;
    }

    /**
     * 添加指定玩家的传送状态
     * @param teleportStatus 传送状态
     */
    public synchronized void addStatus(TeleportStatus teleportStatus) {
        this.statusList.add(teleportStatus);
    }

    /**
     * 移除指定玩家已经保存的传送状态
     * @param player 玩家
     * @param type 传送类型
     */
    public synchronized void remove(Player player, TeleportType type) {
        this.statusList.removeIf(obj -> obj.getPlayUUID().equals(player.getUniqueId()) && obj.getType().equals(type));
    }
}
