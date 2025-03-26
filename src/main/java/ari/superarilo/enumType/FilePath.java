package ari.superarilo.enumType;

import lombok.Getter;

@Getter
public enum FilePath {
    Lang("lang","lang/[lang].yml"),
    TPA("tpa", "module/tpa/setting.yml"),
    CommandAlias("commandAlias", "module/command-alias.yml"),
    HomeList("homeList", "module/home/home-gui.yml"),
    HomeConfig("homeConfig", "module/home/setting.yml"),
    HomeEditor("homeEditor", "module/home/edit-home-gui.yml"),
    WarpList("warpList", "module/warp/warp-gui.yml"),
    WarpConfig("warpConfig", "module/warp/setting.yml"),
    WarpEditor("warpEditor", "module/warp/edit-warp-gui.yml");

    private final String name;
    private final String path;

    FilePath(String name, String path) {
        this.name = name;
        this.path = path;
    }

}
