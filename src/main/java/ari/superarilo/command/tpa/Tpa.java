package ari.superarilo.command.tpa;

import ari.superarilo.SuperArilo;
import ari.superarilo.tool.TextTool;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Tpa implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(!(commandSender instanceof Player)) {
            commandSender.sendMessage(TextTool.setGradientText("", null, null));
            return true;
        }
        if(!s.equals("tpa")) return false;
        commandSender.sendMessage(TextTool.setGradientText("成功1145141919810！", "#114514", "#ABCEDF"));
        return true;
    }
}
