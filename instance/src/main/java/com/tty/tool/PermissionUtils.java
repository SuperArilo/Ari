package com.tty.tool;

import com.tty.enumType.FilePath;
import com.tty.lib.tool.Log;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.checkerframework.checker.nullness.qual.NonNull;


public class PermissionUtils {

    public static Permission PERMISSION;

    public static void setInstance(Permission p) {
        PERMISSION = p;
    }

    public static Permission getInstance() {
        return PERMISSION;
    }

    /**
     * 返回指定玩家的组id名称
     * @param player 被检查玩家
     * @return 返回的组id名称
     */
    public static String getPlayerGroup(Player player) {
        return isNull() ? "default":PERMISSION.getPrimaryGroup(player);
    }

    /**
     *
     * @param player 被检查玩家
     * @param groupName 组名称
     * @return 返回该玩家是否存在于这个组
     */
    public static boolean getPlayerIsInGroup(Player player, String groupName) {
        return isNull() || PERMISSION.playerInGroup(player, groupName);
    }

    /**
     * 返回玩家是否具有对应的权限
     * @param player 被检查玩家
     * @param permission 权限字符串
     * @return 布尔值
     */
    public static boolean hasPermission(Player player, @NonNull String permission) {
        return isNull() ? player.hasPermission(permission):PERMISSION.has(player, permission);
    }

    /**
     * 返回玩家是否具有对应的权限
     * @param sender 被检查玩家
     * @param permission 权限字符串
     * @return 布尔值
     */
    public static boolean hasPermission(CommandSender sender, String permission) {
        return isNull() ? sender.hasPermission(permission):PERMISSION.has(sender, permission);
    }

    public static int getMaxCountInPermission(Player player, String typeString) {
        if (player.isOp()) return Integer.MAX_VALUE;
        int initValue = 0;
        String firstErrorPermission = null;
        for (PermissionAttachmentInfo permissionInfo : player.getEffectivePermissions()) {
            String permission = permissionInfo.getPermission();
            if (!permission.startsWith("ari.count." + typeString + ".")) continue;
            String[] parts = permission.split("\\.");
            if (parts.length < 4) {
                if (firstErrorPermission == null) firstErrorPermission = permission;
                continue;
            }
            try {
                int count = Integer.parseInt(parts[3]);
                if (count > initValue) initValue = count;
            } catch (NumberFormatException e) {
                if (firstErrorPermission == null) firstErrorPermission = permission;
            }
        }
        if (initValue == 0 && firstErrorPermission != null) {
            player.sendMessage(TextTool.setHEXColorText("base.on-error", FilePath.Lang));
            Log.error("player " + player.getName() + " permission format error: " + firstErrorPermission);
        }
        return initValue;
    }

    protected static boolean isNull() {
        return PERMISSION == null;
    }
}
