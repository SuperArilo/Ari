package com.tty.command.lists;

import com.tty.command.check.BaseCommandCheck;
import com.tty.command.function.CommandZako;
import com.tty.enumType.AriCommand;
import com.tty.enumType.commands.Zako;
import com.tty.lib.tool.PublicFunctionUtils;
import com.tty.tool.ConfigUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class zako extends BaseCommandCheck implements TabExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        if (!this.isTheInstructionCorrect(command, AriCommand.ZAKO)) return false;
        //需要单独判断
        if(!this.hasPermission(commandSender, AriCommand.ZAKO)) return true;
        if(strings.length != 2) {
            commandSender.sendMessage(ConfigUtils.t("function.public.fail"));
            return true;
        }
        new CommandZako(commandSender).action(strings[0], strings[1]);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        if (strings.length == 0) return List.of();
        List<String> list = new ArrayList<>();
        if (strings.length == 1) {
            for (Zako value : Zako.values()) {
                list.add(value.getName());
            }
            return PublicFunctionUtils.filterByPrefix(list, strings[0]);
        } else if (strings.length == 2 && strings[0].equals(Zako.INFO.getName())) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                list.add(player.getName());
            }
            return PublicFunctionUtils.filterByPrefix(list, strings[1]);
        } else  {
            return List.of();
        }
    }
}
