package ari.superarilo.enumType;

import org.bukkit.entity.Player;

public enum TeleportObjectType {

    TpaSender("TpaSender"),
    TpaHereSender("TpaHereSender");
    private String type;
    private Player player;

    private Player targetPlayer;
    TeleportObjectType(String type, Player player, Player targetPlayer) {
        this.type = type;
        this.player = player;
        this.targetPlayer = targetPlayer;
    }

    TeleportObjectType(String type) {
        this.type = type;
    }


    public Player getPlayer() {
        return player;
    }

    public Player getTargetPlayer() {
        return targetPlayer;
    }
    private String getTypeName() {
        return null;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
