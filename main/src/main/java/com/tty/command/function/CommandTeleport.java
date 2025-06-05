package com.tty.command.function;

import com.tty.Ari;
import com.tty.entity.TeleportStatus;
import com.tty.enumType.AriCommand;
import com.tty.enumType.FilePath;
import com.tty.enumType.LangType;
import com.tty.enumType.TeleportType;
import com.tty.function.TeleportCheck;
import com.tty.function.TeleportThread;
import com.tty.tool.TextTool;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CommandTeleport {

    private final CommandSender sender;
    private final String playerName;

    public CommandTeleport(CommandSender sender, String playerName) {
        this.sender = sender;
        this.playerName = playerName;
    }

    public void tpa() {
        if(!this.preCheck()) return;
        TeleportCheck.create().preCheckStatus((Player) this.sender, Bukkit.getPlayerExact(this.playerName), AriCommand.TPA);
    }

    public void tpaaccept() {
        if(!this.preCheck()) return;
        Player player = Bukkit.getPlayerExact(this.playerName);
        TeleportStatus status = TeleportCheck.create().checkHaveTeleportStatus(player, (Player) this.sender);
        if(status == null) {
            this.sender.sendMessage(TextTool.setHEXColorText("function.tpa.been-done", FilePath.Lang));
            return;
        }
        //请求成功，移除该请求
        this.sender.sendMessage(TextTool.setHEXColorText("function.tpa.agree", FilePath.Lang));
        Ari.instance.tpStatusValue.remove(player, TeleportType.PLAYER);
        TeleportThread teleportThread = switch (status.getAriCommand()) {
            case TPA -> TeleportThread.playerToPlayer(player, ((Player) this.sender));
            case TPAHERE -> TeleportThread.playerToPlayer((Player) this.sender, player);
            default -> null;
        };
        if (teleportThread != null) {
            teleportThread.teleport(Ari.instance.configManager.getValue("main.teleport.delay", FilePath.TPA, Integer.class));
        } else {
            this.sender.sendMessage(TextTool.setHEXColorText("function.tpa.error", FilePath.Lang));
        }
    }

    public void tparefuse() {
        if(!this.preCheck()) return;
        Player player = Bukkit.getPlayerExact(this.playerName);
        if(player == null) {
            this.sender.sendMessage(TextTool.setHEXColorText("teleport.unable-player", FilePath.Lang));
            return;
        }
        if (TeleportCheck.create().checkHaveTeleportStatus(player, (Player) this.sender) == null) {
            this.sender.sendMessage(TextTool.setHEXColorText("function.tpa.been-done", FilePath.Lang));
        } else {
            if(Ari.instance.tpStatusValue.remove(player, TeleportType.PLAYER)) {
                this.sender.sendMessage(TextTool.setHEXColorText("function.tpa.refuse-success", FilePath.Lang));
                if(Ari.instance.configManager.getValue("function.tpa.refused", FilePath.Lang, String.class) instanceof String message) {
                    player.sendMessage(TextTool.setHEXColorText(message.replace(LangType.TPABESENDER.getType(), this.sender.getName())));
                }
            } else {
                this.sender.sendMessage(TextTool.setHEXColorText("function.public.break", FilePath.Lang));
            }
        }
    }

    public void tpahere() {
        if(!preCheck()) return;
        TeleportCheck.create().preCheckStatus((Player) this.sender, Bukkit.getPlayerExact(this.playerName), AriCommand.TPAHERE);
    }

    public List<String> getOnlinePlayers(AriCommand ariCommand) {
        if (this.sender instanceof Player && Ari.instance.permissionUtils.hasPermission(this.sender, ariCommand.getPermission())) {
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
        if(this.sender instanceof Player && Ari.instance.permissionUtils.hasPermission(this.sender, ariCommand.getPermission())) {
            List<String> players = new ArrayList<>();
            Server server = Ari.instance.getServer();
            Ari.instance.tpStatusValue.getStatusList().stream().filter(obj ->
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

    private boolean preCheck() {
        if(!(this.sender instanceof Player)) {
            this.sender.sendMessage(TextTool.setHEXColorText("function.public.not-player", FilePath.Lang));
            return false;
        }
        if (this.playerName.equals(this.sender.getName())) {
            this.sender.sendMessage(TextTool.setHEXColorText("function.public.fail", FilePath.Lang));
            return false;
        }
        Player player = Ari.instance.getServer().getPlayerExact(this.playerName);
        if(player == null) {
            this.sender.sendMessage(TextTool.setHEXColorText("teleport.unable-player", FilePath.Lang));
            return false;
        }
        return true;
    }
}
