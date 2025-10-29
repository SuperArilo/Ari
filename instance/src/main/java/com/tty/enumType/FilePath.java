package com.tty.enumType;

import lombok.Getter;

@Getter
public enum FilePath {
    Lang("lang/[lang].yml"),
    TPA("module/tpa/setting.yml"),
    CommandAlias("module/command-alias.yml"),
    HomeList("module/home/home-gui.yml"),
    HomeConfig("module/home/setting.yml"),
    HomeEditor("module/home/edit-home-gui.yml"),
    WarpList("module/warp/warp-gui.yml"),
    WarpConfig("module/warp/setting.yml"),
    WarpEditor("module/warp/edit-warp-gui.yml"),
    FunctionConfig("module/function/setting.yml"),
    SpawnConfig("module/spawn/setting.yml");

    private final String path;

    FilePath(String path) {
        this.path = path;
    }

}
