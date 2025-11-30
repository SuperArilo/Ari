package com.tty.lib.enum_type;

import lombok.Getter;

@Getter
public enum TeleportType {
    WARP("warp"),
    BACK("back"),
    RTP("rtp"),
    HOME("home"),
    TPA("tpa"),
    TPAHERE("tpahere"),
    SPAWN("spawn");

    private final String key;

    TeleportType(String key) {
        this.key = key;
    }

}
