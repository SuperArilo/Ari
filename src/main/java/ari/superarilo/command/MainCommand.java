package ari.superarilo.command;

import ari.superarilo.Ari;
import ari.superarilo.enumType.TeleportObjectType;
import ari.superarilo.function.CommandCheck;
import ari.superarilo.function.HomeManager;
import ari.superarilo.function.impl.CommandCheckImpl;
import ari.superarilo.entity.TeleportStatus;
import ari.superarilo.enumType.AriCommand;
import ari.superarilo.enumType.FilePath;
import ari.superarilo.enumType.TeleportType;
import ari.superarilo.function.TeleportPrecondition;
import ari.superarilo.function.impl.TeleportThreadImpl;
import ari.superarilo.gui.home.HomeList;
import ari.superarilo.tool.TextTool;
import ari.superarilo.function.TeleportThread;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class MainCommand implements TabExecutor {

    private final TextComponent commandPublicFail = TextTool.setHEXColorText("command.public.fail", FilePath.Lang);
    private final TextComponent teleportUnablePlayer = TextTool.setHEXColorText("teleport.unable-player", FilePath.Lang);

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
                if(!commandCheck.commandSenderHavePermission(AriCommand.TPA)) {
                    return true;
                }
                if(!commandCheck.isPlayer()) break;
                if (strings.length != 2 || strings[1].equals(commandSender.getName())) {
                    commandSender.sendMessage(this.commandPublicFail);
                    return true;
                }
                Player player = Ari.instance.getServer().getPlayerExact(strings[1]);
                if(player == null) {
                    commandSender.sendMessage(this.teleportUnablePlayer);
                    return true;
                }
                TeleportPrecondition.create().preCheckStatus((Player) commandSender, player, AriCommand.TPA);
            }
            case TPAACCEPT -> {
                if(!commandCheck.commandSenderHavePermission(AriCommand.TPAACCEPT)) {
                    return true;
                }
                if(!commandCheck.isPlayer()) break;
                if (strings.length != 2 || strings[1].equals(commandSender.getName())) {
                    commandSender.sendMessage(this.commandPublicFail);
                    return true;
                }
                //判断玩家是否存在
                Player player = Ari.instance.getServer().getPlayerExact(strings[1]);
                if (player == null) {
                    commandSender.sendMessage(this.teleportUnablePlayer);
                    return true;
                }

                //判断请求是否还存在
                TeleportStatus status = TeleportPrecondition.create().checkStatusV(player, (Player) commandSender);
                if(status == null) {
                    commandSender.sendMessage(TextTool.setHEXColorText("command.tpaaccept.been-done", FilePath.Lang));
                    return true;
                }
                //请求成功，移除该请求
                commandSender.sendMessage(TextTool.setHEXColorText("command.tpaaccept.agree", FilePath.Lang));
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
            }
            case TPAHERE -> {
                if(!commandCheck.commandSenderHavePermission(AriCommand.TPAHERE)) {
                    return true;
                }
                if(!commandCheck.isPlayer()) break;
                if (strings.length != 2 || strings[1].equals(commandSender.getName())) {
                    commandSender.sendMessage(this.commandPublicFail);
                    return true;
                }
                Player player = Ari.instance.getServer().getPlayerExact(strings[1]);
                if (player == null) {
                    commandSender.sendMessage(this.teleportUnablePlayer);
                    return true;
                }

                TeleportPrecondition.create().preCheckStatus((Player) commandSender, player, AriCommand.TPAHERE);
            }
            case TPAREFUSE -> {
                if(!commandCheck.commandSenderHavePermission(AriCommand.TPAREFUSE)) {
                    return true;
                }
                if(!commandCheck.isPlayer()) break;
                if (strings.length != 2 || strings[1].equals(commandSender.getName())) {
                    commandSender.sendMessage(this.commandPublicFail);
                    return true;
                }
                //判断玩家是否存在
                Player player = Ari.instance.getServer().getPlayerExact(strings[1]);
                if (player == null) {
                    commandSender.sendMessage(this.teleportUnablePlayer);
                    return true;
                }
                if (TeleportPrecondition.create().checkStatusV(player, (Player) commandSender) == null) {
                    commandSender.sendMessage(TextTool.setHEXColorText("command.tparefuse.been-done", FilePath.Lang));
                    return true;
                } else {
                    if (Ari.instance.tpStatusValue.getStatusList().removeIf(obj -> obj.getPlayUUID().equals(player.getUniqueId()) && obj.getType().equals(TeleportType.PLAYER))) {
                        commandSender.sendMessage(TextTool.setHEXColorText("command.tparefuse.success", FilePath.Lang));
                        if(Ari.instance.configManager.getValue("command.tparefuse.get-message", FilePath.Lang, String.class) instanceof String message) {
                            player.sendMessage(TextTool.setHEXColorText(message.replace(TeleportObjectType.TPABESENDER.getType(), commandSender.getName())));
                        }
                    } else {
                        commandSender.sendMessage(TextTool.setHEXColorText("command.public.break", FilePath.Lang));
                    }
                }
            }
            case HOME -> {
                if(!commandCheck.commandSenderHavePermission(AriCommand.HOME)) {
                    return true;
                }
                if(!commandCheck.isPlayer()) break;
                new HomeList((Player) commandSender).open();
            }
            case SETHOME -> {
                if(!commandCheck.commandSenderHavePermission(AriCommand.SETHOME)) {
                    return true;
                }
                if(!commandCheck.isPlayer()) break;
                if (strings.length != 2) {
                    commandSender.sendMessage(this.commandPublicFail);
                    return true;
                }
                if(Ari.instance.formatUtil.checkIdName(strings[1])) {
                    HomeManager.create((Player) commandSender).createNewHome(strings[1]);
                } else {
                    commandSender.sendMessage(TextTool.setHEXColorText("command.sethome.id-error", FilePath.Lang));
                }
            }
            case DELETEHOME -> {
                if(!commandCheck.commandSenderHavePermission(AriCommand.SETHOME)) {
                    return true;
                }
                if(!commandCheck.isPlayer()) break;
                if (strings.length != 2) {
                    commandSender.sendMessage(this.commandPublicFail);
                    return true;
                }
                if(Ari.instance.formatUtil.checkIdName(strings[1])) {
                    HomeManager.create((Player) commandSender).deleteHome(strings[1]);
                } else {
                    commandSender.sendMessage(TextTool.setHEXColorText("command.deletehome.id-error", FilePath.Lang));
                }
            }
            case BACK -> {
                if(!commandCheck.commandSenderHavePermission(AriCommand.BACK)) {
                    return true;
                }
                if(!commandCheck.isPlayer()) break;
                Player player = (Player) commandSender;
                Location beforeLocation = TeleportThreadImpl.lastLocation.get(player.getUniqueId());
                if(beforeLocation == null) {
                    commandSender.sendMessage(TextTool.setHEXColorText("teleport.none-location", FilePath.Lang));
                    return true;
                }
                if(TeleportPrecondition.create().preCheckStatus(player, beforeLocation, AriCommand.BACK)) {
                    TeleportThread
                            .playerToLocation(
                                    player,
                                    beforeLocation)
                            .teleport(Ari.instance.configManager.getValue(
                                    "main.teleport.delay",
                                    FilePath.TPA,
                                    Integer.class));
                }
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
                case TPA, TPAHERE -> {
                    List<String> list = new ArrayList<>();
                    Ari.instance.getServer().getOnlinePlayers().forEach(p -> {
                        if(!p.getName().equals(commandSender.getName())) {
                            list.add(p.getName());
                        }
                    });
                    return list;
                }
                case TPAACCEPT, TPAREFUSE -> {
                    List<String> list = new ArrayList<>();
                    Ari.instance.tpStatusValue.getStatusList().stream().filter(obj -> obj.getBePlayerUUID().equals(((Player) commandSender).getUniqueId()) && obj.getType().equals(TeleportType.PLAYER)).toList()
                            .forEach(e -> {
                                Player player = Ari.instance.getServer().getPlayer(e.getPlayUUID());
                                if (player != null) {
                                    list.add(player.getName());
                                }
                            });
                    return list;
                }
                case DELETEHOME -> {
                    List<String> list = HomeManager.create((Player) commandSender).asyncGetHomeIdList();
                    Collections.sort(list);
                    return list;
                }

            }
        }
        return List.of();
    }
}
