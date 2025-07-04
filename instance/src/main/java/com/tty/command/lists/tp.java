package com.tty.command.lists;

import com.tty.command.check.BaseCommandCheck;
import com.tty.command.function.CommandTp;
import com.tty.enumType.AriCommand;
import com.tty.enumType.FilePath;
import com.tty.lib.tool.ComponentUtils;
import com.tty.tool.ConfigUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class tp extends BaseCommandCheck implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, String @NotNull [] strings) {
        if (!this.isTheInstructionCorrect(command, AriCommand.TP)) return false;
        if (this.quickCheck(commandSender, AriCommand.TP) && strings.length == 1) {
            new CommandTp(commandSender, strings[0]).tp();
        } else {
            commandSender.sendMessage(ComponentUtils.text(ConfigUtils.getValue("function.public.fail", FilePath.Lang)));
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, String @NotNull [] strings) {
        if (!command.getName().equalsIgnoreCase(AriCommand.TP.getShow())) return List.of();
        return Bukkit.getServer().getOnlinePlayers()
                .stream()
                .map(Player::getName)
                .filter(name -> !name.equals(commandSender.getName()))
                .toList();
    }
}
