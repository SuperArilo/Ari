package com.tty.command.lists;

import com.tty.command.function.CommandTp;
import com.tty.enumType.AriCommand;
import com.tty.function.CommandCheck;
import com.tty.function.impl.CommandCheckImpl;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class tp implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        CommandCheckImpl commandCheck = CommandCheck.create(commandSender, command, AriCommand.TP);
        if (!commandCheck.isTheInstructionCorrect()) return false;
        if (commandCheck.allCheck() && strings.length == 1) {
            new CommandTp(commandSender, strings[0]).tp();
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!command.getName().equalsIgnoreCase(AriCommand.TP.getShow())) return List.of();
        Collection<? extends Player> onlinePlayers = Bukkit.getServer().getOnlinePlayers();
        List<String> list = new ArrayList<>();
        for (Player player : onlinePlayers) {
            String name = player.getName();
            if (name.equals(commandSender.getName())) continue;
            list.add(player.getName());
        }
        return list;
    }
}
