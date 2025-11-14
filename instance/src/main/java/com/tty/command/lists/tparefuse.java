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

public class tparefuse extends BaseCommandCheck implements TabExecutor {
    
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, String @NotNull [] strings) {
        if (!this.isTheInstructionCorrect(command, AriCommand.TPAREFUSE)) return false;
        if (this.quickCheck(commandSender, AriCommand.TPAREFUSE, strings.length, 1)) {
            new CommandTeleport((Player) commandSender, strings[0]).tparefuse();
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, String @NotNull [] strings) {
        if(strings.length == 1) {
            return PublicFunctionUtils.filterByPrefix(new CommandTeleport((Player) commandSender, strings[0]).getTabs(2), strings[0]);
        }
        return List.of();
    }
}
