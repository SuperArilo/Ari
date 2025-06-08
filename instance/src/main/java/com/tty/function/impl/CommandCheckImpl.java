package com.tty.function.impl;

import com.tty.function.CommandCheck;
import com.tty.enumType.AriCommand;
import com.tty.enumType.FilePath;
import com.tty.tool.PermissionUtils;
import com.tty.tool.TextTool;
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
            this.commandSender.sendMessage(TextTool.setHEXColorText("function.public.not-player", FilePath.Lang));
            return false;
        }
        return true;
    }

    @Override
    public boolean commandSenderHavePermission(AriCommand ariCommand) {
        if(!PermissionUtils.hasPermission(this.commandSender, ariCommand.getPermission())) {
            this.commandSender.sendMessage(TextTool.setHEXColorText("base.permission.no-permission", FilePath.Lang));
            return false;
        }
        return true;
    }

    @Override
    public boolean commandSenderHavePermission() {
        if (!PermissionUtils.hasPermission(this.commandSender, this.ariCommand.getPermission())) {
            this.commandSender.sendMessage(TextTool.setHEXColorText("base.permission.no-permission", FilePath.Lang));
            return false;
        }
        return true;
    }

    @Override
    public boolean allCheck() {
        //判断是否是玩家
        if (!(this.commandSender instanceof Player)) {
            this.commandSender.sendMessage(TextTool.setHEXColorText("function.public.not-player", FilePath.Lang));
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
