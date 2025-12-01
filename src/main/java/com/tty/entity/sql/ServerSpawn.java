package com.tty.entity.sql;

import lombok.Data;

@Data
public class ServerSpawn {
    private int id;
    private String spawnId;
    private String spawnName;
    private String world;
    private String location;
    private String showMaterial;
    private String createBy;
    private long createTime;
    private String permission = "";
    private boolean topSlot = false;
}
