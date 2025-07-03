package com.tty.command.function;

import com.tty.Ari;
import com.tty.command.check.TeleportCheck;
import com.tty.enumType.FilePath;
import com.tty.lib.Lib;
import com.tty.function.Teleport;
import com.tty.tool.TextTool;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandTp {

    private final CommandSender sender;
    private final String targetPlayerName;

    public CommandTp(CommandSender sender, String targetPlayerName) {
        this.sender = sender;
        this.targetPlayerName = targetPlayerName;
    }

    public void tp() {
        if (!TeleportCheck.preCheck(this.sender, this.targetPlayerName)) return;
        Player targetPlayer = Bukkit.getPlayer(this.targetPlayerName);
        if (targetPlayer == null) return;
        Lib.Scheduler.runAtEntity(
                Ari.instance,
                (Player) this.sender,
                i-> Teleport.create((Player) this.sender, targetPlayer.getLocation(), 1).teleport(),
                () -> this.sender.sendMessage(TextTool.setHEXColorText("base.on-error", FilePath.Lang)));
    }
}
