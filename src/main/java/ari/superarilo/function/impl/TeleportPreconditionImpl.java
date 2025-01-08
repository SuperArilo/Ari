package ari.superarilo.function.impl;

import ari.superarilo.Ari;
import ari.superarilo.entity.TeleportStatus;
import ari.superarilo.enumType.AriCommand;
import ari.superarilo.enumType.FilePath;
import ari.superarilo.enumType.KeyType;
import ari.superarilo.function.TeleportPrecondition;
import ari.superarilo.tool.TeleportThread;
import ari.superarilo.tool.TextTool;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class TeleportPreconditionImpl implements TeleportPrecondition {

    public TeleportPreconditionImpl() { }

    @Override
    public void preCheckStatus(Player sender, Player targetPlayer, AriCommand ariCommand) {
        Optional<TeleportStatus> first = Ari.instance.tpStatusValue.getStatusList().stream()
                .filter(obj ->
                        obj.getPlayUUID().equals(sender.getUniqueId()) &&
                        obj.getBePlayerUUID().equals(targetPlayer.getUniqueId()) &&
                        obj.getType().equals(TeleportThread.Type.PLAYER))
                .findFirst();

        if (first.isPresent()) {
            sender.sendMessage(
                    TextTool.setHEXColorText(
                            Ari.instance.configManager.getValue("command." + ariCommand.getShow() + ".again", FilePath.Lang, String.class)
                                    .replace(
                                            KeyType.TPABESENDER.getType(),
                                            targetPlayer.getName())));
        } else {
            sender.sendMessage(TextTool.setHEXColorText(Ari.instance.configManager.getValue("command." + ariCommand.getShow() + ".send-message", FilePath.Lang, String.class)));
            this.sendMessageToBePlayer(sender, targetPlayer, ariCommand);
            this.startAddTask(sender, targetPlayer, ariCommand);
        }
    }

    @Override
    public TeleportStatus preCheckStatus(Player sender, Location targetLocation) {
        return null;
    }

    @Override
    public TeleportStatus checkStatusV(Player sender, Player targetPlayer) {
        Optional<TeleportStatus> first = Ari.instance.tpStatusValue.getStatusList().stream()
                .filter(obj ->
                        obj.getPlayUUID().equals(sender.getUniqueId()) &&
                                obj.getBePlayerUUID().equals(targetPlayer.getUniqueId()) &&
                                obj.getType().equals(TeleportThread.Type.PLAYER))
                .findFirst();
        return first.orElse(null);
    }

    protected void sendMessageToBePlayer(Player player, Player targetPlayer, AriCommand ariCommand) {
        targetPlayer.sendMessage(
                TextTool.setHEXColorText(
                        Ari.instance.configManager.getValue("command." + ariCommand.getShow() + ".get-message", FilePath.Lang, String.class)
                                .replace(
                                        ariCommand.equals(AriCommand.TPA) ? KeyType.TPASENDER.getType():ariCommand.equals(AriCommand.TPAHERE) ? KeyType.TPAHERESENDER.getType():"",
                                        player.getName()))
                        .appendNewline()
                        .append(TextTool.setClickEventText(Ari.instance.configManager.getValue("command.public.agree", FilePath.Lang, String.class), ClickEvent.Action.RUN_COMMAND, "/ari tpaaccept " + player.getName()))
                        .append(TextTool.setHEXColorText(Ari.instance.configManager.getValue("command.public.center", FilePath.Lang, String.class)))
                        .append(TextTool.setClickEventText(Ari.instance.configManager.getValue("command.public.agree.refuse", FilePath.Lang, String.class), ClickEvent.Action.RUN_COMMAND, "/ari tparefuse " + player.getName())));
    }
    protected void startAddTask(Player player, Player targetPlayer, AriCommand ariCommand) {
        Bukkit.getAsyncScheduler().runNow(Ari.instance, t -> {
            TeleportStatus status = new TeleportStatus();
            status.setType(TeleportThread.Type.PLAYER);
            status.setCommandType(ariCommand);
            status.setPlayUUID(player.getUniqueId());
            status.setBePlayerUUID(targetPlayer.getUniqueId());
            Ari.instance.tpStatusValue.addStatus(status);
            //设置定时任务来移除该玩家已经发送的请求状态
            Bukkit.getAsyncScheduler().runDelayed(Ari.instance, i -> Ari.instance.tpStatusValue.remove(player, TeleportThread.Type.PLAYER), 10L, TimeUnit.SECONDS);
        });
    }

}
