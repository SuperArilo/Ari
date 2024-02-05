package ari.superarilo.command;

import ari.superarilo.SuperArilo;
import ari.superarilo.entity.TeleportStatus;
import ari.superarilo.enumType.Commands;
import ari.superarilo.enumType.KeyType;
import ari.superarilo.tool.ConfigFiles;
import ari.superarilo.tool.TeleportThread;
import ari.superarilo.tool.TextTool;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class Arilo implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!(command.getName().equalsIgnoreCase(Commands.ARILO.getShow()) || Commands.ARILO.getAliases().contains(s))) return false;
        if (strings.length == 0) return false;
        Commands type;
        try {
            type = Commands.valueOf(strings[0].toUpperCase(Locale.ROOT));
        } catch (Exception e) {
            commandSender.sendMessage(TextTool.setHEXColorText(ConfigFiles.configs.get("lang").getString("command.unknown", "null")));
            return true;
        }
        //判断是否有对应的权限
        if(!commandSender.hasPermission(type.getPermission())) {
            commandSender.sendMessage(TextTool.setHEXColorText(ConfigFiles.configs.get("lang").getString("command.tpa.permission-message", type.getPermissionMessage())));
            return true;
        }
        Player player;
        switch (type) {
            case RELOAD:
                commandSender.sendMessage(TextTool.setHEXColorText(ConfigFiles.configs.get("lang").getString("command.reload.doing","null")));
                ConfigFiles.reloadAllConfig();
                commandSender.sendMessage(TextTool.setHEXColorText(ConfigFiles.configs.get("lang").getString("command.reload.success","null")));
                break;
            case TPA:
                if (strings.length < 2 || strings[1].equals(commandSender.getName())) {
                    commandSender.sendMessage(TextTool.setHEXColorText(ConfigFiles.configs.get("lang").getString("command.tpa.fail", "null")));
                    return true;
                }
                player = SuperArilo.instance.getServer().getPlayerExact(strings[1]);
                if(player == null) {
                    commandSender.sendMessage(TextTool.setHEXColorText(ConfigFiles.configs.get("lang").getString("command.tpa.unable-player", "null")));
                    return true;
                }

                commandSender.sendMessage(TextTool.setHEXColorText(ConfigFiles.configs.get("lang").getString("command.tpa.send-message", "null")));

                //开始向传送发起者和接收者发送消息
                player.sendMessage(
                        TextTool.setHEXColorText(ConfigFiles.configs.get("lang").getString("command.tpa.get-message", "null").replace(KeyType.TPASENDER.getType(), commandSender.getName()))
                                .appendNewline()
                                .append(TextTool.setClickEventText("&a[同意]",ClickEvent.Action.RUN_COMMAND, "/tpaaccept " + commandSender.getName()))
                                .append(TextTool.setHEXColorText("&f或者"))
                                .append(TextTool.setClickEventText("&c[拒绝]", ClickEvent.Action.RUN_COMMAND, "/tparefuse " + commandSender.getName())));

                Bukkit.getAsyncScheduler().runNow(SuperArilo.instance, t -> {
                    if(SuperArilo.getTeleportStatusList().stream().filter(obj -> obj.getPlayUUID().equals(((Player) commandSender).getUniqueId()) && obj.getType().equals(TeleportThread.Type.PLAYER)).toList().isEmpty()) {
                        TeleportStatus status = new TeleportStatus();
                        status.setType(TeleportThread.Type.PLAYER);
                        status.setCommandType(Commands.TPA);
                        status.setPlayUUID(((Player) commandSender).getUniqueId());
                        status.setBePlayerUUID(player.getUniqueId());
                        SuperArilo.addTeleportStatus(status);
                        //设置定时任务来移除该玩家已经发送的请求状态
                        Bukkit.getAsyncScheduler().runDelayed(SuperArilo.instance, i -> SuperArilo.getTeleportStatusList().removeIf(obj -> obj.getPlayUUID().equals(((Player) commandSender).getUniqueId()) && obj.getType().equals(TeleportThread.Type.PLAYER)), 10L, TimeUnit.SECONDS);
                    } else {
                        commandSender.sendMessage(TextTool.setHEXColorText(ConfigFiles.configs.get("lang").getString("command.tpa.again","null").replace(KeyType.TPABESENDER.getType(), player.getName())));
                    }
                });
                break;
            case TPAACCEPT:
                if (strings.length < 2 || strings[1].equals(commandSender.getName())) {
                    commandSender.sendMessage(TextTool.setHEXColorText(ConfigFiles.configs.get("lang").getString("command.tpa.fail", "null")));
                    return true;
                }
                player = SuperArilo.instance.getServer().getPlayerExact(strings[1]);
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

                break;
            case TPAHERE:
                if (strings.length < 2 || strings[1].equals(commandSender.getName())) {
                    commandSender.sendMessage(TextTool.setHEXColorText(ConfigFiles.configs.get("lang").getString("command.tpahere.fail", "null")));
                    return true;
                }
                player = SuperArilo.instance.getServer().getPlayerExact(strings[1]);
                if (player == null) {
                    commandSender.sendMessage(TextTool.setHEXColorText(ConfigFiles.configs.get("lang").getString("command.tpahere.unable-player", "null")));
                    return true;
                }

                if(SuperArilo.getTeleportStatusList().stream().filter(obj -> obj.getPlayUUID().equals(((Player) commandSender).getUniqueId()) && obj.getType().equals(TeleportThread.Type.PLAYER)).toList().isEmpty()) {
                    commandSender.sendMessage(TextTool.setHEXColorText(ConfigFiles.configs.get("lang").getString("command.tpahere.send-message", "null")));

                    //开始向传送发起者和接收者发送消息
                    player.sendMessage(
                            TextTool.setHEXColorText(ConfigFiles.configs.get("lang").getString("command.tpahere.get-message", "null").replace(KeyType.TPASENDER.getType(), commandSender.getName()))
                                    .appendNewline()
                                    .append(TextTool.setClickEventText("&a[同意]", ClickEvent.Action.RUN_COMMAND, "/ari tpaaccept " + commandSender.getName()))
                                    .append(TextTool.setHEXColorText("&f或者"))
                                    .append(TextTool.setClickEventText("&c[拒绝]", ClickEvent.Action.RUN_COMMAND, "/ari tparefuse " + commandSender.getName())));
                    Bukkit.getAsyncScheduler().runNow(SuperArilo.instance, t -> {
                        TeleportStatus hereStatus = new TeleportStatus();
                        hereStatus.setType(TeleportThread.Type.PLAYER);
                        hereStatus.setCommandType(Commands.TPAHERE);
                        hereStatus.setPlayUUID(((Player) commandSender).getUniqueId());
                        hereStatus.setBePlayerUUID(player.getUniqueId());
                        SuperArilo.addTeleportStatus(hereStatus);
                        //设置定时任务来移除该玩家已经发送的请求状态
                        Bukkit.getAsyncScheduler().runDelayed(SuperArilo.instance, i -> SuperArilo.getTeleportStatusList().removeIf(obj -> obj.getPlayUUID().equals(((Player) commandSender).getUniqueId()) && obj.getType().equals(TeleportThread.Type.PLAYER)), 10L, TimeUnit.SECONDS);
                    });
                } else {
                    commandSender.sendMessage(TextTool.setHEXColorText(ConfigFiles.configs.get("lang").getString("command.tpahere.again","null").replace(KeyType.TPABESENDER.getType(), player.getName())));
                }

                break;
            case TPAREFUSE:
                if (strings.length < 2 || strings[1].equals(commandSender.getName())) {
                    commandSender.sendMessage(TextTool.setHEXColorText(ConfigFiles.configs.get("lang").getString("command.tparefuse.fail", "null")));
                    return true;
                }
                player = SuperArilo.instance.getServer().getPlayerExact(strings[1]);
                //判断玩家是否存在
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

                break;
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(!(command.getName().equalsIgnoreCase(Commands.ARILO.getShow()) || Commands.ARILO.getAliases().contains(s))) return List.of("");
        //返回所有具有权限的指令给玩家
        if (strings[0].isEmpty()) {
            List<String> commandList = new ArrayList<>();
            for (Commands type : Commands.values()) {
                if (type.getShow() == null || type.equals(Commands.ARILO)) continue;
                if (type.getPermission() == null) {
                    commandList.add(type.getShow());
                    continue;
                }
                if (commandSender.hasPermission(type.getPermission())) {
                    commandList.add(type.getShow());
                }
            }
            Collections.sort(commandList);
            return commandList;
        } else if (strings.length == 1) {
            List<String> st = new ArrayList<>();
            for (Commands type : Commands.values()) {
                if (type.getShow() == null || type.equals(Commands.ARILO)) continue;
                if (type.getPermission() == null) {
                    st.add(type.getShow());
                    continue;
                }
                if (commandSender.hasPermission(type.getPermission()) && type.getShow().contains(strings[0])) {
                    st.add(type.getShow());
                }
            }
            Collections.sort(st);
            return st;
        } else if (strings.length == 2) {
            List<String> st = new ArrayList<>();
            Commands c;
            try {
                c = Commands.valueOf(strings[0].toUpperCase(Locale.ROOT));
            } catch (Exception e) {
                return st;
            }
            Server server = SuperArilo.instance.getServer();
            switch (c) {
                case TPAHERE:
                case TPA:
                    server.getOnlinePlayers().forEach(e -> {
                        if(!e.getName().equals(commandSender.getName())) {
                            st.add(e.getName());
                        }
                    });
                    break;
                case TPAACCEPT:
                case TPAREFUSE:
                    SuperArilo.getTeleportStatusList().stream().filter(obj ->
                                    obj.getBePlayerUUID().equals(((Player) commandSender).getUniqueId()) && obj.getType().equals(TeleportThread.Type.PLAYER)).toList()
                            .forEach(e -> {
                                Player player = server.getPlayer(e.getPlayUUID());
                                if (player != null) {
                                    st.add(player.getName());
                                }
                            });
                    break;
            }
            return st;
        }
        return List.of("");
    }
}
