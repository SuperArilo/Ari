package com.tty.lib.enum_type;

import lombok.Getter;

@Getter
public enum FunctionType {
    BACK("返回"),
    REBACK("返回"),
    ICON("图标"),
    MASKICON("遮罩图标"),
    LOCATION("位置"),
    TOP_SLOT("置顶"),
    DATA("数据"),
    RENAME("重命名"),
    SAVE("保存"),
    DELETE("删除"),
    CANCEL("取消"),
    CONFIRM("确认"),
    PREV("上一页"),
    NEXT("下一页"),
    PERMISSION("权限"),
    COST("花费");
    private final String name;

    FunctionType(String name) {
        this.name = name;
    }

}
