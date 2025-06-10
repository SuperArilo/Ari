package com.tty.entity.menu;

import lombok.Data;

import java.util.Map;

@Data
public class BaseMenu {
    private String title;
    private Integer row;
    private Mask mask;
    private Map<String, FunctionItems> functionItems;
}
