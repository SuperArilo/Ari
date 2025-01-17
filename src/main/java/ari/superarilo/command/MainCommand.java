package ari.superarilo.command;

import ari.superarilo.Ari;
import ari.superarilo.function.CommandCheck;
import ari.superarilo.function.impl.CommandCheckImpl;
import ari.superarilo.entity.TeleportStatus;
import ari.superarilo.enumType.AriCommand;
import ari.superarilo.enumType.FilePath;
import ari.superarilo.enumType.TeleportType;
import ari.superarilo.function.TeleportPrecondition;
import ari.superarilo.tool.TextTool;
import ari.superarilo.function.TeleportThread;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class MainCommand implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        CommandCheckImpl commandCheck = CommandCheck.create();
        if (!commandCheck.isTheInstructionCorrect(command, AriCommand.ARI)) return false;
        if (strings.length == 0) return false;

        AriCommand type;
        try {
            type = AriCommand.valueOf(strings[0].toUpperCase(Locale.ROOT));
        } catch (Exception e) {
            commandSender.sendMessage(TextTool.setHEXColorText(Ari.instance.configManager.getValue("command.unknown", FilePath.Lang, String.class)));
            return true;
        }
        //判断是否有对应的权限
        if(!commandCheck.commandSenderHavePermission(commandSender, type)) {
            commandSender.sendMessage(TextTool.setHEXColorText(Ari.instance.configManager.getValue("command.public.permission-message", FilePath.Lang, String.class)));
            return true;
        }
        Player player;
        switch (type) {
            case RELOAD:
                commandSender.sendMessage(TextTool.setHEXColorText(Ari.instance.configManager.getValue("command.reload.doing", FilePath.Lang, String.class)));
                Ari.instance.configManager.reloadAllConfig();
                if (Ari.debug) {
                    Ari.instance.SQLInstance.reconnect();
                }
                commandSender.sendMessage(TextTool.setHEXColorText(Ari.instance.configManager.getValue("command.reload.success", FilePath.Lang, String.class)));
                break;
            case TPA:
                if (strings.length < 2 || strings[1].equals(commandSender.getName())) {
                    commandSender.sendMessage(TextTool.setHEXColorText(Ari.instance.configManager.getValue("command.public.fail", FilePath.Lang, String.class)));
                    return true;
                }
                player = Ari.instance.getServer().getPlayerExact(strings[1]);
                if(player == null) {
                    commandSender.sendMessage(TextTool.setHEXColorText(Ari.instance.configManager.getValue("teleport.unable-player", FilePath.Lang, String.class)));
                    return true;
                }
                TeleportPrecondition.create().preCheckStatus((Player) commandSender, player, AriCommand.TPA);

                break;
            case TPAACCEPT:
                if (strings.length < 2 || strings[1].equals(commandSender.getName())) {
                    commandSender.sendMessage(TextTool.setHEXColorText(Ari.instance.configManager.getValue("command.public.fail", FilePath.Lang, String.class)));
                    return true;
                }
                player = Ari.instance.getServer().getPlayerExact(strings[1]);
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
                    case TPAHERE -> TeleportThread.playerToPlayer((Player) commandSender, player);
                    default -> null;
                };
                if (teleportThread != null) {
                    teleportThread.teleport(Ari.instance.configManager.getValue("main.teleport.delay", FilePath.TPA, Integer.class));
                } else {
                    commandSender.sendMessage(TextTool.setHEXColorText("command.tpaaccept.error"));
                }

                break;
            case TPAHERE:
                if (strings.length < 2 || strings[1].equals(commandSender.getName())) {
                    commandSender.sendMessage(TextTool.setHEXColorText(Ari.instance.configManager.getValue("command.public.fail", FilePath.Lang, String.class)));
                    return true;
                }
                player = Ari.instance.getServer().getPlayerExact(strings[1]);
                if (player == null) {
                    commandSender.sendMessage(TextTool.setHEXColorText(Ari.instance.configManager.getValue("teleport.unable-player", FilePath.Lang, String.class)));
                    return true;
                }

                TeleportPrecondition.create().preCheckStatus((Player) commandSender, player, AriCommand.TPAHERE);

                break;
            case TPAREFUSE:
                if (strings.length < 2 || strings[1].equals(commandSender.getName())) {
                    commandSender.sendMessage(TextTool.setHEXColorText(Ari.instance.configManager.getValue("command.public.fail", FilePath.Lang, String.class)));
                    return true;
                }
                player = Ari.instance.getServer().getPlayerExact(strings[1]);
                //判断玩家是否存在
                if (player == null) {
                    commandSender.sendMessage(TextTool.setHEXColorText(Ari.instance.configManager.getValue("teleport.unable-player", FilePath.Lang, String.class)));
                    return true;
                }
                if (TeleportPrecondition.create().checkStatusV(player, (Player) commandSender) == null) {
                    commandSender.sendMessage(TextTool.setHEXColorText(Ari.instance.configManager.getValue("command.tparefuse.been-done", FilePath.Lang, String.class)));
                    return true;
                } else {
                    if (Ari.instance.tpStatusValue.getStatusList().removeIf(obj -> obj.getPlayUUID().equals(player.getUniqueId()) && obj.getType().equals(TeleportType.PLAYER))) {
                        commandSender.sendMessage(TextTool.setHEXColorText(Ari.instance.configManager.getValue("command.tparefuse.success", FilePath.Lang, String.class)));
                        if(Ari.instance.configManager.getValue("command.tparefuse.get-message", FilePath.Lang, String.class) instanceof String message) {
                            player.sendMessage(TextTool.setHEXColorText(message.replace("[TpaBeSender]", commandSender.getName())));
                        }
                    } else {
                        commandSender.sendMessage(TextTool.setHEXColorText(Ari.instance.configManager.getValue("command.public.break", FilePath.Lang, String.class)));
                    }
                }
                break;
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(!(command.getName().equalsIgnoreCase(AriCommand.ARI.getShow()) || AriCommand.ARI.getAliases().contains(s))) return List.of("");
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
                    Ari.instance.tpStatusValue.getStatusList().stream().filter(obj ->
                                    obj.getBePlayerUUID().equals(((Player) commandSender).getUniqueId()) && obj.getType().equals(TeleportType.PLAYER)).toList()
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
