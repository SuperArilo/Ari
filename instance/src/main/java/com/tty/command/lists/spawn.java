package com.tty.command.lists;

import com.tty.command.function.CommandSpawn;
import com.tty.enumType.AriCommand;
import com.tty.function.CommandCheck;
import com.tty.function.impl.CommandCheckImpl;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class spawn implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        CommandCheckImpl commandCheck = CommandCheck.create(sender, command, AriCommand.SPAWN);
        if (!commandCheck.isTheInstructionCorrect()) return false;
        if (commandCheck.allCheck()) {
            new CommandSpawn(sender).convey();
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        return List.of();
    }
}
