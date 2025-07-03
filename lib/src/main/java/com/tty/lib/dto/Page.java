package com.tty.lib.dto;

import lombok.Getter;

@Getter
public class Page {

    private final int limit;
    private final int offset;

    private Page(int pageNum, int pageSize) {
        this.limit = pageSize;
        this.offset = (pageNum - 1) * pageSize;
    }

    public static Page create(int pageNum, int pageSize) {
        return new Page(pageNum, pageSize);
    }

}
