package ari.superarilo.command.function.impl;

import ari.superarilo.Ari;
import ari.superarilo.command.function.CommandTeleport;
import ari.superarilo.entity.TeleportStatus;
import ari.superarilo.enumType.AriCommand;
import ari.superarilo.enumType.FilePath;
import ari.superarilo.enumType.TeleportObjectType;
import ari.superarilo.enumType.TeleportType;
import ari.superarilo.function.TeleportPrecondition;
import ari.superarilo.function.TeleportThread;
import ari.superarilo.tool.TextTool;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CommandTeleportImpl implements CommandTeleport {

    private final CommandSender sender;
    private final String playerName;

    public CommandTeleportImpl(CommandSender sender, String playerName) {
        this.sender = sender;
        this.playerName = playerName;
    }

    @Override
    public void tpa() {
        if(!this.preCheck()) return;
        TeleportPrecondition.create().preCheckStatus((Player) this.sender, Bukkit.getPlayerExact(this.playerName), AriCommand.TPA);
    }

    @Override
    public void tpaaccept() {
        if(!this.preCheck()) return;
        Player player = Bukkit.getPlayerExact(this.playerName);
        TeleportStatus status = TeleportPrecondition.create().checkStatusV(player, (Player) this.sender);
        if(status == null) {
            this.sender.sendMessage(TextTool.setHEXColorText("command.tpaaccept.been-done", FilePath.Lang));
            return;
        }
        //请求成功，移除该请求
        this.sender.sendMessage(TextTool.setHEXColorText("command.tpaaccept.agree", FilePath.Lang));
        Ari.instance.tpStatusValue.remove(player, TeleportType.PLAYER);
        TeleportThread teleportThread = switch (status.getCommandType()) {
            case TPA -> TeleportThread.playerToPlayer(player, ((Player) this.sender));
            case TPAHERE -> TeleportThread.playerToPlayer((Player) this.sender, player);
            default -> null;
        };
        if (teleportThread != null) {
            teleportThread.teleport(Ari.instance.configManager.getValue("main.teleport.delay", FilePath.TPA, Integer.class));
        } else {
            this.sender.sendMessage(TextTool.setHEXColorText("command.tpaaccept.error"));
        }
    }

    @Override
    public void tparefuse() {
        if(!this.preCheck()) return;
        Player player = Bukkit.getPlayerExact(this.playerName);
        if (TeleportPrecondition.create().checkStatusV(player, (Player) this.sender) == null) {
            this.sender.sendMessage(TextTool.setHEXColorText("command.tparefuse.been-done", FilePath.Lang));
        } else {
            if (Ari.instance.tpStatusValue.getStatusList().removeIf(obj -> {
                assert player != null;
                return obj.getPlayUUID().equals(player.getUniqueId()) && obj.getType().equals(TeleportType.PLAYER);
            })) {
                this.sender.sendMessage(TextTool.setHEXColorText("command.tparefuse.success", FilePath.Lang));
                if(Ari.instance.configManager.getValue("command.tparefuse.get-message", FilePath.Lang, String.class) instanceof String message) {
                    player.sendMessage(TextTool.setHEXColorText(message.replace(TeleportObjectType.TPABESENDER.getType(), this.sender.getName())));
                }
            } else {
                this.sender.sendMessage(TextTool.setHEXColorText("command.public.break", FilePath.Lang));
            }
        }
    }

    @Override
    public void tpahere() {
        if(!preCheck()) return;
        TeleportPrecondition.create().preCheckStatus((Player) this.sender, Bukkit.getPlayerExact(this.playerName), AriCommand.TPAHERE);
    }

    @Override
    public List<String> getOnlinePlayers(AriCommand ariCommand) {
        if (this.sender instanceof Player && Ari.instance.permissionUtils.hasPermission(this.sender, AriCommand.TPA.getPermission())) {
            List<String> players = new ArrayList<>();
            Ari.instance.getServer().getOnlinePlayers().forEach(e -> {
                if(this.sender.getName().equals(e.getName())) return;
                players.add(e.getName());
            });
            return players;
        }
        return List.of();
    }

    @Override
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
            this.sender.sendMessage(TextTool.setHEXColorText("command.public.not-player", FilePath.Lang));
            return false;
        }
        if (this.playerName.equals(this.sender.getName())) {
            this.sender.sendMessage(TextTool.setHEXColorText("command.public.fail", FilePath.Lang));
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
