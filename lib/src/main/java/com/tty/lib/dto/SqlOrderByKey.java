package com.tty.lib.dto;

import lombok.Data;

@Data
public class SqlOrderByKey {

    private String orderBy;
    private String slotMethod;

    public SqlOrderByKey(String orderBy, String slotMethod) {
        this.orderBy = orderBy;
        this.slotMethod = slotMethod;
    }
}
