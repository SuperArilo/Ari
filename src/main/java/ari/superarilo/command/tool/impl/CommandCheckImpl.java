package ari.superarilo.command.tool.impl;

import ari.superarilo.command.tool.CommandCheck;
import ari.superarilo.enumType.AriCommand;
import ari.superarilo.enumType.FilePath;
import ari.superarilo.tool.TextTool;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandCheckImpl implements CommandCheck {

    private static final String i = "command";
    private static final String d = ".";
    private static final String NOTPLAYER = "not-player";
    private static final String PERMISSIONMESSAGE = "permission-message";


    @Override
    //判断指令是否正确
    public boolean isTheInstructionCorrect(Command command, AriCommand type) {
        return command.getName().equalsIgnoreCase(type.getShow());
    }

    @Override
    public boolean isPlayer(CommandSender commandSender, AriCommand type) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(TextTool.setHEXColorText(i + d + type.getShow() + d + NOTPLAYER, FilePath.Lang));
        }
        return true;
    }

    @Override
    public boolean commandSenderHavePermission(CommandSender commandSender, AriCommand type) {
        if (!commandSender.hasPermission(type.getPermission())) {
            commandSender.sendMessage(TextTool.setHEXColorText(i + d + type.getShow() + d + PERMISSIONMESSAGE, FilePath.Lang));
        }
        return true;
    }

    @Override
    public boolean allCheck(CommandSender commandSender, Command command, AriCommand ariCommand) {
        //判断是否是玩家
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(TextTool.setHEXColorText(i + d + ariCommand.getShow() + d + NOTPLAYER, FilePath.Lang));
            return false;
        }
        //判断是否有相应的权限
        if (!commandSender.hasPermission(ariCommand.getPermission())) {
            commandSender.sendMessage(TextTool.setHEXColorText(i + d + ariCommand.getShow() + d + PERMISSIONMESSAGE, FilePath.Lang));
            return false;
        }
        return true;
    }

    @Override
    public boolean isTheCommandIncomplete(String[] strings, String senderName) {
        return false;
    }
}
