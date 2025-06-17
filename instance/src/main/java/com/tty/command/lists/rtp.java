package com.tty.command.lists;

import com.tty.command.function.CommandRtp;
import com.tty.enumType.AriCommand;
import com.tty.function.CommandCheck;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class rtp implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        CommandCheck check = CommandCheck.create(commandSender, command, AriCommand.RTP);
        if (!check.isTheInstructionCorrect()) return false;
        if (check.allCheck()) {
            new CommandRtp(commandSender).rtp();
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        return List.of();
    }
}
