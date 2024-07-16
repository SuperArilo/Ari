package ari.superarilo.enumType.sql;

public enum CreateTableSql {
    PLAYERHOME("player_home","CREATE TABLE IF NOT EXISTS `player_home`(`id` bigint NOT NULL AUTO_INCREMENT,`home_id` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,`home_name` varchar(16) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,`player_uuid` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,`x` decimal(10, 3) NOT NULL,`y` decimal(10, 3) NOT NULL,`z` decimal(10, 3) NOT NULL,`world` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,`show_material` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,PRIMARY KEY (`id`) USING BTREE)ENGINE=InnoDB AUTO_INCREMENT=3 CHARACTER SET=utf8mb3 COLLATE=utf8mb3_general_ci ROW_FORMAT=DYNAMIC");
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
