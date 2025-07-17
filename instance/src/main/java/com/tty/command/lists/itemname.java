package com.tty.command.lists;

import com.tty.command.check.BaseCommandCheck;
import com.tty.command.function.CommandItem;
import com.tty.enumType.AriCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class itemname extends BaseCommandCheck implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        if (!this.isTheInstructionCorrect(command, AriCommand.ITEMNAME)) return false;
        if (this.quickCheck(commandSender, AriCommand.ITEMNAME, strings, 1)) {
            Player player = (Player) commandSender;
            new CommandItem(player, player.getInventory().getItemInMainHand()).changeName(strings[0]);
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        return List.of();
    }
}
