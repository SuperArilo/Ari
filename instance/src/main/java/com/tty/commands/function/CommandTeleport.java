package com.tty.commands.function;

import com.tty.Ari;
import com.tty.dto.TeleportStatus;
import com.tty.enumType.FilePath;
import com.tty.commands.check.TeleportCheck;
import com.tty.lib.enum_type.LangType;
import com.tty.lib.enum_type.TeleportType;
import com.tty.tool.ConfigUtils;
import com.tty.function.Teleport;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Objects;

public class CommandTeleport extends TeleportCheck  {

    private final Player sender;
    private final String playerName;

    public CommandTeleport(Player sender, String playerName) {
        this.sender = sender;
        this.playerName = playerName;
    }

    public void tpa() {
        if(!preCheck(this.sender, this.playerName)) return;
        TeleportCheck.preCheckStatus(this.sender, Bukkit.getPlayerExact(this.playerName), "tpa");
    }

    public void tpaaccept() {
        if(!preCheck(this.sender, this.playerName)) return;
        Player player = Bukkit.getPlayerExact(this.playerName);
        TeleportStatus status = TeleportCheck.checkHaveTeleportStatus(player, this.sender);
        if(status == null) {
            this.sender.sendMessage(ConfigUtils.t("function.tpa.been-done"));
            return;
        }
        //请求成功，移除该请求
        this.sender.sendMessage(ConfigUtils.t("function.tpa.agree"));
        TeleportCheck.remove(player, null,TeleportType.PLAYER);
        Integer value = Ari.C_INSTANCE.getValue("main.teleport.delay", FilePath.TPA, Integer.class, 3);

        Teleport teleport = null;
        if (status.getCommandString().equals("tpa")) {
            teleport = Teleport.create(player, this.sender.getLocation(), value);
        } else if (status.getCommandString().equals("tpahere")) {
            teleport = Teleport.create(this.sender, Objects.requireNonNull(player).getLocation(), value);
        }
        if (teleport != null) {
            teleport.teleport();
        } else {
            this.sender.sendMessage(ConfigUtils.t("function.tpa.error"));
        }
    }

    public void tparefuse() {
        if(!preCheck(this.sender, this.playerName)) return;
        Player player = Bukkit.getPlayerExact(this.playerName);
        if(player == null) {
            this.sender.sendMessage(ConfigUtils.t("teleport.unable-player"));
            return;
        }
        if (TeleportCheck.checkHaveTeleportStatus(player, this.sender) == null) {
            this.sender.sendMessage(ConfigUtils.t("function.tpa.been-done"));
        } else {
            if(TeleportCheck.remove(player, null,TeleportType.PLAYER)) {
                this.sender.sendMessage(ConfigUtils.t("function.tpa.refuse-success"));
                player.sendMessage(ConfigUtils.t("function.tpa.refused", LangType.TPABESENDER.getType(), this.sender.getName()));
            } else {
                this.sender.sendMessage(ConfigUtils.t("function.public.break"));
            }
        }
    }

    public void tpahere() {
        if(!preCheck(this.sender, this.playerName)) return;
        TeleportCheck.preCheckStatus(this.sender, Bukkit.getPlayerExact(this.playerName), "tpahere");
    }

}
