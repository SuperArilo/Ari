package com.tty.commands.check;

import com.tty.Ari;
import com.tty.dto.TeleportStatus;
import com.tty.enumType.FilePath;
import com.tty.lib.Lib;
import com.tty.lib.enum_type.LangType;
import com.tty.lib.enum_type.TeleportType;
import com.tty.lib.tool.ComponentUtils;
import com.tty.tool.ConfigUtils;
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
    public static void preCheckStatus(Player player, Player targetPlayer, String commandString) {
        if (checkHaveTeleportStatus(player, targetPlayer) != null) {
            player.sendMessage(ConfigUtils.t("function.tpa.again", LangType.TPABESENDER.getType(), targetPlayer.getName()));
            return;
        }
        player.sendMessage(ConfigUtils.t("function.tpa.send-message"));
        addTeleportStatusTask(player, targetPlayer, commandString, Ari.C_INSTANCE.getValue("main.teleport.cooldown", FilePath.TPA, Long.class, 10L) * 20);
        String message = Ari.C_INSTANCE.getValue("function.tpa." + (commandString.equals("tpa") ? "to-message":"here-message"), FilePath.Lang);
        targetPlayer.sendMessage(
                ComponentUtils.text(message.replace(LangType.TPASENDER.getType(), player.getName()))
                        .appendNewline()
                        .append(ComponentUtils.setClickEventText(Ari.C_INSTANCE.getValue("function.public.agree", FilePath.Lang), ClickEvent.Action.RUN_COMMAND, "/ari tpaaccept " + player.getName()))
                        .append(ConfigUtils.t("function.public.center"))
                        .append(ComponentUtils.setClickEventText(Ari.C_INSTANCE.getValue("function.public.refuse", FilePath.Lang), ClickEvent.Action.RUN_COMMAND, "/ari tparefuse " + player.getName())));
    }
    /**
     * 检查被传送玩家是否已经发起过传送请求
     * @param player 被传送玩家
     * @param location 目标位置
     * @param delay 传送冷却
     */
    public static boolean preCheckStatus(Player player, Location location, long delay) {
        if (player.isOp()) return true;
        if(checkHaveTeleportStatus(player, location) == null) {
            addTeleportStatusTask(player, location, delay);
            return true;
        } else {
            player.sendMessage(ConfigUtils.t("teleport.again"));
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
    private static void addTeleportStatusTask(Player player, Player targetPlayer, String commandString, long delay) {
        TeleportStatus build = TeleportStatus.build(player.getUniqueId(), targetPlayer.getUniqueId(), TeleportType.PLAYER, commandString);
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

    /**
     * 预先检查发起者和被发起者是否在游戏里发起，目标玩家是否在线
     * @param sender 指令发起者
     * @param targetPlayerName 目标玩家
     * @return 返回检查结果 true 通过， false 失败
     */

    public static boolean preCheck(CommandSender sender, String targetPlayerName) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(ConfigUtils.t("function.public.not-player"));
            return false;
        }
        if (targetPlayerName.equals(sender.getName())) {
            sender.sendMessage(ConfigUtils.t("function.public.fail"));
            return false;
        }
        Player player = Ari.instance.getServer().getPlayerExact(targetPlayerName);
        if(player == null) {
            sender.sendMessage(ConfigUtils.t("teleport.unable-player"));
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
