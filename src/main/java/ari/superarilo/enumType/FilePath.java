package ari.superarilo.enumType;

public enum FilePath {
    Lang("lang","lang/cn.yml"),
    TPA("tpa", "module/tpa/setting.yml"),
    HomeList("homeList", "module/home/home-gui.yml"),
    HomeEditor("homeEditor", "module/home/edit-home-gui.yml"),
    HomeConfig("homeConfig", "module/home/setting.yml");

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
