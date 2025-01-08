package ari.superarilo.enumType.sql;

public enum CreateTableSql {
    PLAYERHOME(
            "player_home",
            """
                    CREATE TABLE IF NOT EXISTS player_home (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        home_id VARCHAR(128) NOT NULL,
                        home_name VARCHAR(128) NOT NULL,
                        player_uuid VARCHAR(128) NOT NULL,
                        x DECIMAL(10, 3) NOT NULL,
                        y DECIMAL(10, 3) NOT NULL,
                        z DECIMAL(10, 3) NOT NULL,
                        world VARCHAR(128) NOT NULL,
                        show_material VARCHAR(128) NOT NULL
                    )""");
    private final String tableName;
    private final String sql;

    CreateTableSql(String tableName, String sql) {
        this.tableName = tableName;
        this.sql = sql;
    }

    public String getSql() {
        return sql;
    }

    public String getTableName() {
        return tableName;
    }
}
