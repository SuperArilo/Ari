package ari.superarilo.command.function.impl;

import ari.superarilo.Ari;
import ari.superarilo.command.function.CommandBack;
import ari.superarilo.enumType.AriCommand;
import ari.superarilo.enumType.FilePath;
import ari.superarilo.function.TeleportPrecondition;
import ari.superarilo.function.TeleportThread;
import ari.superarilo.function.impl.TeleportThreadImpl;
import ari.superarilo.tool.TextTool;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandBackImpl implements CommandBack {

    private final CommandSender sender;

    public CommandBackImpl(CommandSender sender) {
        this.sender = sender;
    }

    @Override
    public void startDo() {
        Player player = (Player) this.sender;
        Location beforeLocation = TeleportThreadImpl.lastLocation.get(player.getUniqueId());
        if(beforeLocation == null) {
            player.sendMessage(TextTool.setHEXColorText("teleport.none-location", FilePath.Lang));
            return;
        }
        if(TeleportPrecondition.create().preCheckStatus(player, beforeLocation, AriCommand.BACK)) {
            TeleportThread
                    .playerToLocation(
                            player,
                            beforeLocation)
                    .teleport(Ari.instance.configManager.getValue(
                            "main.teleport.delay",
                            FilePath.TPA,
                            Integer.class));
        }
    }
}
