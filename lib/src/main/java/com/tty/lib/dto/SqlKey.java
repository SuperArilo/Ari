package com.tty.lib.dto;

import lombok.Data;

@Data
public class SqlKey {
    private String sqlKey;
    private String valueKey;
    private String conjunction;
    private Object value;

    public SqlKey(String sqlKey, String valueKey, String conjunction, Object value) {
        this.sqlKey = sqlKey;
        this.valueKey = valueKey;
        this.conjunction = conjunction;
        this.value = value;
    }
}
