package com.tty.command;

import com.tty.command.check.BaseCommandCheck;
import com.tty.command.function.*;
import com.tty.dto.event.CustomPluginReloadEvent;
import com.tty.enumType.AriCommand;
import com.tty.enumType.commands.Zako;
import com.tty.lib.enum_type.CommandAction;
import com.tty.lib.enum_type.TimePeriod;
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
        switch (type) {
            case RTP -> {
                if (!this.quickCheck(commandSender, AriCommand.RTP)) break;
                new CommandRtp(commandSender).rtp();
            }
            case RELOAD -> {
                if(!this.hasPermission(commandSender, AriCommand.RELOAD)) return true;
                commandSender.sendMessage(ConfigUtils.t("function.reload.doing"));
                Bukkit.getPluginManager().callEvent(new CustomPluginReloadEvent(commandSender));
            }
            case TPA -> {
                if (!this.quickCheck(commandSender, AriCommand.TPA, strings.length, 2)) break;
                new CommandTeleport(commandSender, strings[1]).tpa();
            }
            case TPAACCEPT -> {
                if (!this.quickCheck(commandSender, AriCommand.TPAACCEPT, strings.length, 2)) break;
                new CommandTeleport(commandSender, strings[1]).tpaaccept();
            }
            case TPAHERE -> {
                if (!this.quickCheck(commandSender, AriCommand.TPAHERE, strings.length, 2)) break;
                new CommandTeleport(commandSender, strings[1]).tpahere();
            }
            case TPAREFUSE -> {
                if (!this.quickCheck(commandSender, AriCommand.TPAREFUSE, strings.length, 2)) break;
                new CommandTeleport(commandSender, strings[1]).tparefuse();
            }
            case HOME -> {
                if (!this.quickCheck(commandSender, AriCommand.HOME)) break;
                new CommandHome(commandSender).home();
            }
            case SETHOME -> {
                if (!this.quickCheck(commandSender, AriCommand.SETHOME, strings.length, 2)) break;
                new CommandHome(commandSender).setHome(strings[1]);
            }
            case BACK -> {
                if(!this.quickCheck(commandSender, AriCommand.BACK)) break;
                new CommandBack(commandSender).startDo();
            }
            case WARP -> {
                if (!this.quickCheck(commandSender, AriCommand.WARP)) break;
                new CommandWarp(commandSender).warp();
            }
            case SETWARP -> {
                if (!this.quickCheck(commandSender, AriCommand.SETWARP, strings.length, 2)) break;
                new CommandWarp(commandSender).setWarp(strings[1]);
            }
            case TIME -> {
                if (!this.quickCheck(commandSender, AriCommand.TIME, strings.length, 2)) break;
                new CommandTime((Player) commandSender).control(strings[1]);
            }
            case SPAWN -> {
                if (!this.quickCheck(commandSender, AriCommand.SPAWN)) break;
                new CommandSpawn((Player) commandSender).convey();
            }
            case SETSPAWN -> {
                if (!this.quickCheck(commandSender, AriCommand.SETSPAWN, strings.length, 2)) break;
                new CommandSpawn((Player) commandSender).set();
            }
            case ITEMNAME -> {
                if (!this.quickCheck(commandSender, AriCommand.ITEMNAME, strings.length, 2)) break;
                Player player = (Player) commandSender;
                new CommandItem(player, player.getInventory().getItemInMainHand()).changeName(strings[1]);
            }
            case ITEMLORE -> {
                if (!this.quickCheck(commandSender, AriCommand.ITEMLORE, strings.length, 3)) break;
                Player player = (Player) commandSender;
                new CommandItem(player, player.getInventory().getItemInMainHand()).changeLore(strings[1], strings[2]);
            }
            case ZAKO -> {
                if (!this.quickCheck(commandSender, AriCommand.ZAKO, strings.length, 3)) break;
                Player player = (Player) commandSender;
                new CommandZako(player).action(strings[1], strings[2]);
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
                    case TPA -> {
                        return CommandTeleport.getOnlinePlayers((Player) commandSender, AriCommand.TPA);
                    }
                    case TPAHERE -> {
                        return CommandTeleport.getOnlinePlayers((Player) commandSender, AriCommand.TPAHERE);
                    }
                    case TPAACCEPT -> {
                        return CommandTeleport.getHasRequestPlayers((Player) commandSender, AriCommand.TPAACCEPT);
                    }
                    case TPAREFUSE -> {
                        return CommandTeleport.getHasRequestPlayers((Player) commandSender, AriCommand.TPAREFUSE);
                    }
                    case TIME -> {
                        for (TimePeriod timePeriod : TimePeriod.values()) {
                            returnList.add(timePeriod.getDescription());
                        }
                        return PublicFunctionUtils.filterByPrefix(returnList, strings[1]);
                    }
                    case ITEMLORE -> {
                        for (CommandAction value : CommandAction.values()) {
                            returnList.add(value.getName());
                        }
                        return PublicFunctionUtils.filterByPrefix(returnList, strings[1]);
                    }
                    case ZAKO -> {
                        for (Zako value : Zako.values()) {
                            returnList.add(value.getName());
                        }
                        return PublicFunctionUtils.filterByPrefix(returnList, strings[1]);
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
                            for (Player player : Bukkit.getOnlinePlayers()) {
                                returnList.add(player.getName());
                            }
                            return PublicFunctionUtils.filterByPrefix(returnList, strings[2]);
                        }
                    }
                }
            }
        }
        return List.of();
    }
}
