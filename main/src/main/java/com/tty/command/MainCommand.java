package com.tty.command;

import com.tty.Ari;
import com.tty.command.function.CommandBack;
import com.tty.command.function.CommandHome;
import com.tty.command.function.CommandTeleport;
import com.tty.command.function.CommandWarp;
import com.tty.dto.event.CustomPluginReloadEvent;
import com.tty.function.CommandCheck;
import com.tty.function.impl.CommandCheckImpl;
import com.tty.enumType.AriCommand;
import com.tty.enumType.FilePath;
import com.tty.tool.TextTool;
import org.bukkit.Bukkit;
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
            commandSender.sendMessage(TextTool.setHEXColorText("function.unknown", FilePath.Lang));
            return true;
        }
        switch (type) {
            case RELOAD -> {
                if(!commandCheck.commandSenderHavePermission(AriCommand.RELOAD)) {
                    return true;
                }
                commandSender.sendMessage(TextTool.setHEXColorText("function.reload.doing", FilePath.Lang));
                Ari.instance.configManager.reloadAllConfig();
                if (Ari.debug) {
                    Ari.instance.SQLInstance.reconnect();
                }
                Ari.instance.commandAlias.reloadAllAlias();
                Bukkit.getPluginManager().callEvent(new CustomPluginReloadEvent());
                commandSender.sendMessage(TextTool.setHEXColorText("function.reload.success", FilePath.Lang));
            }
            case TPA -> {
                if(!commandCheck.commandSenderHavePermission(AriCommand.TPA) || strings.length != 2) {
                    return true;
                }
                new CommandTeleport(commandSender, strings[1]).tpa();
            }
            case TPAACCEPT -> {
                if(!commandCheck.commandSenderHavePermission(AriCommand.TPAACCEPT) || strings.length != 2) {
                    return true;
                }
                new CommandTeleport(commandSender, strings[1]).tpaaccept();
            }
            case TPAHERE -> {
                if(!commandCheck.commandSenderHavePermission(AriCommand.TPAHERE) || strings.length != 2) {
                    return true;
                }
                new CommandTeleport(commandSender, strings[1]).tpahere();
            }
            case TPAREFUSE -> {
                if(!commandCheck.commandSenderHavePermission(AriCommand.TPAREFUSE) | strings.length != 2) {
                    return true;
                }
                new CommandTeleport(commandSender, strings[1]).tparefuse();
            }
            case HOME -> {
                if(!commandCheck.commandSenderHavePermission(AriCommand.HOME)) {
                    return true;
                }
                if(!commandCheck.isPlayer()) break;
                new CommandHome(commandSender).home();
            }
            case SETHOME -> {
                if(!commandCheck.commandSenderHavePermission(AriCommand.SETHOME)) {
                    return true;
                }
                if(!commandCheck.isPlayer()) break;
                if (strings.length != 2) {
                    commandSender.sendMessage(TextTool.setHEXColorText("function.public.fail", FilePath.Lang));
                    return true;
                }
                new CommandHome(commandSender).setHome(strings[1]);
            }
            case DELETEHOME -> {
                if(!commandCheck.commandSenderHavePermission(AriCommand.SETHOME)) {
                    return true;
                }
                if(!commandCheck.isPlayer()) break;
                if (strings.length != 2) {
                    commandSender.sendMessage(TextTool.setHEXColorText("function.public.fail", FilePath.Lang));
                    return true;
                }
                new CommandHome(commandSender).deleteHome(strings[1]);
            }
            case BACK -> {
                if(!commandCheck.commandSenderHavePermission(AriCommand.BACK)) {
                    return true;
                }
                if(!commandCheck.isPlayer()) break;
                new CommandBack(commandSender).startDo();
            }
            case WARP -> {
                if(!commandCheck.commandSenderHavePermission(AriCommand.WARP)) {
                    return true;
                }
                if(!commandCheck.isPlayer()) break;
                new CommandWarp(commandSender).warp();
            }
            case SETWARP -> {
                if(!commandCheck.commandSenderHavePermission(AriCommand.SETHOME)) {
                    return true;
                }
                if(!commandCheck.isPlayer()) break;
                if (strings.length != 2) {
                    commandSender.sendMessage(TextTool.setHEXColorText("function.public.fail", FilePath.Lang));
                    return true;
                }
                new CommandWarp(commandSender).setWarp(strings[1]);
            }
            case DELETEWARP -> {
                if(!commandCheck.commandSenderHavePermission(AriCommand.DELETEWARP)) {
                    return true;
                }
                if(!commandCheck.isPlayer()) break;
                if (strings.length != 2) {
                    commandSender.sendMessage(TextTool.setHEXColorText("function.public.fail", FilePath.Lang));
                    return true;
                }
                new CommandWarp(commandSender).deleteWarp(strings[1]);
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
                    return new CommandTeleport(commandSender, strings[1]).getOnlinePlayers(AriCommand.TPA);
                }
                case TPAHERE -> {
                    return new CommandTeleport(commandSender, strings[1]).getOnlinePlayers(AriCommand.TPAHERE);
                }
                case TPAACCEPT -> {
                    return new CommandTeleport(commandSender, strings[1]).getHasRequestPlayers(AriCommand.TPAACCEPT);
                }
                case TPAREFUSE -> {
                    return new CommandTeleport(commandSender, strings[1]).getHasRequestPlayers(AriCommand.TPAREFUSE);
                }
                case DELETEHOME -> {
                    return new CommandHome(commandSender).getHomeList();
                }
                case DELETEWARP -> {
                    return new CommandWarp(commandSender).getWarpList();
                }
            }
        }
        return List.of();
    }
}
