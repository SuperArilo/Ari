package com.tty.command.lists;

import com.tty.command.check.BaseCommandCheck;
import com.tty.command.function.CommandZako;
import com.tty.enumType.AriCommand;
import com.tty.lib.enum_type.CommandAction;
import com.tty.lib.tool.PublicFunctionUtils;
import com.tty.tool.ConfigUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class zako extends BaseCommandCheck implements TabExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        if (!this.isTheInstructionCorrect(command, AriCommand.ZAKO)) return false;
        //需要单独判断
        if(!this.hasPermission(commandSender, AriCommand.ZAKO)) return true;
        if(strings.length != 2) {
            commandSender.sendMessage(ConfigUtils.t("function.public.fail"));
            return true;
        }
        new CommandZako(commandSender).action(strings[0], strings[1]);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        if (strings.length != 1) return List.of();
        List<String> list = new ArrayList<>();
        for (CommandAction value : CommandAction.values()) {
            list.add(value.getName());
        }
        return PublicFunctionUtils.filterByPrefix(list, strings[0]);
    }
}
