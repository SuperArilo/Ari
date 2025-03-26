package ari.superarilo.entity.sql;

import lombok.Data;

@Data
public class ServerPlayer {
    private Integer id;
    private String playerName;
    private String playerUUID;
    private Long firstLoginTime;
    private Long lastLoginOffTime;
    private Long totalOnlineTime;
    private String namePrefix;
    private String nameSuffix;
}
