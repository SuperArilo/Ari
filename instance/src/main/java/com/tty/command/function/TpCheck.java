package com.tty.command.function;

import com.tty.Ari;
import com.tty.enumType.FilePath;
import com.tty.tool.TextTool;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpCheck {
    public boolean preCheck(CommandSender sender, String targetPlayerName) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(TextTool.setHEXColorText("function.public.not-player", FilePath.Lang));
            return false;
        }
        if (targetPlayerName.equals(sender.getName())) {
            sender.sendMessage(TextTool.setHEXColorText("function.public.fail", FilePath.Lang));
            return false;
        }
        Player player = Ari.instance.getServer().getPlayerExact(targetPlayerName);
        if(player == null) {
            sender.sendMessage(TextTool.setHEXColorText("teleport.unable-player", FilePath.Lang));
            return false;
        }
        return true;
    }
}

