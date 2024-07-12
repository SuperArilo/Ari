package ari.superarilo.tool;


import ari.superarilo.Ari;
import ari.superarilo.enumType.SQLType;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.logging.Level;

import static ari.superarilo.Ari.logger;

public class SQLInstance {
    private final Ari instance;
    private final FileConfiguration config;
    private SQLType sqlType;
    private static SqlSessionFactory sessionFactory;

    public SQLInstance(Ari instance)  {
        this.instance = instance;
        this.config = instance.getConfig();
        this.start();
    }

    private void start() {
        logger.log(Level.INFO, "Start connecting");
        try {
            sqlType = SQLType.valueOf(config.getString("data.storage-type", "null").toUpperCase());
        } catch (Exception e) {
            this.instance.getLogger().log(Level.WARNING, "storage-type is null, Running sqlite mode");
            sqlType = SQLType.SQLITE;
        }
        logger.log(Level.INFO, "The database type is " + sqlType.getType());
        switch (sqlType) {
            case MYSQL:
                this.createMysql();
                break;
            case SQLITE:
                this.createSQLite();
                break;
        }
    }
    public void reconnect() {
        logger.log(Level.INFO, "Connection is closing...");
        sessionFactory = null;
        logger.log(Level.INFO, "Connection closed successfully");
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
        sessionFactory = new SqlSessionFactoryBuilder().build(new Configuration(new Environment("development", new JdbcTransactionFactory(), hikariDataSource)));
    }
    protected void createSQLite() {
        HikariDataSource hikariDataSource = new HikariDataSource();
        hikariDataSource.setDriverClassName(sqlType.getDriver());
        hikariDataSource.setJdbcUrl("jdbc:sqlite:" + this.instance.getDataFolder().getAbsolutePath() + "/" + "AriDB.db");
        sessionFactory = new SqlSessionFactoryBuilder().build(new Configuration(new Environment("development", new JdbcTransactionFactory(), hikariDataSource)));
    }
}
