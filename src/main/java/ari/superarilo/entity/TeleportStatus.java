package ari.superarilo.entity;

import ari.superarilo.enumType.AriCommand;
import ari.superarilo.tool.TeleportThread;

import java.util.UUID;

public class TeleportStatus {

    //传送发起者
    private UUID playUUID;
    //被接收者
    private UUID bePlayerUUID;
    private TeleportThread.Type type;
    private AriCommand commandType;

    public UUID getPlayUUID() {
        return playUUID;
    }

    public void setPlayUUID(UUID playUUID) {
        this.playUUID = playUUID;
    }

    public TeleportThread.Type getType() {
        return type;
    }

    public void setType(TeleportThread.Type type) {
        this.type = type;
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
}
