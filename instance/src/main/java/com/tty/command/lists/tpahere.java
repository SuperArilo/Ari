package com.tty.command.lists;

import com.tty.command.check.BaseCommandCheck;
import com.tty.command.function.CommandTeleport;
import com.tty.enumType.AriCommand;
import com.tty.enumType.FilePath;
import com.tty.tool.TextTool;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class tpahere extends BaseCommandCheck implements TabExecutor {
    
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, String @NotNull [] strings) {
        if (!this.isTheInstructionCorrect(command, AriCommand.TPAHERE)) return false;
        if (this.quickCheck(commandSender, AriCommand.TPAHERE) && strings.length == 1) {
            new CommandTeleport(commandSender, strings[0]).tpahere();
        } else {
            commandSender.sendMessage(TextTool.setHEXColorText("function.public.fail", FilePath.Lang));
        }
        return true;
    }
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, String @NotNull [] strings) {
        if(!command.getName().equalsIgnoreCase(AriCommand.TPAHERE.getShow())) return List.of();
        return new CommandTeleport(commandSender, strings[0]).getOnlinePlayers(AriCommand.TPAHERE);
    }


}
