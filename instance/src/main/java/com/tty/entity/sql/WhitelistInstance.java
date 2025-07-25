package com.tty.entity.sql;

import lombok.Data;

@Data
public class WhitelistInstance {

    private long id;
    private String playerUUID;
    private String operator;
    private double addTime;

}
