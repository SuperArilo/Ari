package com.tty.sql;

import com.tty.lib.enum_type.SQLType;
import com.tty.tool.SQLInstance;

public class Table {

    public static String players =
            """
                CREATE TABLE IF NOT EXISTS %splayers (
                id INTEGER PRIMARY KEY %s,
                player_name varchar(64) NOT NULL,
                player_uuid varchar(128) NOT NULL,
                first_login_time INTEGER NULL DEFAULT 0,
                last_login_off_time INTEGER NULL DEFAULT 0,
                total_online_time INTEGER NULL DEFAULT 0,
                name_prefix varchar(128) DEFAULT NULL,
                name_suffix varchar(128) DEFAULT NULL);
            """.formatted(
                    SQLInstance.getTablePrefix(),
            SQLInstance.sqlType.equals(SQLType.MYSQL) ? "AUTO_INCREMENT":"AUTOINCREMENT");

    public static String playerHomes =
            """
                CREATE TABLE IF NOT EXISTS %splayer_home (
                id INTEGER PRIMARY KEY %s,
                home_id VARCHAR(128) NOT NULL,
                home_name VARCHAR(128) NOT NULL,
                player_uuid VARCHAR(128) NOT NULL,
                location VARCHAR(128) NOT NULL,
                show_material VARCHAR(128) NOT NULL);
            """.formatted(
                    SQLInstance.getTablePrefix(),
                    SQLInstance.sqlType.equals(SQLType.MYSQL) ? "AUTO_INCREMENT":"AUTOINCREMENT");

    public static String warps =
            """
                CREATE TABLE IF NOT EXISTS %swarps (
                id INTEGER PRIMARY KEY %s,
                warp_id VARCHAR(128) NOT NULL,
                warp_name VARCHAR(128) NOT NULL,
                create_by VARCHAR(128) NOT NULL,
                location VARCHAR(128) NOT NULL,
                show_material VARCHAR(128) NOT NULL,
                permission VARCHAR(128) default NULL,
                cost INTEGER default 0);
            """.formatted(
                    SQLInstance.getTablePrefix(),
                    SQLInstance.sqlType.equals(SQLType.MYSQL) ? "AUTO_INCREMENT":"AUTOINCREMENT");
}
