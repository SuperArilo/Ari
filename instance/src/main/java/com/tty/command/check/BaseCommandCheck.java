package com.tty.command.check;

import com.tty.enumType.AriCommand;
import com.tty.enumType.FilePath;
import com.tty.lib.tool.ComponentUtils;
import com.tty.tool.ConfigUtils;
import com.tty.tool.PermissionUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static com.tty.listener.player.OnPlayerJoinAndLeaveListener.PLAYER_LOGIN_TIMES;

public class BaseCommandCheck {

    /**
     * 判断指令字符串是否匹配
     * @return true为匹配
     */
    protected boolean isTheInstructionCorrect(Command command, AriCommand ariCommand) {
        return command.getName().equals(ariCommand.getShow());
    }
    /**
     * 检查指令发起者是否是玩家
     * @return true 为匹配
     */
    protected boolean isPlayer(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ComponentUtils.text(ConfigUtils.getValue(
                    "function.public.not-player",
                    FilePath.Lang)));
            return false;
        }
        return true;
    }

    /**
     * 检查指令发起者有没有指定的权限
     * @param command 被检查的指令
     * @return true 为匹配
     */
    protected boolean hasPermission(CommandSender sender, AriCommand command) {
        if (!PermissionUtils.hasPermission(sender, command.getPermission())) {
            sender.sendMessage(ComponentUtils.text(ConfigUtils.getValue("base.permission.no-permission", FilePath.Lang)));
            return false;
        }
        return true;
    }

    protected boolean quickCheck(CommandSender sender, AriCommand ariCommand) {
        return this.baseQuickCheck(sender, ariCommand);
    }

    protected boolean quickCheck(CommandSender sender, AriCommand ariCommand, int argsLength, int length) {
        if (!this.baseQuickCheck(sender, ariCommand)) return false;
        if (argsLength != length) {
            sender.sendMessage(ComponentUtils.text(ConfigUtils.getValue("function.public.fail", FilePath.Lang)));
            return false;
        }
        return true;
    }

    private boolean baseQuickCheck(CommandSender sender, AriCommand ariCommand) {
        if(!this.isPlayer(sender)) return false;
        if (!PLAYER_LOGIN_TIMES.containsKey(((Player) sender).getUniqueId())) {
            sender.sendMessage(ComponentUtils.text(ConfigUtils.getValue("base.permission.whitelist-no-permission", FilePath.Lang)));
            return false;
        }
        return this.hasPermission(sender, ariCommand);
    }
}
