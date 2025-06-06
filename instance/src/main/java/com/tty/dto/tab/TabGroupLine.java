package com.tty.dto.tab;

import lombok.Data;

@Data
public class TabGroupLine {
    private String prefix;
    private String suffix;

    public TabGroupLine(String prefix, String suffix) {
        this.prefix = prefix;
        this.suffix = suffix;
    }
}
