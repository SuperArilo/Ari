package com.tty.command;

import com.tty.command.check.BaseCommandCheck;
import com.tty.command.function.*;
import com.tty.dto.event.CustomPluginReloadEvent;
import com.tty.enumType.AriCommand;
import com.tty.enumType.FilePath;
import com.tty.lib.enum_type.TimePeriod;
import com.tty.lib.tool.ComponentUtils;
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
            commandSender.sendMessage(ComponentUtils.text(ConfigUtils.getValue("function.unknown", FilePath.Lang)));
            return true;
        }
        switch (type) {
            case CIALLO -> new CommandCiallo(commandSender).ciallo();
            case RTP -> {
                if (this.quickCheck(commandSender, AriCommand.RTP)) break;
                new CommandRtp(commandSender).rtp();
            }
            case RELOAD -> {
                if(!this.hasPermission(commandSender, AriCommand.RELOAD)) return true;
                commandSender.sendMessage(ComponentUtils.text(ConfigUtils.getValue("function.reload.doing", FilePath.Lang)));
                Bukkit.getPluginManager().callEvent(new CustomPluginReloadEvent(commandSender));
            }
            case TP -> {
                if (!this.quickCheck(commandSender, AriCommand.TP, strings.length, 2)) break;
                new CommandTp(commandSender, strings[1]).tp();
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
                if (!this.quickCheck(commandSender, AriCommand.SETSPAWN, strings.length, 2)) break;
                new CommandWarp(commandSender).setWarp(strings[1]);
            }
            case TIME -> {
                if (!this.quickCheck(commandSender, AriCommand.TIME, strings.length, 2)) break;
                new CommandTime((Player) commandSender).control(strings[1]);
            }
            case SPAWN -> {
                if (!this.quickCheck(commandSender, AriCommand.SPAWN)) break;
                new CommandSpawn(commandSender).convey();
            }
            case SPAWNLIST -> {
                if (!this.quickCheck(commandSender, AriCommand.SPAWN)) break;
                new CommandSpawn(commandSender).openList();
            }
            case SETSPAWN -> {
                if (!this.quickCheck(commandSender, AriCommand.SETSPAWN, strings.length, 2)) break;
                new CommandSpawn(commandSender).set(strings[1]);
            }
            case ITEMNAME -> {
                if (!this.quickCheck(commandSender, AriCommand.SETSPAWN, strings.length, 2)) break;
                Player player = (Player) commandSender;
                new CommandItem(player, player.getInventory().getItemInMainHand()).changeName(strings[1]);
            }
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, String @NotNull [] strings) {
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
                if(PermissionUtils.hasPermission(commandSender, type.getPermission())) {
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
                if (PermissionUtils.hasPermission(commandSender, type.getPermission()) && type.getShow().contains(strings[0])) {
                    st.add(type.getShow());
                }
            }
            return PublicFunctionUtils.filterByPrefix(st, strings[0]);
        } else if (strings.length == 2) {
            AriCommand c;
            try {
                c = AriCommand.valueOf(strings[0].toUpperCase(Locale.ROOT));
            } catch (Exception e) {
                return List.of();
            }
            switch (c) {
                case TP -> {
                    Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
                    List<String> ps = new ArrayList<>();
                    for (Player player : onlinePlayers) {
                        String name = player.getName();
                        if (commandSender.getName().equalsIgnoreCase(name)) continue;
                        ps.add(player.getName());
                    }
                    return ps;
                }
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
                case TIME -> {
                    List<String> list = new ArrayList<>();
                    for (TimePeriod timePeriod : TimePeriod.values()) {
                        list.add(timePeriod.getDescription());
                    }
                    return PublicFunctionUtils.filterByPrefix(list, strings[1]);
                }
            }
        }
        return List.of();
    }
}
