package ari.superarilo.enumType;

public enum FilePath {
    Lang("lang","lang/cn.yml"),
    Commands("commands", "commands.yml");

    private final String name;
    private final String path;

    FilePath(String name, String path) {
        this.name = name;
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public String getName() {
        return name;
    }
}
