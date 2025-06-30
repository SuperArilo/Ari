package com.tty.enumType;

import com.tty.lib.enum_type.SQLType;
import com.tty.tool.SQLInstance;

public enum SqlUpdate {

    Update_Home_Top_Slot("""
                ALTER TABLE %splayer_home ADD COLUMN top_slot boolean NOT NULL DEFAULT false;
            """);

    private final String updateSql;

    SqlUpdate(String updateSql) {
        this.updateSql = updateSql;
    }

    public String getUpdateSql() {
        return updateSql.formatted(
                SQLInstance.getTablePrefix(),
                SQLInstance.sqlType.equals(SQLType.MYSQL) ? "AUTO_INCREMENT":"AUTOINCREMENT");
    }
}
