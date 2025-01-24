package ari.superarilo.command.lists;

import ari.superarilo.Ari;
import ari.superarilo.enumType.TeleportObjectType;
import ari.superarilo.function.CommandCheck;
import ari.superarilo.function.impl.CommandCheckImpl;
import ari.superarilo.entity.TeleportStatus;
import ari.superarilo.enumType.AriCommand;
import ari.superarilo.enumType.FilePath;
import ari.superarilo.enumType.TeleportType;
import ari.superarilo.function.TeleportPrecondition;
import ari.superarilo.tool.TextTool;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class tparefuse implements TabExecutor {
    
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        CommandCheckImpl check = CommandCheck.create(commandSender, command, AriCommand.TPAREFUSE);
        if (!check.isTheInstructionCorrect()) return false;
        if (check.allCheck()) {
            //指令不全
            if (strings.length != 1 || strings[0].equals(commandSender.getName())) {
                commandSender.sendMessage(TextTool.setHEXColorText("command.public.fail", FilePath.Lang));
                return true;
            }

            Player player = Ari.instance.getServer().getPlayerExact(strings[0]);
            if (player == null) {
                commandSender.sendMessage(TextTool.setHEXColorText("teleport.unable-player", FilePath.Lang));
                return true;
            }

            TeleportStatus status = TeleportPrecondition.create().checkStatusV(player, (Player) commandSender);
            if (status == null) {
                commandSender.sendMessage(TextTool.setHEXColorText("command.tparefuse.been-done", FilePath.Lang));
                return true;
            } else {
                if (Ari.instance.tpStatusValue.getStatusList().removeIf(obj -> obj.getPlayUUID().equals(player.getUniqueId()) && obj.getType().equals(TeleportType.PLAYER))) {
                    commandSender.sendMessage(TextTool.setHEXColorText("command.tparefuse.success", FilePath.Lang));
                    if(Ari.instance.configManager.getValue("command.tparefuse.get-message", FilePath.Lang, String.class) instanceof String message) {
                        player.sendMessage(TextTool.setHEXColorText(message.replace(TeleportObjectType.TPABESENDER.getType(), commandSender.getName())));
                    }
                } else {
                    commandSender.sendMessage(TextTool.setHEXColorText("teleport.break", FilePath.Lang));
                }
            }
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!command.getName().equalsIgnoreCase(AriCommand.TPAREFUSE.getShow())) return List.of();
        if(commandSender instanceof Player && Ari.instance.permissionUtils.hasPermission(commandSender, AriCommand.TPAREFUSE.getPermission())) {
            List<String> players = new ArrayList<>();
            Server server = Ari.instance.getServer();
            Ari.instance.tpStatusValue.getStatusList().stream().filter(obj ->
                    obj.getBePlayerUUID().equals(((Player) commandSender).getUniqueId()) && obj.getType().equals(TeleportType.PLAYER))
                    .forEach(e -> {
                        Player p = server.getPlayer(e.getPlayUUID());
                        if (p != null) {
                            players.add(p.getName());
                        }
                    });
            return players;
        }

        return List.of();
    }
}
