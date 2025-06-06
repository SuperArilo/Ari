package com.tty.command.lists;

import com.tty.command.function.CommandHome;
import com.tty.enumType.AriCommand;
import com.tty.function.CommandCheck;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class deletehome implements TabExecutor {


    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        CommandCheck check = CommandCheck.create(commandSender, command, AriCommand.DELETEHOME);
        if (!check.isTheInstructionCorrect()) return false;
        if (check.allCheck() && strings.length == 1) {
            new CommandHome(commandSender).deleteHome(strings[0]);
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
//        if(!command.getName().equalsIgnoreCase(AriCommand.DELETEHOME.getShow())) return List.of();
//        return new CommandHome(commandSender).getHomeList();
        return List.of();
    }
}
