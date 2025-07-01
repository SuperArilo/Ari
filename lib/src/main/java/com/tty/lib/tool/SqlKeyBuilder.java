package com.tty.lib.tool;

import com.tty.lib.dto.SqlKey;
import com.tty.lib.dto.SqlOrderByKey;

import java.util.ArrayList;

public class SqlKeyBuilder {

    public static SqlKeyList build(String sqlKey, String valueKey, String conjunction, Object value) {
        return new SqlKeyList().build(sqlKey, valueKey, conjunction, value);
    }

    public static class SqlKeyList extends ArrayList<SqlKey> {
        public SqlKeyList build(String sqlKey, String valueKey, String conjunction, Object value) {
            this.add(new SqlKey(sqlKey, valueKey, conjunction, value));
            return this;
        }
    }

    public static SqlOrderByKeyList build(String orderBy, String slotMethod) {
        return new SqlOrderByKeyList().build(orderBy, slotMethod);
    }

    public static class SqlOrderByKeyList extends ArrayList<SqlOrderByKey> {
        public SqlOrderByKeyList build(String orderBy, String slotMethod) {
            this.add(new SqlOrderByKey(orderBy, slotMethod));
            return this;
        }
    }
}
