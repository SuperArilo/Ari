package ari.superarilo.tool;


import ari.superarilo.Ari;
import ari.superarilo.enumType.MapperList;
import ari.superarilo.enumType.SQLType;
import ari.superarilo.enumType.sql.CreateTableSql;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import static ari.superarilo.Ari.logger;

public class SQLInstance {
    private final Ari instance;
    private final FileConfiguration config;
    private SQLType sqlType;
    public static SqlSessionFactory sessionFactory;

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

        try(SqlSession sqlSession = sessionFactory.openSession()) {
            Connection connection = sqlSession.getConnection();
            try(Statement statement = connection.createStatement()) {
                for (CreateTableSql tableSql : CreateTableSql.values()) {
                    logger.log(Level.FINE, "creating table " + tableSql.getTableName());
                    statement.execute(tableSql.getSql());
                    logger.log(Level.FINE, "created table " + tableSql.getTableName());
                }
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "create table error", e);
            }
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
        Configuration configuration = new Configuration(new Environment("development", new JdbcTransactionFactory(), hikariDataSource));
        this.getMapperClasses().forEach(configuration::addMapper);
        configuration.addMappers("ari.superarilo.mapper");

        sessionFactory = new SqlSessionFactoryBuilder().build(configuration);
    }
    protected void createSQLite() {
        HikariDataSource hikariDataSource = new HikariDataSource();
        hikariDataSource.setDriverClassName(sqlType.getDriver());
        hikariDataSource.setJdbcUrl("jdbc:sqlite:" + this.instance.getDataFolder().getAbsolutePath() + "/" + "AriDB.db");
        sessionFactory = new SqlSessionFactoryBuilder().build(new Configuration(new Environment("development", new JdbcTransactionFactory(), hikariDataSource)));
    }
    protected List<Class<?>> getMapperClasses() {
        List<Class<?>> classList = new ArrayList<>();
        for (MapperList mapperList:MapperList.values()) {
            classList.add(mapperList.getClazz());
        }
        return classList;
    }
}
