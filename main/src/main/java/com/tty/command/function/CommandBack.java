package com.tty.command.function;

import com.tty.Ari;
import com.tty.enumType.FilePath;
import com.tty.function.TeleportCheck;
import com.tty.function.TeleportThread;
import com.tty.tool.TextTool;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandBack {

    private final CommandSender sender;

    public CommandBack(CommandSender sender) {
        this.sender = sender;
    }

    public void startDo() {
        Player player = (Player) this.sender;
        Location beforeLocation = TeleportThread.lastLocation.get(player.getUniqueId());
        if(beforeLocation == null) {
            player.sendMessage(TextTool.setHEXColorText("teleport.none-location", FilePath.Lang));
            return;
        }
       if(TeleportCheck.create().preCheckStatus(player, beforeLocation)) {
           com.tty.function.TeleportThread.playerToLocation(player, beforeLocation) .teleport(Ari.instance.configManager.getValue("main.teleport.delay", FilePath.TPA, Integer.class));
       }
    }

}
