package ari.superarilo.command.teleport;

import ari.superarilo.Ari;
import ari.superarilo.command.tool.CommandCheck;
import ari.superarilo.command.tool.impl.CommandCheckImpl;
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

public class TpaRefuse implements TabExecutor {
    
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        CommandCheckImpl check = CommandCheck.create();
        if (!check.isTheInstructionCorrect(command, AriCommand.TPAREFUSE)) return false;
        if (check.allCheck(commandSender, command, AriCommand.TPAREFUSE)) {
            //指令不全
            if (strings.length != 1 || strings[0].equals(commandSender.getName())) {
                commandSender.sendMessage(TextTool.setHEXColorText(Ari.instance.configManager.getValue("command.public.fail", FilePath.Lang, String.class)));
                return true;
            }

            Player player = Ari.instance.getServer().getPlayerExact(strings[0]);
            if (player == null) {
                commandSender.sendMessage(TextTool.setHEXColorText(Ari.instance.configManager.getValue("teleport.unable-player", FilePath.Lang, String.class)));
                return true;
            }

            TeleportStatus status = TeleportPrecondition.create().checkStatusV(player, (Player) commandSender);
            if (status == null) {
                commandSender.sendMessage(TextTool.setHEXColorText(Ari.instance.configManager.getValue("command.tparefuse.been-done", FilePath.Lang, String.class)));
                return true;
            } else {
                if (Ari.instance.tpStatusValue.getStatusList().removeIf(obj -> obj.getPlayUUID().equals(player.getUniqueId()) && obj.getType().equals(TeleportType.PLAYER))) {
                    commandSender.sendMessage(TextTool.setHEXColorText(Ari.instance.configManager.getValue("command.tparefuse.success", FilePath.Lang, String.class)));
                    if(Ari.instance.configManager.getValue("command.tparefuse.get-message", FilePath.Lang, String.class) instanceof String message) {
                        player.sendMessage(TextTool.setHEXColorText(message.replace("[TpaBeSender]", commandSender.getName())));
                    }
                } else {
                    commandSender.sendMessage(TextTool.setHEXColorText(Ari.instance.configManager.getValue("teleport.break", FilePath.Lang, String.class)));
                }
            }
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!command.getName().equalsIgnoreCase(AriCommand.TPAREFUSE.getShow())) return List.of("");
        if(commandSender instanceof Player && commandSender.hasPermission(AriCommand.TPAREFUSE.getPermission())) {
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

        return List.of("");
    }
}
