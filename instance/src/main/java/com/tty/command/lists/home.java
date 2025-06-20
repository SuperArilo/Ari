package com.tty.command.lists;

import com.tty.command.function.CommandHome;
import com.tty.enumType.AriCommand;
import com.tty.function.CommandCheck;
import com.tty.function.impl.CommandCheckImpl;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class home implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        CommandCheckImpl check = CommandCheck.create(commandSender, command, AriCommand.HOME);
        if (!check.isTheInstructionCorrect()) return false;
        if (check.allCheck()) {
            new CommandHome(commandSender).home();
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return List.of();
    }
}
