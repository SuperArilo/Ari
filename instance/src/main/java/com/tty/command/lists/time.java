package com.tty.command.lists;

import com.tty.command.check.BaseCommandCheck;
import com.tty.command.function.CommandTime;
import com.tty.enumType.AriCommand;
import com.tty.lib.tool.PublicFunctionUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class time extends BaseCommandCheck implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, String @NotNull [] strings) {
        if (!this.isTheInstructionCorrect(command, AriCommand.TIME)) return false;
        if (this.quickCheck(commandSender, AriCommand.TIME, strings.length, 1)) {
            new CommandTime((Player) commandSender).control(strings[0]);
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, String @NotNull [] strings) {
        if (strings.length == 1) {
            return PublicFunctionUtils.filterByPrefix(new CommandTime((Player) commandSender).getTabs(1), strings[0]);
        }
        return List.of();
    }
}
