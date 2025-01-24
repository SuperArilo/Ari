package ari.superarilo.command.lists;

import ari.superarilo.Ari;
import ari.superarilo.enumType.AriCommand;
import ari.superarilo.enumType.FilePath;
import ari.superarilo.function.CommandCheck;
import ari.superarilo.function.TeleportPrecondition;
import ari.superarilo.function.TeleportThread;
import ari.superarilo.function.impl.TeleportThreadImpl;
import ari.superarilo.tool.TextTool;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class back implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        CommandCheck check = CommandCheck.create(commandSender, command, AriCommand.BACK);
        if(!check.isTheInstructionCorrect()) return false;
        if(check.allCheck()) {
            Player player = (Player) commandSender;
            Location beforeLocation = TeleportThreadImpl.lastLocation.get(player.getUniqueId());
            if(beforeLocation == null) {
                commandSender.sendMessage(TextTool.setHEXColorText("teleport.none-location", FilePath.Lang));
                return true;
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
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return List.of();
    }
}
