package ari.superarilo.command;

import ari.superarilo.Ari;
import ari.superarilo.command.tool.CommandCheck;
import ari.superarilo.command.tool.impl.CommandCheckImpl;
import ari.superarilo.entity.TeleportStatus;
import ari.superarilo.enumType.AriCommand;
import ari.superarilo.enumType.FilePath;
import ari.superarilo.function.teleport.TeleportPrecondition;
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

import java.util.*;

public class MainCommand implements TabExecutor {
    private final ConfigFiles config = Ari.instance.getConfigFiles();
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        CommandCheckImpl commandCheck = CommandCheck.create();
        if (!commandCheck.isTheInstructionCorrect(command, AriCommand.ARI)) return false;
        if (strings.length == 0) return false;

        AriCommand type;
        try {
            type = AriCommand.valueOf(strings[0].toUpperCase(Locale.ROOT));
        } catch (Exception e) {
            commandSender.sendMessage(TextTool.setHEXColorText(this.config.getValue("command.unknown", FilePath.Lang, String.class)));
            return true;
        }
        //判断是否有对应的权限
        if(!commandCheck.commandSenderHavePermission(commandSender, type)) {
            commandSender.sendMessage(TextTool.setHEXColorText(this.config.getValue("command.tpa.permission-message", FilePath.Lang, String.class)));
            return true;
        }
        Player player;
        switch (type) {
            case RELOAD:
                commandSender.sendMessage(TextTool.setHEXColorText(this.config.getValue("command.reload.doing", FilePath.Lang, String.class)));
                Ari.instance.getConfigFiles().reloadAllConfig();
                if (Ari.debug) {
                    Ari.instance.getSQLInstance().reconnect();
                }
                commandSender.sendMessage(TextTool.setHEXColorText(this.config.getValue("command.reload.success", FilePath.Lang, String.class)));
                break;
            case TPA:
                if (strings.length < 2 || strings[1].equals(commandSender.getName())) {
                    commandSender.sendMessage(TextTool.setHEXColorText(this.config.getValue("command.tpa.fail", FilePath.Lang, String.class)));
                    return true;
                }
                player = Ari.instance.getServer().getPlayerExact(strings[1]);
                if(player == null) {
                    commandSender.sendMessage(TextTool.setHEXColorText(this.config.getValue("command.tpa.unable-player", FilePath.Lang, String.class)));
                    return true;
                }
                TeleportPrecondition.create().preCheckStatus((Player) commandSender, player, AriCommand.TPA);

                break;
            case TPAACCEPT:
                if (strings.length < 2 || strings[1].equals(commandSender.getName())) {
                    commandSender.sendMessage(TextTool.setHEXColorText(this.config.getValue("command.tpa.fail", FilePath.Lang, String.class)));
                    return true;
                }
                player = Ari.instance.getServer().getPlayerExact(strings[1]);
                //判断玩家是否存在
                if (player == null) {
                    commandSender.sendMessage(TextTool.setHEXColorText(this.config.getValue("command.tpaaccept.unable-player", FilePath.Lang, String.class)));
                    return true;
                }

                //判断请求是否还存在
                TeleportStatus status = TeleportPrecondition.create().checkStatusV(player, (Player) commandSender);
                if(status == null) {
                    commandSender.sendMessage(TextTool.setHEXColorText(this.config.getValue("command.tpaaccept.been-done", FilePath.Lang, String.class)));
                    return true;
                }
                //请求成功，移除该请求
                commandSender.sendMessage(TextTool.setHEXColorText(this.config.getValue("command.tpaaccept.agree", FilePath.Lang, String.class)));
                Ari.instance.getTpStatusValue().remove(player, TeleportThread.Type.PLAYER);
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
                    commandSender.sendMessage(TextTool.setHEXColorText(this.config.getValue("command.tpahere.fail", FilePath.Lang, String.class)));
                    return true;
                }
                player = Ari.instance.getServer().getPlayerExact(strings[1]);
                if (player == null) {
                    commandSender.sendMessage(TextTool.setHEXColorText(this.config.getValue("command.tpahere.unable-player", FilePath.Lang, String.class)));
                    return true;
                }

                TeleportPrecondition.create().preCheckStatus((Player) commandSender, player, AriCommand.TPAHERE);

                break;
            case TPAREFUSE:
                if (strings.length < 2 || strings[1].equals(commandSender.getName())) {
                    commandSender.sendMessage(TextTool.setHEXColorText(this.config.getValue("command.tparefuse.fail", FilePath.Lang, String.class)));
                    return true;
                }
                player = Ari.instance.getServer().getPlayerExact(strings[1]);
                //判断玩家是否存在
                if (player == null) {
                    commandSender.sendMessage(TextTool.setHEXColorText(this.config.getValue("command.tparefuse.unable-player", FilePath.Lang, String.class)));
                    return true;
                }
                if (TeleportPrecondition.create().checkStatusV(player, (Player) commandSender) == null) {
                    commandSender.sendMessage(TextTool.setHEXColorText(this.config.getValue("command.tparefuse.been-done", FilePath.Lang, String.class)));
                    return true;
                } else {
                    if (Ari.instance.getTpStatusValue().getStatusList().removeIf(obj -> obj.getPlayUUID().equals(player.getUniqueId()) && obj.getType().equals(TeleportThread.Type.PLAYER))) {
                        commandSender.sendMessage(TextTool.setHEXColorText(this.config.getValue("command.tparefuse.success", FilePath.Lang, String.class)));
                        player.sendMessage(TextTool.setHEXColorText(this.config.getValue("command.tparefuse.get-message", FilePath.Lang, String.class).replace("[TpaBeSender]", commandSender.getName())));
                    } else {
                        commandSender.sendMessage(TextTool.setHEXColorText(this.config.getValue("command.tparefuse.break", FilePath.Lang, String.class)));
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
                if (commandSender.hasPermission(type.getPermission())) {
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
                    Ari.instance.getTpStatusValue().getStatusList().stream().filter(obj ->
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
