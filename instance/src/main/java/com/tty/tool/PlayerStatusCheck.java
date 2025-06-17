package com.tty.tool;

import com.tty.enumType.FilePath;
import org.bukkit.entity.Player;

public class PlayerStatusCheck {

    public static boolean playerStatusCheck(Player player) {
        if (player.isSleeping() || player.isDeeplySleeping() || player.isFlying()) {
            player.sendMessage(TextTool.setHEXColorText("base.status-not-allow", FilePath.Lang));
            return false;
        }
        return true;
    }

}
