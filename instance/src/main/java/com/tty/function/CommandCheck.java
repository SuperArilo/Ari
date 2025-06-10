package com.tty.function;

import com.tty.enumType.AriCommand;
import com.tty.function.impl.CommandCheckImpl;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public interface CommandCheck {
    /**
     * 判断指令字符串是否匹配
     * @return true为匹配
     */
    boolean isTheInstructionCorrect();

    /**
     * 检查指令发起者是否是玩家
     * @return true 为匹配
     */
    boolean isPlayer();

    /**
     * 检查指令发起者有没有指定的权限
     * @param ariCommand 被检查的指令
     * @return true 为匹配
     */
    boolean commandSenderHavePermission(AriCommand ariCommand);
    /**
     * 检查指令发起者有没有对应的权限
     * @return true 为匹配
     */
    boolean commandSenderHavePermission();

    /**
     * 检查是否是玩家和检查有没有相应的权限
     * @return true 为匹配
     */
    boolean allCheck();
    boolean isTheCommandIncomplete(String[] strings, String senderName);
    static CommandCheckImpl create(CommandSender commandSender, Command command, AriCommand ariCommand) {
        return new CommandCheckImpl(commandSender, command, ariCommand);
    }
}
