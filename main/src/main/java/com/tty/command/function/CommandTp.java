package com.tty.command.function;

import com.tty.Ari;
import com.tty.enumType.FilePath;
import com.tty.lib.EntityTeleport;
import com.tty.lib.Lib;
import com.tty.tool.TextTool;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandTp extends TpCheck {

    private final CommandSender sender;
    private final String targetPlayerName;

    public CommandTp(CommandSender sender, String targetPlayerName) {
        this.sender = sender;
        this.targetPlayerName = targetPlayerName;
    }

    public void tp() {
        if (!this.preCheck(this.sender, this.targetPlayerName)) return;
        Lib.Scheduler.runAtEntity(
                Ari.instance,
                (Player) this.sender,
                i-> {
                    EntityTeleport.teleport((Player) this.sender, Bukkit.getPlayer(this.targetPlayerName));
                    this.sender.sendMessage(TextTool.setHEXColorText(Ari.instance.configManager.getValue("teleport.success", FilePath.Lang, String.class)));
                },
                () -> this.sender.sendMessage(TextTool.setHEXColorText(Ari.instance.configManager.getValue("base.on-error", FilePath.Lang, String.class))));
    }

}
