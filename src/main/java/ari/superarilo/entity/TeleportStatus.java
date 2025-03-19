package ari.superarilo.entity;

import ari.superarilo.enumType.AriCommand;
import ari.superarilo.enumType.TeleportType;
import lombok.Data;

import java.util.UUID;

@Data
public class TeleportStatus {
    private UUID playUUID;
    private UUID bePlayerUUID;
    private TeleportType type;
    private AriCommand commandType;
}
