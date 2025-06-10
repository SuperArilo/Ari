package com.tty.command.lists;

import com.tty.command.function.CommandTime;
import com.tty.enumType.AriCommand;
import com.tty.function.CommandCheck;
import com.tty.lib.enum_type.TimePeriod;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class time implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        CommandCheck check = CommandCheck.create(commandSender, command,AriCommand.TIME);
        if (!check.isTheInstructionCorrect()) return false;
        if (check.allCheck() && strings.length == 1) {
            new CommandTime((Player) commandSender).control(strings[0]);
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(!command.getName().equalsIgnoreCase(AriCommand.TIME.getShow())) return List.of();
        List<String> list = new ArrayList<>();
        for (TimePeriod timePeriod : TimePeriod.values()) {
            list.add(timePeriod.getDescription());
        }
        return list;
    }
}
