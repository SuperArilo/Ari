package ari.superarilo.function.impl;

import ari.superarilo.Ari;
import ari.superarilo.entity.TeleportStatus;
import ari.superarilo.enumType.AriCommand;
import ari.superarilo.enumType.FilePath;
import ari.superarilo.enumType.TeleportObjectType;
import ari.superarilo.enumType.TeleportType;
import ari.superarilo.function.TeleportPrecondition;
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
                        obj.getType().equals(TeleportType.PLAYER))
                .findFirst();

        if (first.isPresent()) {
            if(Ari.instance.configManager.getValue("command." + ariCommand.getShow() + ".again", FilePath.Lang, String.class) instanceof String message) {
                sender.sendMessage(TextTool.setHEXColorText(message.replace(TeleportObjectType.TPABESENDER.getType(), targetPlayer.getName())));
            }
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
        return Ari.instance.tpStatusValue.getStatusList().stream()
                .filter(obj ->
                        obj.getPlayUUID().equals(sender.getUniqueId()) &&
                                obj.getBePlayerUUID().equals(targetPlayer.getUniqueId()) &&
                                obj.getType().equals(TeleportType.PLAYER))
                .findFirst()
                .orElse(null);
    }
    //向接收玩家发送接受传送邀请信息
    protected void sendMessageToBePlayer(Player player, Player targetPlayer, AriCommand ariCommand) {
        if(Ari.instance.configManager.getValue("command." + ariCommand.getShow() + ".get-message", FilePath.Lang, String.class) instanceof String message) {
            targetPlayer.sendMessage(
                            TextTool.setHEXColorText(
                                    message
                                    .replace(
                                            ariCommand.equals(AriCommand.TPA) ? TeleportObjectType.TPASENDER.getType():ariCommand.equals(AriCommand.TPAHERE) ? TeleportObjectType.TPAHERESENDER.getType():"",
                                            player.getName()))
                    .appendNewline()
                    .append(TextTool.setClickEventText(Ari.instance.configManager.getValue("command.public.agree", FilePath.Lang, String.class), ClickEvent.Action.RUN_COMMAND, "/ari tpaaccept " + player.getName()))
                    .append(TextTool.setHEXColorText(Ari.instance.configManager.getValue("command.public.center", FilePath.Lang, String.class)))
                    .append(TextTool.setClickEventText(Ari.instance.configManager.getValue("command.public.refuse", FilePath.Lang, String.class), ClickEvent.Action.RUN_COMMAND, "/ari tparefuse " + player.getName())));
        }

    }

    /**
     * 添加玩家传送到玩家的状态
     * @param player 被传送玩家
     * @param targetPlayer 接收玩家
     * @param ariCommand 传送类型
     */
    protected void startAddTask(Player player, Player targetPlayer, AriCommand ariCommand) {
        Bukkit.getAsyncScheduler().runNow(Ari.instance, t -> {
            TeleportStatus status = new TeleportStatus();
            status.setType(TeleportType.PLAYER);
            status.setCommandType(ariCommand);
            status.setPlayUUID(player.getUniqueId());
            status.setBePlayerUUID(targetPlayer.getUniqueId());
            Ari.instance.tpStatusValue.addStatus(status);
            //设置定时任务来移除该玩家已经发送的请求状态
            Bukkit.getAsyncScheduler().runDelayed(Ari.instance, i -> Ari.instance.tpStatusValue.remove(player, TeleportType.PLAYER), 10L, TimeUnit.SECONDS);
        });
    }

}
