package com.tty.enumType;

import com.tty.Ari;
import lombok.Getter;

@Getter
public enum TeleportType {
    WARP("warp"),
    BACK("back"),
    RTP("rtp"),
    HOME("home"),
    TPA("tpa"),
    SPAWN("spawn");

    private final String key;

    TeleportType(String key) {
        this.key = key;
    }

    public static int getCoolDownTime(TeleportType type) {
        return Ari.C_INSTANCE.getValue("teleport." + type.getKey() + ".cooldown", FilePath.FunctionConfig, Integer.class, 10);
    }

    public static int getDelayTime(TeleportType type) {
        return Ari.C_INSTANCE.getValue("teleport." + type.getKey() + ".delay", FilePath.FunctionConfig, Integer.class, 3);
    }
}
