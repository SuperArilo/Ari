package ari.superarilo.command.tool.impl;

import ari.superarilo.command.tool.CommandCheck;
import ari.superarilo.enumType.AriCommand;
import ari.superarilo.enumType.FilePath;
import ari.superarilo.tool.ConfigFiles;
import ari.superarilo.tool.TextTool;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandCheckImpl implements CommandCheck {

    private static final String i = "command";
    private static final String d = ".";
    private static final String NOTPLAYER = "not-player";
    private static final String PERMISSIONMESSAGE = "permission-message";
    private final ConfigFiles config;

    public CommandCheckImpl(ConfigFiles config) {
        this.config = config;
    }

    @Override
    public boolean isTheInstructionCorrect(Command command, AriCommand type) {
        return command.getName().equalsIgnoreCase(type.getShow());
    }

    @Override
    public boolean isPlayer(CommandSender commandSender, AriCommand type) {
        return commandSender instanceof Player;
    }

    @Override
    public boolean commandSenderHavePermission(CommandSender commandSender, AriCommand type) {
        if (!commandSender.hasPermission(type.getPermission())) {
            commandSender.sendMessage(TextTool.setHEXColorText(this.config.getValue(i + d + type.getShow() + d + PERMISSIONMESSAGE, FilePath.Lang, String.class)));
            return false;
        }
        return true;
    }

    @Override
    public boolean allCheck(CommandSender commandSender, Command command, AriCommand ariCommand) {
        if (this.isTheInstructionCorrect(command, ariCommand)) {
            if (!this.isPlayer(commandSender, ariCommand)) {
                commandSender.sendMessage(TextTool.setHEXColorText(this.config.getValue(i + d + ariCommand.getShow() + d + NOTPLAYER, FilePath.Lang, String.class)));
                return false;
            }
            if (!this.commandSenderHavePermission(commandSender, ariCommand)) {
                commandSender.sendMessage(TextTool.setHEXColorText(this.config.getValue(i + d + ariCommand.getShow() + d + PERMISSIONMESSAGE, FilePath.Lang, String.class)));
                return false;
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean isTheCommandIncomplete(String[] strings, String senderName) {
        return false;
    }
}
