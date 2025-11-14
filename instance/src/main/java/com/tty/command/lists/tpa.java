package com.tty.command.lists;

import com.tty.command.check.BaseCommandCheck;
import com.tty.command.function.CommandTeleport;
import com.tty.enumType.AriCommand;
import com.tty.lib.tool.PublicFunctionUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class tpa extends BaseCommandCheck implements TabExecutor {
    
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, String @NotNull [] strings) {
        if (!this.isTheInstructionCorrect(command, AriCommand.TPA)) return false;
        if (this.quickCheck(commandSender, AriCommand.TPA, strings.length, 1)) {
            new CommandTeleport((Player) commandSender, strings[0]).tpa();
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, String @NotNull [] strings) {
        if(strings.length == 1) {
            return PublicFunctionUtils.filterByPrefix(new CommandTeleport((Player) commandSender, strings[0]).getTabs(1), strings[0]);
        }
        return List.of();
    }
}
