package com.tty.command.lists;

import com.tty.command.function.CommandSpawn;
import com.tty.enumType.AriCommand;
import com.tty.function.CommandCheck;
import com.tty.function.impl.CommandCheckImpl;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class setspawn implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        CommandCheckImpl commandCheck = CommandCheck.create(sender, command, AriCommand.SETSPAWN);
        if (!commandCheck.isTheInstructionCorrect()) return false;
        if (commandCheck.allCheck()) {
            Player player = (Player) sender;
            Location location = player.getLocation();
            new CommandSpawn(sender).set(location);
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        return List.of();
    }
}
