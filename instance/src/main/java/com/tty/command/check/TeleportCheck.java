package com.tty.command.check;

import com.tty.Ari;
import com.tty.dto.TeleportStatus;
import com.tty.enumType.AriCommand;
import com.tty.enumType.FilePath;
import com.tty.lib.Lib;
import com.tty.lib.enum_type.LangType;
import com.tty.lib.enum_type.TeleportType;
import com.tty.tool.ConfigObjectUtils;
import com.tty.tool.TextTool;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;


public class TeleportCheck {

    public static final List<TeleportStatus> TELEPORT_STATUS = new ArrayList<>();

    /**
     * 检查是否已经向目标玩家发送过传送请求
     * @param player 被传送玩家
     * @param targetPlayer 目标玩家
     */
    public static void preCheckStatus(Player player, Player targetPlayer, AriCommand ariCommand) {
        if (checkHaveTeleportStatus(player, targetPlayer) != null) {
            player.sendMessage(TextTool.setHEXColorText(
                    ConfigObjectUtils.getValue(
                            "function.tpa.again",
                            FilePath.Lang.getName(),
                            String.class,
                            "null").replace(LangType.TPABESENDER.getType(), targetPlayer.getName())));
            return;
        }
        player.sendMessage(TextTool.setHEXColorText("function.tpa.send-message", FilePath.Lang));
        addTeleportStatusTask(player, targetPlayer, ariCommand, 200L);
        String message = ConfigObjectUtils.getValue("function.tpa.get-message", FilePath.Lang.getName(), String.class, "null");
        boolean isTpa = message.contains(LangType.TPASENDER.getType());
        targetPlayer.sendMessage(
                TextTool.setHEXColorText(message.replace(isTpa ? LangType.TPASENDER.getType():LangType.TPABESENDER.getType(), isTpa ? player.getName():targetPlayer.getName()))
                        .appendNewline()
                        .append(TextTool.setClickEventText(ConfigObjectUtils.getValue("function.public.agree", FilePath.Lang.getName(), String.class, "null"), ClickEvent.Action.RUN_COMMAND, "/ari tpaaccept " + player.getName()))
                        .append(TextTool.setHEXColorText(ConfigObjectUtils.getValue("function.public.center", FilePath.Lang.getName(), String.class, "null")))
                        .append(TextTool.setClickEventText(ConfigObjectUtils.getValue("function.public.refuse", FilePath.Lang.getName(), String.class, "null"), ClickEvent.Action.RUN_COMMAND, "/ari tparefuse " + player.getName())));
    }
    /**
     * 检查被传送玩家是否已经发起过传送请求
     * @param player 被传送玩家
     * @param location 目标位置
     * @param delay 传送冷却
     */
    public static boolean preCheckStatus(Player player, Location location, long delay) {
        if(checkHaveTeleportStatus(player, location) == null) {
            addTeleportStatusTask(player, location, delay);
            return true;
        } else {
            player.sendMessage(TextTool.setHEXColorText("teleport.again", FilePath.Lang));
            return false;
        }
    }
    /**
     * 查询玩家之间传送的请求
     * @param player 被传送的玩家
     * @param targetPlayer 目标玩家
     * @return 传送请求类
     */
    public static TeleportStatus checkHaveTeleportStatus(Player player, Player targetPlayer) {
        return TELEPORT_STATUS.stream()
                .filter(obj ->
                        obj.getPlayUUID().equals(player.getUniqueId()) &&
                                (obj.getBePlayerUUID() != null && obj.getBePlayerUUID().equals(targetPlayer.getUniqueId())) &&
                                obj.getType().equals(TeleportType.PLAYER))
                .findFirst()
                .orElse(null);
    }

    /**
     * 查询玩家定点传送的请求
     * @param player 被传送的玩家
     * @param location 目标地方
     * @return 传送请求类
     */
    public static TeleportStatus checkHaveTeleportStatus(Player player, Location location) {
        return TELEPORT_STATUS.stream()
                .filter(obj ->
                        obj.getPlayUUID().equals(player.getUniqueId()) &&
                                (obj.getLocation() == null || obj.getLocation().equals(location)) &&
                                obj.getType().equals(TeleportType.POINT))
                .findFirst()
                .orElse(null);
    }
    /**
     * 添加玩家传送到玩家的状态
     * @param player       被传送玩家
     * @param targetPlayer 接收玩家
     * @param delay 传送冷却
     */
    private static void addTeleportStatusTask(Player player, Player targetPlayer, AriCommand ariCommand, long delay) {
        TeleportStatus build = TeleportStatus.build(player.getUniqueId(), targetPlayer.getUniqueId(), TeleportType.PLAYER, ariCommand);
        TELEPORT_STATUS.add(build);
        Lib.Scheduler.runAsyncDelayed(Ari.instance, i -> remove(player, null,TeleportType.PLAYER), delay);
    }
    /**
     * 添加玩家传送到玩家的状态
     * @param player 被传送玩家
     * @param location 传送地方
     * @param delay 传送冷却
     */
    private static void addTeleportStatusTask(Player player, Location location, long delay) {
        TeleportStatus build = TeleportStatus.build(player.getUniqueId(), location, TeleportType.POINT, null);
        TELEPORT_STATUS.add(build);
        Lib.Scheduler.runLater(Ari.instance, i -> remove(player, location, TeleportType.POINT), delay);
    }

    public static boolean preCheck(CommandSender sender, String targetPlayerName) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(TextTool.setHEXColorText("function.public.not-player", FilePath.Lang));
            return false;
        }
        if (targetPlayerName.equals(sender.getName())) {
            sender.sendMessage(TextTool.setHEXColorText("function.public.fail", FilePath.Lang));
            return false;
        }
        Player player = Ari.instance.getServer().getPlayerExact(targetPlayerName);
        if(player == null) {
            sender.sendMessage(TextTool.setHEXColorText("teleport.unable-player", FilePath.Lang));
            return false;
        }
        return true;
    }

    /**
     * 移除指定玩家已经保存的传送状态
     * @param player 玩家
     * @param type 传送类型
     */
    public static synchronized boolean remove(Player player, Location location, TeleportType type) {
        return TELEPORT_STATUS.removeIf(obj ->
                obj.getType().equals(type) &&
                        obj.getPlayUUID().equals(player.getUniqueId()) &&
                        (obj.getLocation() == null || obj.getLocation().equals(location))
        );
    }
}
