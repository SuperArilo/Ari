package com.tty.entity.sql;

import lombok.Data;

@Data
public class ServerSpawn {
    private int id;
    private String world;
    private String location;
    private String createBy;
    private long createTime;
}
