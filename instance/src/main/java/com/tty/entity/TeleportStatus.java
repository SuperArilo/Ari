package com.tty.entity;

import com.tty.enumType.AriCommand;
import com.tty.lib.enum_type.TeleportType;
import lombok.Data;
import org.bukkit.Location;

import java.util.UUID;

@Data
public class TeleportStatus {
    private UUID playUUID;
    private UUID bePlayerUUID;
    private Location location;
    private TeleportType type;
    private AriCommand ariCommand;


    public static TeleportStatus build(UUID playUUID, UUID bePlayerUUID, TeleportType type, AriCommand ariCommand) {
        TeleportStatus status = new TeleportStatus();
        status.setPlayUUID(playUUID);
        status.setBePlayerUUID(bePlayerUUID);
        status.setType(type);
        status.setAriCommand(ariCommand);
        return status;
    }

    public static TeleportStatus build(UUID playUUID, Location location, TeleportType type, AriCommand ariCommand) {
        TeleportStatus status = new TeleportStatus();
        status.setPlayUUID(playUUID);
        status.setLocation(location);
        status.setType(type);
        status.setAriCommand(ariCommand);
        return status;
    }
}
