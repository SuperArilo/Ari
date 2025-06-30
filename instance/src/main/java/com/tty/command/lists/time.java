package com.tty.command.lists;

import com.tty.command.check.BaseCommandCheck;
import com.tty.command.function.CommandTime;
import com.tty.enumType.AriCommand;
import com.tty.enumType.FilePath;
import com.tty.lib.enum_type.TimePeriod;
import com.tty.lib.tool.PublicFunctionUtils;
import com.tty.tool.TextTool;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class time extends BaseCommandCheck implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, String @NotNull [] strings) {
        if (!this.isTheInstructionCorrect(command, AriCommand.TIME)) return false;
        if (this.quickCheck(commandSender, AriCommand.TIME) && strings.length == 1) {
            new CommandTime((Player) commandSender).control(strings[0]);
        } else {
            commandSender.sendMessage(TextTool.setHEXColorText("function.public.fail", FilePath.Lang));
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, String @NotNull [] strings) {
        if(!command.getName().equalsIgnoreCase(AriCommand.TIME.getShow())) return List.of();
        List<String> list = new ArrayList<>();
        for (TimePeriod timePeriod : TimePeriod.values()) {
            list.add(timePeriod.getDescription());
        }
        return PublicFunctionUtils.filterByPrefix(list, strings[0]);
    }
}
