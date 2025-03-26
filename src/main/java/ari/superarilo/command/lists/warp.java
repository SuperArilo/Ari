package ari.superarilo.command.lists;

import ari.superarilo.command.function.CommandWarp;
import ari.superarilo.enumType.AriCommand;
import ari.superarilo.function.CommandCheck;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class warp  implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        CommandCheck check = CommandCheck.create(commandSender, command, AriCommand.WARP);
        if(!check.isTheInstructionCorrect()) return false;
        if(check.allCheck()) {
            new CommandWarp(commandSender).warp();
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return List.of();
    }
}
