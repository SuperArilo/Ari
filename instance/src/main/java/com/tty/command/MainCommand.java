package com.tty.command;

import com.tty.command.check.BaseCommandCheck;
import com.tty.command.function.*;
import com.tty.command.lists.rtp;
import com.tty.dto.event.CustomPluginReloadEvent;
import com.tty.enumType.AriCommand;
import com.tty.enumType.commands.Rtp;
import com.tty.enumType.commands.Zako;
import com.tty.lib.tool.PublicFunctionUtils;
import com.tty.tool.ConfigUtils;
import com.tty.tool.PermissionUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class MainCommand extends BaseCommandCheck implements TabExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s,  String @NotNull [] strings) {
        if (!this.isTheInstructionCorrect(command, AriCommand.ARI)) return false;
        if (strings.length == 0) return false;
        AriCommand type;
        try {
            type = AriCommand.valueOf(strings[0].toUpperCase(Locale.ROOT));
        } catch (Exception e) {
            commandSender.sendMessage(ConfigUtils.t("function.unknown"));
            return true;
        }
        switch (strings.length) {
            case 1 -> {
                switch (type) {
                    case RTP -> {
                        if (!this.quickCheck(commandSender, AriCommand.RTP)) break;
                        Player player = (Player) commandSender;
                        CommandRtp commandRtp = new CommandRtp(player);
                        rtp.RTP_LIST.put(player, commandRtp);
                        commandRtp.rtp();
                    }
                    case RELOAD -> {
                        if(!this.hasPermission(commandSender, AriCommand.RELOAD)) return true;
                        commandSender.sendMessage(ConfigUtils.t("function.reload.doing"));
                        Bukkit.getPluginManager().callEvent(new CustomPluginReloadEvent(commandSender));
                    }
                    case HOME -> {
                        if (!this.quickCheck(commandSender, AriCommand.HOME)) break;
                        new CommandHome(commandSender).home();
                    }
                    case BACK -> {
                        if(!this.quickCheck(commandSender, AriCommand.BACK)) break;
                        new CommandBack(commandSender).startDo();
                    }
                    case WARP -> {
                        if (!this.quickCheck(commandSender, AriCommand.WARP)) break;
                        new CommandWarp(commandSender).warp();
                    }
                    case SPAWN -> {
                        if (!this.quickCheck(commandSender, AriCommand.SPAWN)) break;
                        new CommandSpawn((Player) commandSender).convey();
                    }
                    case SETSPAWN -> {
                        if (!this.quickCheck(commandSender, AriCommand.SETSPAWN, strings.length, 1)) break;
                        new CommandSpawn((Player) commandSender).set();
                    }
                }
            }
            case 2 -> {
                switch (type) {
                    case RTP -> {
                        Player player = (Player) commandSender;
                        if (strings[1].equals(Rtp.CANCEL.getName())) {
                            CommandRtp commandRtp = rtp.RTP_LIST.get(player);
                            if (commandRtp == null) {
                                commandSender.sendMessage(ConfigUtils.t("function.rtp.no-rtp"));
                                return true;
                            } else {
                                commandRtp.cancelRtp();
                            }
                        } else {
                            commandSender.sendMessage(ConfigUtils.t("function.public.fail"));
                        }
                    }
                    case TPA -> new CommandTeleport((Player) commandSender, strings[1]).tpa();
                    case TPAACCEPT -> new CommandTeleport((Player) commandSender, strings[1]).tpaaccept();
                    case TPAHERE -> new CommandTeleport((Player) commandSender, strings[1]).tpahere();
                    case TPAREFUSE -> new CommandTeleport((Player) commandSender, strings[1]).tparefuse();
                    case SETHOME -> new CommandHome(commandSender).setHome(strings[1]);
                    case SETWARP -> new CommandWarp(commandSender).setWarp(strings[1]);
                    case TIME -> new CommandTime((Player) commandSender).control(strings[1]);
                    case ITEMNAME -> {
                        Player player = (Player) commandSender;
                        new CommandItem(player, player.getInventory().getItemInMainHand()).changeName(strings[1]);
                    }
                }
            }
            case 3 -> {
                switch (type) {
                    case ZAKO -> {
                        Player player = (Player) commandSender;
                        new CommandZako(player).action(strings[1], strings[2]);
                    }
                    case ITEMLORE -> {
                        Player player = (Player) commandSender;
                        new CommandItem(player, player.getInventory().getItemInMainHand()).changeLore(strings[1], strings[2]);
                    }

                }
            }
        }


        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, String @NotNull [] strings) {
        if(!this.isTheInstructionCorrect(command, AriCommand.ARI)) return List.of();
        List<String> returnList = new ArrayList<>();
        //返回所有具有权限的指令给玩家
        switch (strings.length) {
            //二级子指令
            case 1 -> {
                for (AriCommand type : AriCommand.values()) {
                    if (type.getShow() == null || type.equals(AriCommand.ARI)) continue;
                    if (type.getPermission() == null) {
                        returnList.add(type.getShow());
                        continue;
                    }
                    if (PermissionUtils.hasPermission(commandSender, type.getPermission()) && type.getShow().contains(strings[0])) {
                        returnList.add(type.getShow());
                    }
                }
                return PublicFunctionUtils.filterByPrefix(returnList, strings[0]);
            }
            case 2 -> {
                AriCommand c;
                try {
                    c = AriCommand.valueOf(strings[0].toUpperCase(Locale.ROOT));
                } catch (Exception e) {
                    return List.of();
                }
                switch (c) {
                    case TPA, TPAHERE -> {
                        return PublicFunctionUtils.filterByPrefix(new CommandTeleport((Player) commandSender, strings[1]).getTabs(1), strings[1]);
                    }
                    case TPAACCEPT, TPAREFUSE -> {
                        return PublicFunctionUtils.filterByPrefix(new CommandTeleport((Player) commandSender, strings[1]).getTabs(2), strings[1]);
                    }
                    case TIME -> {
                        return PublicFunctionUtils.filterByPrefix(new CommandTime((Player) commandSender).getTabs(1), strings[1]);
                    }
                    case ITEMLORE -> {
                        Player player = (Player) commandSender;
                        return PublicFunctionUtils.filterByPrefix(new CommandItem(player, player.getInventory().getItemInMainHand()).getTabs(1), strings[1]);
                    }
                    case ZAKO -> {
                        return PublicFunctionUtils.filterByPrefix(new CommandZako(commandSender).getTabs(1), strings[1]);
                    }
                    case RTP -> {
                        return PublicFunctionUtils.filterByPrefix(new CommandRtp((Player) commandSender).getTabs(1), strings[1]);
                    }
                }
            }
            case 3 -> {
                AriCommand c;
                try {
                    c = AriCommand.valueOf(strings[0].toUpperCase(Locale.ROOT));
                } catch (Exception e) {
                    return List.of();
                }
                switch (c) {
                    case ZAKO -> {
                        if(strings[1].equals(Zako.INFO.getName())) {
                            return PublicFunctionUtils.filterByPrefix(new CommandZako(commandSender).getTabs(2), strings[2]);
                        }
                    }
                }
            }
        }
        return List.of();
    }
}
