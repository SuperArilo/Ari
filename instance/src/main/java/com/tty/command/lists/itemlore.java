package com.tty.command.lists;

import com.tty.command.check.BaseCommandCheck;
import com.tty.command.function.CommandItem;
import com.tty.enumType.AriCommand;
import com.tty.lib.enum_type.CommandAction;
import com.tty.lib.tool.PublicFunctionUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class itemlore extends BaseCommandCheck implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        if (!this.isTheInstructionCorrect(command, AriCommand.ITEMLORE)) return false;
        if (this.quickCheck(commandSender, AriCommand.ITEMLORE, strings.length, 2)) {
            Player player = (Player) commandSender;
            new CommandItem(player, player.getInventory().getItemInMainHand()).changeLore(strings[0], strings[1]);
        }
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
