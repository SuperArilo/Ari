package com.tty.tool;


import com.tty.Ari;
import com.tty.enumType.MapperList;
import com.tty.lib.enum_type.SQLType;
import com.tty.mapper.CreateTable;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.*;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class SQLInstance {

    private final FileConfiguration config;
    private SQLType sqlType;
    public static SqlSessionFactory sessionFactory;

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

        try(SqlSession sqlSession = sessionFactory.openSession()) {
            CreateTable mapper = sqlSession.getMapper(CreateTable.class);
            mapper.createPlayers();
            mapper.createHomeList();
            mapper.createWarpList();
        } catch (Exception e) {
            Log.error( "executing sql error", e);
        }

    }
    public void reconnect() {
        Log.debug(Level.INFO, "Connection is closing...");
        sessionFactory = null;
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
        setSessionFactory(hikariDataSource);
    }
    protected void createSQLite() {
        HikariDataSource hikariDataSource = new HikariDataSource();
        hikariDataSource.setDriverClassName(sqlType.getDriver());
        hikariDataSource.setJdbcUrl("jdbc:sqlite:" + Ari.instance.getDataFolder().getAbsolutePath() + "/" + "AriDB.db");
        setSessionFactory(hikariDataSource);
    }
    protected List<Class<?>> getMapperClasses() {
        List<Class<?>> classList = new ArrayList<>();
        for (MapperList mapperList:MapperList.values()) {
            classList.add(mapperList.getClazz());
        }
        return classList;
    }
    protected void setSessionFactory(HikariDataSource dataSource) {
        Configuration configuration = new Configuration(new Environment("development", new JdbcTransactionFactory(), dataSource));
        configuration.getVariables().put("table_prefix", this.config.getString("data.table-prefix", "ari_"));
        configuration.addMappers("com.tty.mapper");
        configuration.setAutoMappingBehavior(AutoMappingBehavior.FULL);
        configuration.setMapUnderscoreToCamelCase(true);
        this.getMapperClasses().forEach(configuration::addMapper);
        configuration.setLogImpl(org.apache.ibatis.logging.slf4j.Slf4jImpl.class);
        sessionFactory = new SqlSessionFactoryBuilder().build(configuration);
    }
}
