package com.tty.dto;

import com.tty.enumType.TeleportType;
import lombok.Data;
import org.bukkit.Location;

import java.util.UUID;

@Data
public class TeleportStatus {
    private UUID playUUID;
    private UUID bePlayerUUID;
    private Location location;
    private TeleportType type;
    private String commandString;


    public static TeleportStatus build(UUID playUUID, UUID bePlayerUUID, TeleportType type, String commandString) {
        TeleportStatus status = new TeleportStatus();
        status.setPlayUUID(playUUID);
        status.setBePlayerUUID(bePlayerUUID);
        status.setType(type);
        status.setCommandString(commandString);
        return status;
    }

    public static TeleportStatus build(UUID playUUID, Location location, TeleportType type, String commandString) {
        TeleportStatus status = new TeleportStatus();
        status.setPlayUUID(playUUID);
        status.setLocation(location);
        status.setType(type);
        status.setCommandString(commandString);
        return status;
    }
}
