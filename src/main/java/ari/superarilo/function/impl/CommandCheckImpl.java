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

    private final CommandSender commandSender;
    private final Command command;
    private final AriCommand ariCommand;

    public CommandCheckImpl(CommandSender commandSender, Command command, AriCommand ariCommand) {
        this.commandSender = commandSender;
        this.command = command;
        this.ariCommand = ariCommand;
    }

    @Override
    //判断指令是否正确
    public boolean isTheInstructionCorrect() {
        return this.command.getName().equalsIgnoreCase(this.ariCommand.getShow());
    }

    @Override
    public boolean isPlayer() {
        if (!(this.commandSender instanceof Player)) {
            this.commandSender.sendMessage(TextTool.setHEXColorText("command.public.not-player", FilePath.Lang));
            return false;
        }
        return true;
    }

    @Override
    public boolean commandSenderHavePermission(AriCommand ariCommand) {
        if(!Ari.instance.permissionUtils.hasPermission(this.commandSender, ariCommand.getPermission())) {
            this.commandSender.sendMessage(TextTool.setHEXColorText("command.public.permission-message", FilePath.Lang));
            return false;
        }
        return true;
    }

    @Override
    public boolean commandSenderHavePermission() {
        if (!Ari.instance.permissionUtils.hasPermission(this.commandSender, this.ariCommand.getPermission())) {
            this.commandSender.sendMessage(TextTool.setHEXColorText("command.public.permission-message", FilePath.Lang));
            return false;
        }
        return true;
    }

    @Override
    public boolean allCheck() {
        //判断是否是玩家
        if (!(this.commandSender instanceof Player)) {
            this.commandSender.sendMessage(TextTool.setHEXColorText("command.public.not-player", FilePath.Lang));
            return false;
        }
        //判断是否有相应的权限
        return this.commandSenderHavePermission();
    }

    @Override
    public boolean isTheCommandIncomplete(String[] strings, String senderName) {
        return false;
    }
}
