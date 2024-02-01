package ari.superarilo.command.tpa;

import ari.superarilo.SuperArilo;
import ari.superarilo.tool.ConfigFiles;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.List;

public class Tpa implements TabExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!command.getName().equalsIgnoreCase("tpa")) return false;
        if(!(commandSender instanceof Player)) {
//            commandSender.sendMessage(TextTool.setGradientText("", null, null));
            return true;
        }
        if(commandSender.hasPermission("ari.command.tpa")) {
            commandSender.sendMessage(ConfigFiles.configs.get("lang").getString("command.tpa.send-message", "null"));
            return true;
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(!command.getName().equalsIgnoreCase("tpa")) return null;
        if (commandSender instanceof Player && commandSender.hasPermission("ari.command.tpa")) {
            List<String> players = new ArrayList<>();
            SuperArilo.instance.getServer().getOnlinePlayers().forEach(e -> {
                if(commandSender.getName().equals(e.getName())) return;
                players.add(e.getName());
            });
            return players;
        }
        return null;
    }
}
