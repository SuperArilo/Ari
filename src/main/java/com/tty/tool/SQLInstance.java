package com.tty.tool;

import com.tty.Ari;
import com.tty.enumType.SqlTable;
import com.tty.lib.Log;
import com.tty.lib.enum_type.SQLType;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.configuration.file.FileConfiguration;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class SQLInstance {

    public static SQLType sqlType;
    public static Sql2o SESSION_FACTORY;

    public void start() {
        Log.debug("Start connecting");
        try {
            sqlType = SQLType.valueOf(Ari.instance.getConfig().getString("data.storage-type", "null").toUpperCase());
        } catch (Exception e) {
            Log.warn("storage-type is null, Running sqlite mode");
            sqlType = SQLType.SQLITE;
        }
        Log.debug("The database type is %s", sqlType.getType());
        switch (sqlType) {
            case MYSQL -> this.createMysql();
            case SQLITE -> this.createSQLite();
        }

        try (Connection connection = SESSION_FACTORY.open()) {
            for (SqlTable value : SqlTable.values()) {
                connection.createQuery(value.getSql()).executeUpdate();
            }
        } catch (Exception e) {
            Log.error(e, "sql error");
        }

    }
    public void reconnect() {
        Log.debug("Connection is closing...");
        close();
        this.start();
    }
    protected void createMysql() {
        FileConfiguration config = Ari.instance.getConfig();
        HikariDataSource hikariDataSource = new HikariDataSource();
        hikariDataSource.setDriverClassName(sqlType.getDriver());
        hikariDataSource.setJdbcUrl("jdbc:mysql://" + config.getString("data.address") + ":" + config.getString("data.port") +  "/" + config.getString("data.database") + "?useUnicode=true&character_set_server=utf8mb4");
        hikariDataSource.setUsername(config.getString("data.username"));
        hikariDataSource.setPassword(config.getString("data.password"));
        hikariDataSource.setMaximumPoolSize(config.getInt("data.maximum-pool-size"));
        hikariDataSource.setMinimumIdle(config.getInt("data.minimum-idle"));
        hikariDataSource.setMaxLifetime(config.getInt("data.connection-timeout"));
        hikariDataSource.setKeepaliveTime(config.getLong("data.keepalive-time"));
        this.setLiteFactory(hikariDataSource);
    }
    protected void createSQLite() {
        HikariDataSource hikariDataSource = new HikariDataSource();
        hikariDataSource.setDriverClassName(sqlType.getDriver());
        hikariDataSource.setJdbcUrl("jdbc:sqlite:" + Ari.instance.getDataFolder().getAbsolutePath() + "/" + "AriDB.db");
        this.setLiteFactory(hikariDataSource);
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
        colMaps.put("top_slot", "topSlot");
        colMaps.put("warp_id", "warpId");
        colMaps.put("warp_name", "warpName");
        colMaps.put("create_by", "createBy");
        colMaps.put("create_time", "createTime");
        colMaps.put("spawn_id", "spawnId");
        colMaps.put("spawn_name", "spawnName");
        colMaps.put("add_time", "addTime");
        SESSION_FACTORY.setDefaultColumnMappings(colMaps);
    }

    public static String getTablePrefix() {
        return Ari.instance.getConfig().getString("data.table-prefix", "ari");
    }

    public static void close() {
        try {
            if (SQLInstance.SESSION_FACTORY != null) {
                SQLInstance.SESSION_FACTORY.getConnectionSource().getConnection().close();
                Log.debug("Connection closed successfully");
            }
        } catch (SQLException e) {
            Log.error(e, "close sql connection error");
        }
        SQLInstance.SESSION_FACTORY = null;
    }

}
