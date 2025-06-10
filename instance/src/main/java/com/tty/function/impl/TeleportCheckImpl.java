package com.tty.function.impl;

import com.tty.Ari;
import com.tty.entity.TeleportStatus;
import com.tty.enumType.AriCommand;
import com.tty.enumType.FilePath;
import com.tty.function.TeleportCheck;
import com.tty.lib.Lib;
import com.tty.lib.enum_type.LangType;
import com.tty.lib.enum_type.TeleportType;
import com.tty.tool.ConfigObjectUtils;
import com.tty.tool.TextTool;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Location;
import org.bukkit.entity.Player;


public class TeleportCheckImpl implements TeleportCheck {
    @Override
    public void preCheckStatus(Player player, Player targetPlayer, AriCommand ariCommand) {
        if(this.checkHaveTeleportStatus(player, targetPlayer) == null) {
            player.sendMessage(TextTool.setHEXColorText("function.tpa.send-message", FilePath.Lang));
            this.addTeleportStatusTask(player, targetPlayer, ariCommand);
            String message = ConfigObjectUtils.getValue("function.tpa.get-message", FilePath.Lang.getName(), String.class, "null");
            boolean isTpa = message.contains(LangType.TPASENDER.getType());
            targetPlayer.sendMessage(
                    TextTool.setHEXColorText(message.replace(isTpa ? LangType.TPASENDER.getType():LangType.TPABESENDER.getType(), isTpa ? player.getName():targetPlayer.getName()))
                            .appendNewline()
                            .append(TextTool.setClickEventText(ConfigObjectUtils.getValue("function.public.agree", FilePath.Lang.getName(), String.class, "null"), ClickEvent.Action.RUN_COMMAND, "/ari tpaaccept " + player.getName()))
                            .append(TextTool.setHEXColorText(ConfigObjectUtils.getValue("function.public.center", FilePath.Lang.getName(), String.class, "null")))
                            .append(TextTool.setClickEventText(ConfigObjectUtils.getValue("function.public.refuse", FilePath.Lang.getName(), String.class, "null"), ClickEvent.Action.RUN_COMMAND, "/ari tparefuse " + player.getName())));
        } else {
            player.sendMessage(TextTool.setHEXColorText(ConfigObjectUtils.getValue("function.tpa.again", FilePath.Lang.getName(), String.class, "null").replace(LangType.TPABESENDER.getType(), targetPlayer.getName())));
        }
    }

    @Override
    public boolean preCheckStatus(Player player, Location location) {
        if(this.checkHaveTeleportStatus(player, location) == null) {
            this.addTeleportStatusTask(player, location);
            return true;
        } else {
            player.sendMessage(TextTool.setHEXColorText("teleport.again", FilePath.Lang));
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
        Lib.Scheduler.runLater(Ari.instance, i -> Ari.instance.tpStatusValue.remove(player, TeleportType.PLAYER), 200L);
    }
    /**
     * 添加玩家传送到玩家的状态
     * @param player 被传送玩家
     * @param location 传送地方
     */
    private void addTeleportStatusTask(Player player, Location location) {
        TeleportStatus build = TeleportStatus.build(player.getUniqueId(), location, TeleportType.POINT, null);
        Ari.instance.tpStatusValue.addStatus(build);
        Lib.Scheduler.runLater(Ari.instance, i -> Ari.instance.tpStatusValue.remove(player, TeleportType.POINT), 200L);
    }
}
