package ari.superarilo.command;

import ari.superarilo.tool.ConfigFiles;
import ari.superarilo.tool.TextTool;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Reload implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!command.getName().equalsIgnoreCase("reload")) return false;
        if (commandSender.isOp()) {
            commandSender.sendMessage(TextTool.setHEXColorText(ConfigFiles.configs.get("lang").getString("command.reload.doing","null")));
            ConfigFiles.reloadAllConfig();
            commandSender.sendMessage(TextTool.setHEXColorText(ConfigFiles.configs.get("lang").getString("command.reload.success","null")));
            return true;
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!command.getName().equalsIgnoreCase("reload")) return null;
        if (commandSender.isOp()) {
            if (strings.length == 0) return null;
        }
        return null;
    }
}
