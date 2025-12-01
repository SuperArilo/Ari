package com.tty.entity.sql;

import lombok.Data;

@Data
public class ServerPlayer {
    private Integer id;
    private String playerName;
    private String playerUUID;
    private Long firstLoginTime = 0L;
    private Long lastLoginOffTime = 0L;
    private Long totalOnlineTime = 0L;
    private String namePrefix = "";
    private String nameSuffix = "";
}
