package ari.superarilo.entity;

import ari.superarilo.enumType.AriCommand;
import ari.superarilo.enumType.TeleportType;
import org.bukkit.Location;

import java.util.UUID;

public class TeleportStatus {

    //传送发起者
    private UUID playUUID;
    //被接收者
    private UUID bePlayerUUID;
    private Location location;

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    private TeleportType type;
    private AriCommand commandType;

    public UUID getPlayUUID() {
        return playUUID;
    }

    public void setPlayUUID(UUID playUUID) {
        this.playUUID = playUUID;
    }

    public UUID getBePlayerUUID() {
        return bePlayerUUID;
    }

    public void setBePlayerUUID(UUID bePlayerUUID) {
        this.bePlayerUUID = bePlayerUUID;
    }

    public AriCommand getCommandType() {
        return commandType;
    }

    public void setCommandType(AriCommand commandType) {
        this.commandType = commandType;
    }

    public TeleportType getType() {
        return type;
    }

    public void setType(TeleportType type) {
        this.type = type;
    }
}
