package ari.superarilo.command.lists;

import ari.superarilo.command.function.CommandWarp;
import ari.superarilo.enumType.AriCommand;
import ari.superarilo.function.CommandCheck;
import ari.superarilo.function.impl.CommandCheckImpl;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class setwarp implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        CommandCheckImpl check = CommandCheck.create(commandSender, command, AriCommand.SETWARP);
        if (!check.isTheInstructionCorrect()) return false;
        if (check.allCheck() && strings.length == 1) {
            new CommandWarp(commandSender).setWarp(strings[0]);
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return List.of();
    }
}
