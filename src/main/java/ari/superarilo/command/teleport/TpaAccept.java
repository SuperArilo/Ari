package ari.superarilo.command.teleport;

import ari.superarilo.Ari;
import ari.superarilo.command.tool.CommandCheck;
import ari.superarilo.entity.TeleportStatus;
import ari.superarilo.enumType.AriCommand;
import ari.superarilo.enumType.FilePath;
import ari.superarilo.function.teleport.TeleportPrecondition;
import ari.superarilo.tool.ConfigFiles;
import ari.superarilo.tool.TeleportThread;
import ari.superarilo.tool.TextTool;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;


public class TpaAccept implements TabExecutor {
    private final ConfigFiles config = Ari.instance.getConfigFiles();
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!CommandCheck.create().allCheck(commandSender, command, AriCommand.TPAACCEPT)) return false;

        if (strings.length != 1 || strings[0].equals(commandSender.getName())) {
            commandSender.sendMessage(TextTool.setHEXColorText(this.config.getValue("command.tpaaccept.fail", FilePath.Lang, String.class)));
            return true;
        }
        Player player = Ari.instance.getServer().getPlayerExact(strings[0]);
        //判断玩家是否存在
        if (player == null) {
            commandSender.sendMessage(TextTool.setHEXColorText(this.config.getValue("command.tpaaccept.unable-player", FilePath.Lang, String.class)));
            return true;
        }
        //判断请求是否还存在

        TeleportStatus status = TeleportPrecondition.create().checkStatusV(player, (Player) commandSender);

        if(status == null) {
            commandSender.sendMessage(TextTool.setHEXColorText(this.config.getValue("command.tpaaccept.been-done", FilePath.Lang, String.class)));
            return true;
        }
        //请求成功，移除该请求
        commandSender.sendMessage(TextTool.setHEXColorText(this.config.getValue("command.tpaaccept.agree", FilePath.Lang, String.class)));
        Ari.instance.getTpStatusValue().remove(player, TeleportThread.Type.PLAYER);

        TeleportThread teleportThread = switch (status.getCommandType()) {
            case TPA -> new TeleportThread(player, ((Player) commandSender), TeleportThread.Type.PLAYER);
            case TPAHERE -> new TeleportThread(((Player) commandSender), player, TeleportThread.Type.PLAYER);
            default -> null;
        };
        if (teleportThread != null) {
            teleportThread.teleport();
        } else {
            commandSender.sendMessage(TextTool.setHEXColorText("command.tpaaccept.error"));
        }
        return true;
    }
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (commandSender instanceof Player) {
            List<String> i = new ArrayList<>();
            Server tempServer = Ari.instance.getServer();
            Ari.instance.getTpStatusValue().getStatusList().stream().filter(obj ->
                    obj.getBePlayerUUID().equals(((Player) commandSender).getUniqueId()) && obj.getType().equals(TeleportThread.Type.PLAYER))
                    .forEach(e -> {
                        Player p = tempServer.getPlayer(e.getPlayUUID());
                        if (p != null) {
                            i.add(p.getName());
                        }
                    });
            return i;
        }
        return List.of("");
    }
}
