package com.tty.command.function;

import com.tty.Ari;
import com.tty.dto.TeleportStatus;
import com.tty.enumType.AriCommand;
import com.tty.enumType.FilePath;
import com.tty.command.check.TeleportCheck;
import com.tty.lib.enum_type.LangType;
import com.tty.lib.enum_type.TeleportType;
import com.tty.tool.ConfigUtils;
import com.tty.tool.PermissionUtils;
import com.tty.function.Teleport;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CommandTeleport extends TeleportCheck {

    private final CommandSender sender;
    private final String playerName;

    public CommandTeleport(CommandSender sender, String playerName) {
        this.sender = sender;
        this.playerName = playerName;
    }

    public void tpa() {
        if(!this.preCheck(this.sender, this.playerName)) return;
        TeleportCheck.preCheckStatus((Player) this.sender, Bukkit.getPlayerExact(this.playerName), AriCommand.TPA);
    }

    public void tpaaccept() {
        if(!this.preCheck(this.sender, this.playerName)) return;
        Player player = Bukkit.getPlayerExact(this.playerName);
        TeleportStatus status = TeleportCheck.checkHaveTeleportStatus(player, (Player) this.sender);
        if(status == null) {
            this.sender.sendMessage(ConfigUtils.t("function.tpa.been-done"));
            return;
        }
        //请求成功，移除该请求
        this.sender.sendMessage(ConfigUtils.t("function.tpa.agree"));
        TeleportCheck.remove(player, null,TeleportType.PLAYER);
        Integer value = ConfigUtils.getValue("main.teleport.delay", FilePath.TPA, Integer.class, 3);
        Teleport teleport = switch (status.getAriCommand()) {
            case TPA -> Teleport.create(player, ((Player) this.sender).getLocation(), value);
            case TPAHERE -> Teleport.create((Player) this.sender, Objects.requireNonNull(player).getLocation(), value);
            default -> null;
        };
        if (teleport != null) {
            teleport.teleport();
        } else {
            this.sender.sendMessage(ConfigUtils.t("function.tpa.error"));
        }
    }

    public void tparefuse() {
        if(!this.preCheck(this.sender, this.playerName)) return;
        Player player = Bukkit.getPlayerExact(this.playerName);
        if(player == null) {
            this.sender.sendMessage(ConfigUtils.t("teleport.unable-player"));
            return;
        }
        if (TeleportCheck.checkHaveTeleportStatus(player, (Player) this.sender) == null) {
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
        TeleportCheck.preCheckStatus((Player) this.sender, Bukkit.getPlayerExact(this.playerName), AriCommand.TPAHERE);
    }

    public List<String> getOnlinePlayers(AriCommand ariCommand) {
        if (this.sender instanceof Player && PermissionUtils.hasPermission(this.sender, ariCommand.getPermission())) {
            List<String> players = new ArrayList<>();
            Ari.instance.getServer().getOnlinePlayers().forEach(e -> {
                if(this.sender.getName().equals(e.getName())) return;
                players.add(e.getName());
            });
            return players;
        }
        return List.of();
    }

    public List<String> getHasRequestPlayers(AriCommand ariCommand) {
        if(this.sender instanceof Player && PermissionUtils.hasPermission(this.sender, ariCommand.getPermission())) {
            List<String> players = new ArrayList<>();
            Server server = Ari.instance.getServer();
            TeleportCheck.TELEPORT_STATUS.stream().filter(obj ->
                            obj.getBePlayerUUID().equals(((Player) this.sender).getUniqueId()) && obj.getType().equals(TeleportType.PLAYER))
                    .forEach(e -> {
                        Player p = server.getPlayer(e.getPlayUUID());
                        if (p != null) {
                            players.add(p.getName());
                        }
                    });
            return players;
        }
        return List.of();
    }

}
