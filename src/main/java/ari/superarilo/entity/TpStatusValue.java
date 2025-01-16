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
    public synchronized void addStatus(TeleportStatus teleportStatus) {
        this.statusList.add(teleportStatus);
    }
    public synchronized void remove(Player player, TeleportType type) {
        this.statusList.removeIf(obj -> obj.getPlayUUID().equals(player.getUniqueId()) && obj.getType().equals(type));
    }
}
