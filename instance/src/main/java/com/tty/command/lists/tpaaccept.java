package com.tty.command.lists;

import com.tty.command.function.CommandTeleport;
import com.tty.function.CommandCheck;
import com.tty.function.impl.CommandCheckImpl;
import com.tty.enumType.AriCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;


public class tpaaccept implements TabExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        CommandCheckImpl check = CommandCheck.create(commandSender, command, AriCommand.TPAACCEPT);
        if (!check.isTheInstructionCorrect()) return false;
        if (check.allCheck() && strings.length == 1) {
            new CommandTeleport(commandSender, strings[0]).tpaaccept();
        }

        return true;
    }
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(!command.getName().equalsIgnoreCase(AriCommand.TPAACCEPT.getShow())) return List.of();
        return new CommandTeleport(commandSender, strings[0]).getHasRequestPlayers(AriCommand.TPA);
    }
}
