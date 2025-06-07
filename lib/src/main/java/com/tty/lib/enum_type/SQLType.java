package com.tty.lib.enum_type;

import lombok.Getter;

@Getter
public enum SQLType {
    MYSQL("mysql", "com.mysql.cj.jdbc.Driver"),
    SQLITE("sqlite", "org.sqlite.JDBC");
    private final String type;
    private final String driver;
    SQLType(String type, String driver) {
        this.type = type;
        this.driver = driver;
    }
}
