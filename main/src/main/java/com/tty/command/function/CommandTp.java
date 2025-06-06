package com.tty.command.function;

import com.tty.Ari;
import com.tty.enumType.FilePath;
import com.tty.function.TeleportThread;
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
        Player targetPlayer = Bukkit.getPlayer(this.targetPlayerName);
        if (targetPlayer == null) return;
        Lib.Scheduler.runAtEntity(
                Ari.instance,
                (Player) this.sender,
                i-> {
                    TeleportThread.playerToPlayer((Player) this.sender, targetPlayer).teleport(0);
//                    EntityTeleport.teleport((Player) this.sender, targetPlayer.getLocation());
//                    this.sender.sendMessage(TextTool.setHEXColorText(Ari.instance.configManager.getValue("teleport.success", FilePath.Lang, String.class)));
                },
                () -> this.sender.sendMessage(TextTool.setHEXColorText(Ari.instance.configManager.getValue("base.on-error", FilePath.Lang, String.class))));
    }

}
