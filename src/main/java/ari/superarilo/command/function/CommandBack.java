package ari.superarilo.command.function;

import ari.superarilo.Ari;
import ari.superarilo.enumType.FilePath;
import ari.superarilo.function.TeleportCheck;
import ari.superarilo.function.TeleportThread;
import ari.superarilo.function.impl.TeleportThreadImpl;
import ari.superarilo.tool.TextTool;
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
        Location beforeLocation = TeleportThreadImpl.lastLocation.get(player.getUniqueId());
        if(beforeLocation == null) {
            player.sendMessage(TextTool.setHEXColorText("teleport.none-location", FilePath.Lang));
            return;
        }
       if(TeleportCheck.create().preCheckStatus(player, beforeLocation)) {
           TeleportThread.playerToLocation(player, beforeLocation) .teleport(Ari.instance.configManager.getValue("main.teleport.delay", FilePath.TPA, Integer.class));
       }
    }

}
