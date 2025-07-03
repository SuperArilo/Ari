package com.tty.command.function;

import com.tty.command.check.TeleportCheck;
import com.tty.enumType.FilePath;
import com.tty.tool.ConfigObjectUtils;
import com.tty.function.Teleport;
import com.tty.tool.TextTool;
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
            player.sendMessage(TextTool.setHEXColorText("teleport.none-location", FilePath.Lang));
            return;
        }
       if(TeleportCheck.preCheckStatus(player, beforeLocation, 60L) || this.sender.isOp()) {
           Teleport.create(
                   player,
                   beforeLocation,
                   ConfigObjectUtils.getValue("main.teleport.delay", FilePath.TPA.getName(), Integer.class, 3))
                   .teleport();
       }
    }
}
