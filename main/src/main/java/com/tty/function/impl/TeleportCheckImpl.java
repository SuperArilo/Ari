package com.tty.function.impl;

import com.tty.Ari;
import com.tty.entity.TeleportStatus;
import com.tty.enumType.AriCommand;
import com.tty.enumType.FilePath;
import com.tty.enumType.LangType;
import com.tty.enumType.TeleportType;
import com.tty.function.TeleportCheck;
import com.tty.tool.TextTool;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;

public class TeleportCheckImpl implements TeleportCheck {
    @Override
    public void preCheckStatus(Player player, Player targetPlayer, AriCommand ariCommand) {
        if(this.checkHaveTeleportStatus(player, targetPlayer) == null) {
            player.sendMessage(TextTool.setHEXColorText(Ari.instance.configManager.getValue("function.tpa.send-message", FilePath.Lang, String.class)));
            this.addTeleportStatusTask(player, targetPlayer, ariCommand);
            if(Ari.instance.configManager.getValue("function.tpa.get-message", FilePath.Lang, String.class) instanceof String message) {
                boolean isTpa = message.contains(LangType.TPASENDER.getType());
                targetPlayer.sendMessage(
                        TextTool.setHEXColorText(message.replace(isTpa ? LangType.TPASENDER.getType():LangType.TPABESENDER.getType(), isTpa ? player.getName():targetPlayer.getName()))
                                .appendNewline()
                                .append(TextTool.setClickEventText(Ari.instance.configManager.getValue("function.public.agree", FilePath.Lang, String.class), ClickEvent.Action.RUN_COMMAND, "/ari tpaaccept " + player.getName()))
                                .append(TextTool.setHEXColorText(Ari.instance.configManager.getValue("function.public.center", FilePath.Lang, String.class)))
                                .append(TextTool.setClickEventText(Ari.instance.configManager.getValue("function.public.refuse", FilePath.Lang, String.class), ClickEvent.Action.RUN_COMMAND, "/ari tparefuse " + player.getName())));
            }
        } else {
            if(Ari.instance.configManager.getValue("function.tpa.again", FilePath.Lang, String.class) instanceof String message) {
                player.sendMessage(TextTool.setHEXColorText(message.replace(LangType.TPABESENDER.getType(), targetPlayer.getName())));
            }
        }
    }

    @Override
    public boolean preCheckStatus(Player player, Location location) {
        if(this.checkHaveTeleportStatus(player, location) == null) {
            this.addTeleportStatusTask(player, location);
            return true;
        } else {
            player.sendMessage(TextTool.setHEXColorText(Ari.instance.configManager.getValue("teleport.again", FilePath.Lang, String.class)));
            return false;
        }
    }

    @Override
    public TeleportStatus checkHaveTeleportStatus(Player player, Player targetPlayer) {
        return Ari.instance.tpStatusValue.getStatusList().stream()
                .filter(obj ->
                        obj.getPlayUUID().equals(player.getUniqueId()) &&
                                obj.getBePlayerUUID().equals(targetPlayer.getUniqueId()) &&
                                obj.getType().equals(TeleportType.PLAYER))
                .findFirst()
                .orElse(null);
    }


    @Override
    public TeleportStatus checkHaveTeleportStatus(Player player, Location location) {
        return Ari.instance.tpStatusValue.getStatusList().stream()
                .filter(obj ->
                        obj.getPlayUUID().equals(player.getUniqueId()) &&
                                obj.getLocation().equals(location) &&
                                obj.getType().equals(TeleportType.POINT))
                .findFirst()
                .orElse(null);
    }
    /**
     * 添加玩家传送到玩家的状态
     * @param player       被传送玩家
     * @param targetPlayer 接收玩家
     */
    private void addTeleportStatusTask(Player player, Player targetPlayer, AriCommand ariCommand) {
        TeleportStatus build = TeleportStatus.build(player.getUniqueId(), targetPlayer.getUniqueId(), TeleportType.PLAYER, ariCommand);
        Ari.instance.tpStatusValue.addStatus(build);
        Bukkit.getAsyncScheduler().runDelayed(Ari.instance, i -> Ari.instance.tpStatusValue.remove(player, TeleportType.PLAYER), 10L, TimeUnit.SECONDS);
    }
    /**
     * 添加玩家传送到玩家的状态
     * @param player 被传送玩家
     * @param location 传送地方
     */
    private void addTeleportStatusTask(Player player, Location location) {
        TeleportStatus build = TeleportStatus.build(player.getUniqueId(), location, TeleportType.POINT, null);
        Ari.instance.tpStatusValue.addStatus(build);
        Bukkit.getAsyncScheduler().runDelayed(Ari.instance, i -> Ari.instance.tpStatusValue.remove(player, TeleportType.POINT), 10L, TimeUnit.SECONDS);
    }
}
