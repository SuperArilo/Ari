package ari.superarilo.command.lists;

import ari.superarilo.Ari;
import ari.superarilo.function.CommandCheck;
import ari.superarilo.function.impl.CommandCheckImpl;
import ari.superarilo.enumType.AriCommand;
import ari.superarilo.enumType.FilePath;
import ari.superarilo.function.HomeManager;
import ari.superarilo.tool.TextTool;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class sethome implements TabExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        CommandCheckImpl check = CommandCheck.create(commandSender, command, AriCommand.SETHOME);
        if (!check.isTheInstructionCorrect()) return false;
        if (check.allCheck()) {
            if (strings.length != 1) {
                commandSender.sendMessage(TextTool.setHEXColorText("command.public.fail", FilePath.Lang));
                return true;
            }
            if(Ari.instance.formatUtil.checkIdName(strings[0])) {
                HomeManager.create((Player) commandSender).createNewHome(strings[0]);
            } else {
                commandSender.sendMessage(TextTool.setHEXColorText("command.sethome.id-error", FilePath.Lang));
            }
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return List.of();
    }
}
