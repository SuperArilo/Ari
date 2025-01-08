package ari.superarilo.enumType;

public enum FunctionType {
    BACK("返回"),
    REBACK("返回"),
    ICON("图标"),
    LOCATION("位置"),
    RENAME("重命名"),
    DELETE("删除");
    private final String name;

    FunctionType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
