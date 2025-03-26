package ari.superarilo.command.lists;

import ari.superarilo.command.function.CommandTeleport;
import ari.superarilo.function.CommandCheck;
import ari.superarilo.function.impl.CommandCheckImpl;
import ari.superarilo.enumType.AriCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.List;

public class tpa implements TabExecutor {
    
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        CommandCheckImpl check = CommandCheck.create(commandSender, command, AriCommand.TPA);
        if (!check.isTheInstructionCorrect()) return false;
        if (check.allCheck() && strings.length == 1) {
            new CommandTeleport(commandSender, strings[0]).tpa();
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(!command.getName().equalsIgnoreCase(AriCommand.TPA.getShow())) return List.of();
        return new CommandTeleport(commandSender, strings[0]).getOnlinePlayers(AriCommand.TPA);
    }
}
