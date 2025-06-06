package com.tty.lib.dto;

import lombok.Data;

@Data
public class Page {
    private final int pageNum;
    private final int pageSize;
    //控制每页的记录数
    private final int limit;
    //控制查询从哪一条记录开始
    private final int offset;

    private Page(int pageNum, int pageSize) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.limit = pageSize;
        this.offset = (pageNum - 1) * pageSize;
    }

    public static Page create(int pageNum, int pageSize) {
        return new Page(pageNum, pageSize);
    }

}
