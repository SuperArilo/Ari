package ari.superarilo.command.lists;

import ari.superarilo.function.CommandCheck;
import ari.superarilo.function.impl.CommandCheckImpl;
import ari.superarilo.enumType.AriCommand;
import ari.superarilo.gui.home.HomeList;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class home implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        CommandCheckImpl check = CommandCheck.create(commandSender, command, AriCommand.HOME);
        if (!check.isTheInstructionCorrect()) return false;
        if (check.allCheck()) {
            new HomeList((Player) commandSender).open();
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return List.of();
    }
}
