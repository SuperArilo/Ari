package ari.superarilo.function.impl;

import ari.superarilo.Ari;
import ari.superarilo.function.CommandCheck;
import ari.superarilo.enumType.AriCommand;
import ari.superarilo.enumType.FilePath;
import ari.superarilo.tool.TextTool;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandCheckImpl implements CommandCheck {

    @Override
    //判断指令是否正确
    public boolean isTheInstructionCorrect(Command command, AriCommand type) {
        return command.getName().equalsIgnoreCase(type.getShow());
    }

    @Override
    public boolean isPlayer(CommandSender commandSender, AriCommand type) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(TextTool.setHEXColorText("command.public.not-player", FilePath.Lang));
        }
        return true;
    }

    @Override
    public boolean commandSenderHavePermission(CommandSender commandSender, AriCommand type) {
        if (!Ari.instance.permissionUtils.hasPermission(commandSender, type.getPermission())) {
            commandSender.sendMessage(TextTool.setHEXColorText("command.public.permission-message", FilePath.Lang));
        }
        return true;
    }

    @Override
    public boolean allCheck(CommandSender commandSender, Command command, AriCommand ariCommand) {
        //判断是否是玩家
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(TextTool.setHEXColorText("command.public.not-player", FilePath.Lang));
            return false;
        }
        //判断是否有相应的权限
        if (!Ari.instance.permissionUtils.hasPermission(commandSender, ariCommand.getPermission())) {
            commandSender.sendMessage(TextTool.setHEXColorText("command.public.permission-message", FilePath.Lang));
            return false;
        }
        return true;
    }

    @Override
    public boolean isTheCommandIncomplete(String[] strings, String senderName) {
        return false;
    }
}
