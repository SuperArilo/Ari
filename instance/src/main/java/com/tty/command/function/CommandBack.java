package com.tty.command.function;

import com.tty.Ari;
import com.tty.command.check.TeleportCheck;
import com.tty.enumType.FilePath;
import com.tty.tool.ConfigUtils;
import com.tty.function.Teleport;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static com.tty.listener.teleport.RecordLastLocationListener.TELEPORT_LAST_LOCATION;

public class CommandBack {

    private final CommandSender sender;

    public CommandBack(CommandSender sender) {
        this.sender = sender;
    }

    public void startDo() {
        Player player = (Player) this.sender;
        Location beforeLocation = TELEPORT_LAST_LOCATION.get(player);
        if(beforeLocation == null) {
            player.sendMessage(ConfigUtils.t("teleport.none-location"));
            return;
        }
       if(TeleportCheck.preCheckStatus(player, beforeLocation, 60L) || this.sender.isOp()) {
           Teleport.create(
                   player,
                   beforeLocation,
                           Ari.C_INSTANCE.getValue("main.teleport.delay", FilePath.TPA, Integer.class, 3))
                   .teleport();
       }
    }
}
