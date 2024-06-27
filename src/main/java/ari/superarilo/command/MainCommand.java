package ari.superarilo.command;

import ari.superarilo.Ari;
import ari.superarilo.entity.TeleportStatus;
import ari.superarilo.enumType.AriCommand;
import ari.superarilo.enumType.KeyType;
import ari.superarilo.function.teleport.TeleportPrecondition;
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

public class MainCommand implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!(command.getName().equalsIgnoreCase(AriCommand.ARILO.getShow()) || AriCommand.ARILO.getAliases().contains(s))) return false;
        if (strings.length == 0) return false;
        AriCommand type;
        try {
            type = AriCommand.valueOf(strings[0].toUpperCase(Locale.ROOT));
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
                player = Ari.instance.getServer().getPlayerExact(strings[1]);
                if(player == null) {
                    commandSender.sendMessage(TextTool.setHEXColorText(ConfigFiles.configs.get("lang").getString("command.tpa.unable-player", "null")));
                    return true;
                }
                TeleportPrecondition.create().preCheckStatus((Player) commandSender, player, AriCommand.TPA);

                break;
            case TPAACCEPT:
                if (strings.length < 2 || strings[1].equals(commandSender.getName())) {
                    commandSender.sendMessage(TextTool.setHEXColorText(ConfigFiles.configs.get("lang").getString("command.tpa.fail", "null")));
                    return true;
                }
                player = Ari.instance.getServer().getPlayerExact(strings[1]);
                //判断玩家是否存在
                if (player == null) {
                    commandSender.sendMessage(TextTool.setHEXColorText(ConfigFiles.configs.get("lang").getString("command.tpaaccept.unable-player", "null")));
                    return true;
                }
                //判断请求是否还存在
                List<TeleportStatus> statusList = Ari.getTeleportStatusList().stream().filter(obj ->
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
                Ari.deleteAddTeleportStatus(player.getUniqueId(), TeleportThread.Type.PLAYER);
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
                player = Ari.instance.getServer().getPlayerExact(strings[1]);
                if (player == null) {
                    commandSender.sendMessage(TextTool.setHEXColorText(ConfigFiles.configs.get("lang").getString("command.tpahere.unable-player", "null")));
                    return true;
                }

                if(Ari.getTeleportStatusList().stream().filter(obj -> obj.getPlayUUID().equals(((Player) commandSender).getUniqueId()) && obj.getType().equals(TeleportThread.Type.PLAYER)).toList().isEmpty()) {
                    commandSender.sendMessage(TextTool.setHEXColorText(ConfigFiles.configs.get("lang").getString("command.tpahere.send-message", "null")));

                    //开始向传送发起者和接收者发送消息
                    player.sendMessage(
                            TextTool.setHEXColorText(ConfigFiles.configs.get("lang").getString("command.tpahere.get-message", "null").replace(KeyType.TPASENDER.getType(), commandSender.getName()))
                                    .appendNewline()
                                    .append(TextTool.setClickEventText("&a[同意]", ClickEvent.Action.RUN_COMMAND, "/ari tpaaccept " + commandSender.getName()))
                                    .append(TextTool.setHEXColorText("&f或者"))
                                    .append(TextTool.setClickEventText("&c[拒绝]", ClickEvent.Action.RUN_COMMAND, "/ari tparefuse " + commandSender.getName())));
                    Bukkit.getAsyncScheduler().runNow(Ari.instance, t -> {
                        TeleportStatus hereStatus = new TeleportStatus();
                        hereStatus.setType(TeleportThread.Type.PLAYER);
                        hereStatus.setCommandType(AriCommand.TPAHERE);
                        hereStatus.setPlayUUID(((Player) commandSender).getUniqueId());
                        hereStatus.setBePlayerUUID(player.getUniqueId());
                        Ari.addTeleportStatus(hereStatus);
                        //设置定时任务来移除该玩家已经发送的请求状态
                        Bukkit.getAsyncScheduler().runDelayed(Ari.instance, i -> Ari.getTeleportStatusList().removeIf(obj -> obj.getPlayUUID().equals(((Player) commandSender).getUniqueId()) && obj.getType().equals(TeleportThread.Type.PLAYER)), 10L, TimeUnit.SECONDS);
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
                player = Ari.instance.getServer().getPlayerExact(strings[1]);
                //判断玩家是否存在
                if (player == null) {
                    commandSender.sendMessage(TextTool.setHEXColorText(ConfigFiles.configs.get("lang").getString("command.tparefuse.unable-player", "null")));
                    return true;
                }
                //判断请求是否还存在
                if(Ari.getTeleportStatusList().stream().noneMatch(obj -> obj.getPlayUUID().equals(player.getUniqueId()) && obj.getType().equals(TeleportThread.Type.PLAYER))) {
                    commandSender.sendMessage(TextTool.setHEXColorText(ConfigFiles.configs.get("lang").getString("command.tparefuse.been-done", "null")));
                    return true;
                }

                if (Ari.getTeleportStatusList().removeIf(obj -> obj.getPlayUUID().equals(player.getUniqueId()) && obj.getType().equals(TeleportThread.Type.PLAYER))) {
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
        if(!(command.getName().equalsIgnoreCase(AriCommand.ARILO.getShow()) || AriCommand.ARILO.getAliases().contains(s))) return List.of("");
        //返回所有具有权限的指令给玩家
        if (strings[0].isEmpty()) {
            List<String> commandList = new ArrayList<>();
            for (AriCommand type : AriCommand.values()) {
                if (type.getShow() == null || type.equals(AriCommand.ARILO)) continue;
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
            for (AriCommand type : AriCommand.values()) {
                if (type.getShow() == null || type.equals(AriCommand.ARILO)) continue;
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
            AriCommand c;
            try {
                c = AriCommand.valueOf(strings[0].toUpperCase(Locale.ROOT));
            } catch (Exception e) {
                return st;
            }
            Server server = Ari.instance.getServer();
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
                    Ari.getTeleportStatusList().stream().filter(obj ->
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
