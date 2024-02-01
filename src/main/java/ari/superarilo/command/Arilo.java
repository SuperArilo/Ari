package ari.superarilo.command;

import ari.superarilo.SuperArilo;
import ari.superarilo.enumType.Commands;
import ari.superarilo.tool.ConfigFiles;
import ari.superarilo.tool.TextTool;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class Arilo implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!(command.getName().equalsIgnoreCase(Commands.ARILO.getShow()) || Commands.ARILO.getAliases().contains(s))) return false;
        if (strings.length == 0) return false;
        Commands type;
        try {
            type = Commands.valueOf(strings[0].toUpperCase(Locale.ROOT));
        } catch (Exception e) {
            type = Commands.NONE;
        }
        switch (type) {
            case RELOAD:
                commandSender.sendMessage(TextTool.setHEXColorText(ConfigFiles.configs.get("lang").getString("command.reload.doing","null")));
                ConfigFiles.reloadAllConfig();
                commandSender.sendMessage(TextTool.setHEXColorText(ConfigFiles.configs.get("lang").getString("command.reload.success","null")));
                break;
            case TPA:
                //判断是否有对应的权限
                if(commandSender.hasPermission(type.getPermission())) {
                    if (strings.length < 2) {
                        commandSender.sendMessage(TextTool.setHEXColorText(ConfigFiles.configs.get("lang").getString("command.tpa.fail", "null")));
                        return true;
                    }
                    Player bePlayer = SuperArilo.instance.getServer().getPlayer(strings[1]);
                    if(bePlayer == null) {
                        commandSender.sendMessage(TextTool.setHEXColorText(ConfigFiles.configs.get("lang").getString("command.tpa.unable-player", "null")));
                    } else {
                        SuperArilo.logger.info(bePlayer.getName());
                    }
                } else {
                    commandSender.sendMessage(TextTool.setHEXColorText(ConfigFiles.configs.get("lang").getString("command.tpa.permission-message", type.getPermissionMessage())));
                }
                break;
            case TPAHERE:

                break;

        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(command.getName().equalsIgnoreCase(Commands.ARILO.getShow()) || Commands.ARILO.getAliases().contains(s)) {
            if (strings[0].isEmpty()) return Commands.ARILO.getTabCompleteList();
            Commands commands;
            try {
                commands = Commands.valueOf(strings[0].toUpperCase(Locale.ROOT));
            } catch (Exception e) {
                commands = Commands.NONE;
            }
            switch (commands) {
                case TPA: {
                    List<String> players = new ArrayList<>();
                    SuperArilo.instance.getServer().getOnlinePlayers().forEach(e -> {
                        if(commandSender.getName().equals(e.getName())) return;
                        players.add(e.getName());
                    });
                    return players;
                }
                case RELOAD:
                    return Arrays.asList("114", "514");
            }
            SuperArilo.logger.warning(Arrays.toString(strings));
        }

        return null;
    }
}
