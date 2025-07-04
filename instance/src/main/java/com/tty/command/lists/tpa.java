package com.tty.command.lists;

import com.tty.command.check.BaseCommandCheck;
import com.tty.command.function.CommandTeleport;
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

public class tpa extends BaseCommandCheck implements TabExecutor {
    
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, String @NotNull [] strings) {
        if (!this.isTheInstructionCorrect(command, AriCommand.TPA)) return false;
        if (this.quickCheck(commandSender, AriCommand.TPA) && strings.length == 1) {
            new CommandTeleport(commandSender, strings[0]).tpa();
        } else {
            commandSender.sendMessage(ComponentUtils.text(ConfigUtils.getValue("function.public.fail", FilePath.Lang)));
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, String @NotNull [] strings) {
        if(!command.getName().equalsIgnoreCase(AriCommand.TPA.getShow())) return List.of();
        return new CommandTeleport(commandSender, strings[0]).getOnlinePlayers(AriCommand.TPA);
    }
}
