package ari.superarilo.function;

import ari.superarilo.function.impl.CommandCheckImpl;
import ari.superarilo.enumType.AriCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public interface CommandCheck {
    /**
     * 判断指令字符串是否匹配
     * @param command 被检查的指令
     * @param type 指令type
     * @return true为匹配
     */
    boolean isTheInstructionCorrect(Command command, AriCommand type);

    /**
     * 检查指令发起者是否是玩家
     * @param commandSender 指令发送者
     * @return true 为匹配
     */
    boolean isPlayer(CommandSender commandSender);

    /**
     * 检查指令发起者有没有对应的权限
     * @param commandSender 指令发起者
     * @param type 指令类别
     * @return true 为匹配
     */
    boolean commandSenderHavePermission(CommandSender commandSender, AriCommand type);

    /**
     * 检查是否是玩家和检查有没有相应的权限
     * @param commandSender 指令发送者
     * @param ariCommand 指令type
     * @return true 为匹配
     */
    boolean allCheck(CommandSender commandSender, AriCommand ariCommand);
    boolean isTheCommandIncomplete(String[] strings, String senderName);
    static CommandCheckImpl create() {
        return new CommandCheckImpl();
    }
}
