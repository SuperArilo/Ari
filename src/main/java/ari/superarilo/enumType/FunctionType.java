package ari.superarilo.enumType;

public enum FunctionType {
    BACK("返回");
    private final String name;

    FunctionType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
