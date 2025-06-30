package com.tty.entity.sql;

import lombok.Data;

@Data
public class ServerHome {
    private Long id;
    private String homeId;
    private String homeName;
    private String playerUUID;
    private String location;
    private String showMaterial;
    private boolean topSlot = false;
}
