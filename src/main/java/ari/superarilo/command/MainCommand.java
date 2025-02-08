package ari.superarilo.command;

import ari.superarilo.Ari;
import ari.superarilo.command.function.CommandBack;
import ari.superarilo.command.function.CommandHome;
import ari.superarilo.command.function.CommandTeleport;
import ari.superarilo.function.CommandCheck;
import ari.superarilo.function.impl.CommandCheckImpl;
import ari.superarilo.enumType.AriCommand;
import ari.superarilo.enumType.FilePath;
import ari.superarilo.tool.TextTool;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class MainCommand implements TabExecutor {


    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        CommandCheckImpl commandCheck = CommandCheck.create(commandSender, command, AriCommand.ARI);
        if (!commandCheck.isTheInstructionCorrect()) return false;
        if (strings.length == 0) return false;

        AriCommand type;
        try {
            type = AriCommand.valueOf(strings[0].toUpperCase(Locale.ROOT));
        } catch (Exception e) {
            commandSender.sendMessage(TextTool.setHEXColorText("command.unknown", FilePath.Lang));
            return true;
        }
        switch (type) {
            case RELOAD -> {
                if(!commandCheck.commandSenderHavePermission(AriCommand.RELOAD)) {
                    return true;
                }
                commandSender.sendMessage(TextTool.setHEXColorText("command.reload.doing", FilePath.Lang));
                Ari.instance.configManager.reloadAllConfig();
                if (Ari.debug) {
                    Ari.instance.SQLInstance.reconnect();
                }
                Ari.instance.commandAlias.reloadAllAlias();
                commandSender.sendMessage(TextTool.setHEXColorText("command.reload.success", FilePath.Lang));
            }
            case TPA -> {
                if(!commandCheck.commandSenderHavePermission(AriCommand.TPA) || strings.length != 2) {
                    return true;
                }
                CommandTeleport.build(commandSender, strings[1]).tpa();
            }
            case TPAACCEPT -> {
                if(!commandCheck.commandSenderHavePermission(AriCommand.TPAACCEPT) || strings.length != 2) {
                    return true;
                }
                CommandTeleport.build(commandSender, strings[1]).tpaaccept();
            }
            case TPAHERE -> {
                if(!commandCheck.commandSenderHavePermission(AriCommand.TPAHERE) || strings.length != 2) {
                    return true;
                }
                CommandTeleport.build(commandSender, strings[1]).tpahere();
            }
            case TPAREFUSE -> {
                if(!commandCheck.commandSenderHavePermission(AriCommand.TPAREFUSE) | strings.length != 2) {
                    return true;
                }
                CommandTeleport.build(commandSender,strings[1]).tparefuse();
            }
            case HOME -> {
                if(!commandCheck.commandSenderHavePermission(AriCommand.HOME)) {
                    return true;
                }
                if(!commandCheck.isPlayer()) break;
                CommandHome.build(commandSender).home();
            }
            case SETHOME -> {
                if(!commandCheck.commandSenderHavePermission(AriCommand.SETHOME)) {
                    return true;
                }
                if(!commandCheck.isPlayer()) break;
                if (strings.length != 2) {
                    commandSender.sendMessage(TextTool.setHEXColorText("command.public.fail", FilePath.Lang));
                    return true;
                }
                CommandHome.build(commandSender).setHome(strings[1]);
            }
            case DELETEHOME -> {
                if(!commandCheck.commandSenderHavePermission(AriCommand.SETHOME)) {
                    return true;
                }
                if(!commandCheck.isPlayer()) break;
                if (strings.length != 2) {
                    commandSender.sendMessage(TextTool.setHEXColorText("command.public.fail", FilePath.Lang));
                    return true;
                }
                CommandHome.build(commandSender).deleteHome(strings[1]);
            }
            case BACK -> {
                if(!commandCheck.commandSenderHavePermission(AriCommand.BACK)) {
                    return true;
                }
                if(!commandCheck.isPlayer()) break;
                CommandBack.build(commandSender).startDo();
            }
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(!(command.getName().equalsIgnoreCase(AriCommand.ARI.getShow()))) return List.of();
        //返回所有具有权限的指令给玩家
        if (strings[0].isEmpty()) {
            List<String> commandList = new ArrayList<>();
            for (AriCommand type : AriCommand.values()) {
                if (type.getShow() == null || type.equals(AriCommand.ARI)) continue;
                if (type.getPermission() == null) {
                    commandList.add(type.getShow());
                    continue;
                }
                if(Ari.instance.permissionUtils.hasPermission(commandSender, type.getPermission())) {
                    commandList.add(type.getShow());
                }
            }
            Collections.sort(commandList);
            return commandList;
        } else if (strings.length == 1) {
            List<String> st = new ArrayList<>();
            for (AriCommand type : AriCommand.values()) {
                if (type.getShow() == null || type.equals(AriCommand.ARI)) continue;
                if (type.getPermission() == null) {
                    st.add(type.getShow());
                    continue;
                }
                if (Ari.instance.permissionUtils.hasPermission(commandSender, type.getPermission()) && type.getShow().contains(strings[0])) {
                    st.add(type.getShow());
                }
            }
            Collections.sort(st);
            return st;
        } else if (strings.length == 2) {
            AriCommand c;
            try {
                c = AriCommand.valueOf(strings[0].toUpperCase(Locale.ROOT));
            } catch (Exception e) {
                return List.of();
            }
            switch (c) {
                case TPA -> {
                    return CommandTeleport.build(commandSender, strings[1]).getOnlinePlayers(AriCommand.TPA);
                }
                case TPAHERE -> {
                    return CommandTeleport.build(commandSender, strings[1]).getOnlinePlayers(AriCommand.TPAHERE);
                }
                case TPAACCEPT -> {
                    return CommandTeleport.build(commandSender, strings[1]).getHasRequestPlayers(AriCommand.TPAACCEPT);
                }
                case TPAREFUSE -> {
                    return CommandTeleport.build(commandSender, strings[1]).getHasRequestPlayers(AriCommand.TPAREFUSE);
                }
                case DELETEHOME -> {
                    return CommandHome.build(commandSender).getHomeList();
                }

            }
        }
        return List.of();
    }
}
