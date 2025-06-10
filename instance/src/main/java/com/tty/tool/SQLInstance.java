package com.tty.tool;


import com.tty.Ari;
import com.tty.lib.enum_type.SQLType;
import com.tty.sql.Table;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.configuration.file.FileConfiguration;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class SQLInstance {

    private final FileConfiguration config;
    public static SQLType sqlType;
    public static Sql2o SESSION_FACTORY;

    public SQLInstance()  {
        this.config = Ari.instance.getConfig();
        this.start();
    }

    private void start() {
        Log.debug(Level.INFO, "Start connecting");
        try {
            sqlType = SQLType.valueOf(config.getString("data.storage-type", "null").toUpperCase());
        } catch (Exception e) {
            Log.warning("storage-type is null, Running sqlite mode");
            sqlType = SQLType.SQLITE;
        }
        Log.debug(Level.INFO, "The database type is " + sqlType.getType());
        switch (sqlType) {
            case MYSQL -> this.createMysql();
            case SQLITE -> this.createSQLite();
        }

        try (Connection connection = SESSION_FACTORY.open()) {
            connection.createQuery(Table.players).executeUpdate();
            connection.createQuery(Table.playerHomes).executeUpdate();
            connection.createQuery(Table.warps).executeUpdate();
        }

    }
    public void reconnect() {
        Log.debug(Level.INFO, "Connection is closing...");
        SQLInstance.SESSION_FACTORY = null;
        Log.debug(Level.INFO, "Connection closed successfully");
        this.start();
    }
    protected void createMysql() {
        HikariDataSource hikariDataSource = new HikariDataSource();
        hikariDataSource.setDriverClassName(sqlType.getDriver());
        hikariDataSource.setJdbcUrl("jdbc:mysql://" + config.getString("data.address") + ":" + config.getString("data.port") +  "/" + config.getString("data.database") + "?useUnicode=true&character_set_server=utf8mb4");
        hikariDataSource.setUsername(config.getString("data.username"));
        hikariDataSource.setPassword(config.getString("data.password"));
        hikariDataSource.setMaximumPoolSize(config.getInt("data.maximum-pool-size"));
        hikariDataSource.setMinimumIdle(config.getInt("data.minimum-idle"));
        hikariDataSource.setMaxLifetime(config.getInt("data.connection-timeout"));
        hikariDataSource.setKeepaliveTime(config.getLong("data.keepalive-time"));
        setLiteFactory(hikariDataSource);
    }
    protected void createSQLite() {
        HikariDataSource hikariDataSource = new HikariDataSource();
        hikariDataSource.setDriverClassName(sqlType.getDriver());
        hikariDataSource.setJdbcUrl("jdbc:sqlite:" + Ari.instance.getDataFolder().getAbsolutePath() + "/" + "AriDB.db");
        setLiteFactory(hikariDataSource);
    }

    protected void setLiteFactory(HikariDataSource dataSource) {
        SESSION_FACTORY = new Sql2o(dataSource);
        Map<String, String> colMaps = new HashMap<>();
        colMaps.put("player_name", "playerName");
        colMaps.put("player_uuid", "playerUUID");
        colMaps.put("first_login_time", "firstLoginTime");
        colMaps.put("last_login_off_time", "lastLoginOffTime");
        colMaps.put("total_online_time", "totalOnlineTime");
        colMaps.put("name_prefix", "namePrefix");
        colMaps.put("name_suffix", "nameSuffix");
        colMaps.put("home_id", "homeId");
        colMaps.put("home_name", "homeName");
        colMaps.put("show_material", "showMaterial");
        colMaps.put("warp_id", "warpId");
        colMaps.put("warp_name", "warpName");
        colMaps.put("create_by", "createBy");
        SESSION_FACTORY.setDefaultColumnMappings(colMaps);
    }

    public static String getTablePrefix() {
        return Ari.instance.getConfig().getString("data.table-prefix", "ari");
    }

}
