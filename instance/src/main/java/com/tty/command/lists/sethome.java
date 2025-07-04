package com.tty.command.lists;

import com.tty.command.check.BaseCommandCheck;
import com.tty.command.function.CommandHome;
import com.tty.enumType.AriCommand;
import com.tty.enumType.FilePath;
import com.tty.lib.tool.ComponentUtils;
import com.tty.tool.ConfigUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class sethome extends BaseCommandCheck implements TabExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, String @NotNull [] strings) {
        if (!this.isTheInstructionCorrect(command, AriCommand.SETHOME)) return false;
        if (this.quickCheck(commandSender, AriCommand.SETHOME) && strings.length == 1) {
            new CommandHome(commandSender).setHome(strings[0]);
        } else {
            commandSender.sendMessage(ComponentUtils.text(ConfigUtils.getValue("function.public.fail", FilePath.Lang)));
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, String @NotNull [] strings) {
        return List.of();
    }
}
