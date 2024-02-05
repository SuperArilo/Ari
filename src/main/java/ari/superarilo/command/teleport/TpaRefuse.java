package ari.superarilo.command.teleport;

import ari.superarilo.SuperArilo;
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

public class TpaRefuse implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!command.getName().equalsIgnoreCase(Commands.TPAREFUSE.getShow())) return false;
        if(!(commandSender instanceof Player)) {
            commandSender.sendMessage(TextTool.setHEXColorText(ConfigFiles.configs.get("lang").getString("command.tparefuse.not-player","null")));
            return true;
        }
        if(!commandSender.hasPermission(Commands.TPAREFUSE.getPermission())) {
            commandSender.sendMessage(TextTool.setHEXColorText(ConfigFiles.configs.get("lang").getString("command.tparefuse.permission-message", "null")));
            return true;
        }
        //指令不全
        if (strings.length != 1 || strings[0].equals(commandSender.getName())) {
            commandSender.sendMessage(TextTool.setHEXColorText(ConfigFiles.configs.get("lang").getString("command.tparefuse.fail", "null")));
            return true;
        }
        Player player = SuperArilo.instance.getServer().getPlayerExact(strings[0]);
        if (player == null) {
            commandSender.sendMessage(TextTool.setHEXColorText(ConfigFiles.configs.get("lang").getString("command.tparefuse.unable-player", "null")));
            return true;
        }
        //判断请求是否还存在
        if(SuperArilo.getTeleportStatusList().stream().noneMatch(obj -> obj.getPlayUUID().equals(player.getUniqueId()) && obj.getType().equals(TeleportThread.Type.PLAYER))) {
            commandSender.sendMessage(TextTool.setHEXColorText(ConfigFiles.configs.get("lang").getString("command.tparefuse.been-done", "null")));
            return true;
        }

        if (SuperArilo.getTeleportStatusList().removeIf(obj -> obj.getPlayUUID().equals(player.getUniqueId()) && obj.getType().equals(TeleportThread.Type.PLAYER))) {
            commandSender.sendMessage(TextTool.setHEXColorText(ConfigFiles.configs.get("lang").getString("command.tparefuse.success", "null")));
            player.sendMessage(TextTool.setHEXColorText(ConfigFiles.configs.get("lang").getString("command.tparefuse.get-message", "null").replace("[TpaBeSender]", commandSender.getName())));
        } else {
            commandSender.sendMessage(TextTool.setHEXColorText(ConfigFiles.configs.get("lang").getString("command.tparefuse.break", "null")));
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!command.getName().equalsIgnoreCase(Commands.TPAREFUSE.getShow())) return List.of("");
        if(commandSender instanceof Player && commandSender.hasPermission(Commands.TPAREFUSE.getPermission())) {
            List<String> players = new ArrayList<>();
            Server server = SuperArilo.instance.getServer();
            SuperArilo.getTeleportStatusList().stream().filter(obj ->
                    obj.getBePlayerUUID().equals(((Player) commandSender).getUniqueId()) && obj.getType().equals(TeleportThread.Type.PLAYER))
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
