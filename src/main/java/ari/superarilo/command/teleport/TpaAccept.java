package ari.superarilo.command.teleport;

import ari.superarilo.SuperArilo;
import ari.superarilo.entity.TeleportStatus;
import ari.superarilo.enumType.Commands;
import ari.superarilo.tool.ConfigFiles;
import ari.superarilo.tool.TeleportThread;
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
        if (!command.getName().equalsIgnoreCase(Commands.TPAACCEPT.getShow())) return false;
        if(!(commandSender instanceof Player)) {
            commandSender.sendMessage(TextTool.setHEXColorText(ConfigFiles.configs.get("lang").getString("command.tpaaccept.not-player", "null")));
            return true;
        }
        if (strings.length != 1 || strings[0].equals(commandSender.getName())) {
            commandSender.sendMessage(TextTool.setHEXColorText(ConfigFiles.configs.get("lang").getString("command.tpaaccept.fail", "null")));
            return true;
        }
        Player player = SuperArilo.instance.getServer().getPlayerExact(strings[0]);
        //判断玩家是否存在
        if (player == null) {
            commandSender.sendMessage(TextTool.setHEXColorText(ConfigFiles.configs.get("lang").getString("command.tpaaccept.unable-player", "null")));
            return true;
        }
        //判断请求是否还存在
        List<TeleportStatus> statusList = SuperArilo.getTeleportStatusList().stream().filter(obj ->
                obj.getPlayUUID().equals(player.getUniqueId())
                        && obj.getBePlayerUUID().equals(((Player) commandSender).getUniqueId())
                        && obj.getType().equals(TeleportThread.Type.PLAYER)).toList();
        TeleportStatus status = statusList.isEmpty() ? null:statusList.get(0);
        if(status == null) {
            commandSender.sendMessage(TextTool.setHEXColorText(ConfigFiles.configs.get("lang").getString("command.tpaaccept.been-done", "null")));
            return true;
        }
        //请求成功，移除该请求
        commandSender.sendMessage(TextTool.setHEXColorText(ConfigFiles.configs.get("lang").getString("command.tpaaccept.agree","null")));
        SuperArilo.deleteAddTeleportStatus(player.getUniqueId(), TeleportThread.Type.PLAYER);
        TeleportThread teleportThread = switch (status.getCommandType()) {
            case TPA -> new TeleportThread(player, ((Player) commandSender), TeleportThread.Type.PLAYER);
            case TPAHERE -> new TeleportThread(((Player) commandSender), player, TeleportThread.Type.PLAYER);
            default -> null;
        };
        if (teleportThread != null) {
            teleportThread.teleport();
        } else {
            commandSender.sendMessage(TextTool.setHEXColorText("command.tpaaccept.error"));
        }
        return true;
    }
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (commandSender instanceof Player) {
            List<String> i = new ArrayList<>();
            Server tempServer = SuperArilo.instance.getServer();
            SuperArilo.getTeleportStatusList().stream().filter(obj ->
                    obj.getBePlayerUUID().equals(((Player) commandSender).getUniqueId()) && obj.getType().equals(TeleportThread.Type.PLAYER))
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
