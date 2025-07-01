package com.tty.function;


import com.tty.lib.dto.Page;
import com.tty.lib.dto.SqlKey;
import com.tty.lib.dto.SqlOrderByKey;
import com.tty.tool.SQLInstance;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface BaseManager<T> {

    /**
     * 异步获取保存的列表
     * @param page 分页对象
     * @return 列表数组
     */

    CompletableFuture<List<T>> asyncGetList(Page page, List<SqlKey> sqlKeys, List<SqlOrderByKey> orderByKeys);

    CompletableFuture<T> asyncGetInstance(List<SqlKey> sqlKeys);

    CompletableFuture<Boolean> createInstance(T instance);

    CompletableFuture<Boolean> deleteInstance(T instance);
    /**
     * 修改信息
     * @param instance 被修改的对象
     * @return 修改成功状态。true：成功，false：失败
     */
    CompletableFuture<Boolean> modify(T instance);

    default String buildWhereSql(String start, Page page, List<SqlKey> sqlKeys, List<SqlOrderByKey> orderByKeys) {
        StringBuilder value = new StringBuilder(start.formatted(SQLInstance.getTablePrefix()));
        if (value.charAt(value.length()-1) != ' ') {
            value.append(" ");
        }
        if (sqlKeys != null && !sqlKeys.isEmpty()) {
            StringBuilder builder = new StringBuilder("where ");
            for (SqlKey key : sqlKeys) {
                builder.append(key.getSqlKey())
                        .append("=")
                        .append(":")
                        .append(key.getValueKey())
                        .append(" ")
                        .append(key.getConjunction())
                        .append(" ");
            }
            value.append(builder);
        }
        if (orderByKeys != null && !orderByKeys.isEmpty()) {
            StringBuilder order = new StringBuilder("order by ");
            for (int i = 0;i < orderByKeys.size();i++) {
                SqlOrderByKey key = orderByKeys.get(i);
                order.append(key.getOrderBy())
                        .append(" ")
                        .append(key.getSlotMethod())
                        .append(" ");
                if (i < orderByKeys.size() - 1) {
                    order.append(", ");
                }
            }
            value.append(order);
        }
        if (page != null) {
            value.append("LIMIT %s OFFSET %s".formatted(page.getLimit(), page.getOffset())).append(" ");
        }
        return value.toString();
    }
}
