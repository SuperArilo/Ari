package ari.superarilo.command.teleport;

import ari.superarilo.Ari;
import ari.superarilo.function.CommandCheck;
import ari.superarilo.function.impl.CommandCheckImpl;
import ari.superarilo.entity.TeleportStatus;
import ari.superarilo.enumType.AriCommand;
import ari.superarilo.enumType.FilePath;
import ari.superarilo.enumType.TeleportType;
import ari.superarilo.function.TeleportPrecondition;
import ari.superarilo.function.TeleportThread;
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


public class TpaAccept implements TabExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        CommandCheckImpl check = CommandCheck.create();
        if (!check.isTheInstructionCorrect(command, AriCommand.TPAACCEPT)) return false;
        if (check.allCheck(commandSender, AriCommand.TPAACCEPT)) {
            if (strings.length != 1 || strings[0].equals(commandSender.getName())) {
                commandSender.sendMessage(TextTool.setHEXColorText(Ari.instance.configManager.getValue("command.public.fail", FilePath.Lang, String.class)));
                return true;
            }
            Player player = Ari.instance.getServer().getPlayerExact(strings[0]);
            //判断玩家是否存在
            if (player == null) {
                commandSender.sendMessage(TextTool.setHEXColorText(Ari.instance.configManager.getValue("teleport.unable-player", FilePath.Lang, String.class)));
                return true;
            }
            //判断请求是否还存在

            TeleportStatus status = TeleportPrecondition.create().checkStatusV(player, (Player) commandSender);

            if(status == null) {
                commandSender.sendMessage(TextTool.setHEXColorText(Ari.instance.configManager.getValue("command.tpaaccept.been-done", FilePath.Lang, String.class)));
                return true;
            }
            //请求成功，移除该请求
            commandSender.sendMessage(TextTool.setHEXColorText(Ari.instance.configManager.getValue("command.tpaaccept.agree", FilePath.Lang, String.class)));
            Ari.instance.tpStatusValue.remove(player, TeleportType.PLAYER);

            TeleportThread teleportThread = switch (status.getCommandType()) {
                case TPA -> TeleportThread.playerToPlayer(player, ((Player) commandSender));
                case TPAHERE -> TeleportThread.playerToPlayer(((Player) commandSender), player);
                default -> null;
            };
            if (teleportThread != null) {
                teleportThread.teleport(Ari.instance.configManager.getValue("main.teleport.delay", FilePath.TPA, Integer.class));
            } else {
                commandSender.sendMessage(TextTool.setHEXColorText("command.tpaaccept.error"));
            }
        }

        return true;
    }
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (commandSender instanceof Player && Ari.instance.permissionUtils.hasPermission(commandSender, AriCommand.TPAACCEPT.getPermission())) {
            List<String> i = new ArrayList<>();
            Server tempServer = Ari.instance.getServer();
            Ari.instance.tpStatusValue.getStatusList().stream().filter(obj ->
                    obj.getBePlayerUUID().equals(((Player) commandSender).getUniqueId()) && obj.getType().equals(TeleportType.PLAYER))
                    .forEach(e -> {
                        Player p = tempServer.getPlayer(e.getPlayUUID());
                        if (p != null) {
                            i.add(p.getName());
                        }
                    });
            return i;
        }
        return List.of("");
    }
}
