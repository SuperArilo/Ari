package ari.superarilo.enumType;

public enum FunctionType {
    BACK("返回"),
    REBACK("返回"),
    ICON("图标"),
    MASKICON("遮罩图标"),
    LOCATION("位置"),
    DATA("数据"),
    RENAME("重命名"),
    SAVE("保存"),
    DELETE("删除"),
    CANCEL("取消"),
    CONFIRM("确认");
    private final String name;

    FunctionType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
