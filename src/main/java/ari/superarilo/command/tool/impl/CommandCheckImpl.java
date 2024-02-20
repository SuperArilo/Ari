package ari.superarilo.command.tool.impl;

import ari.superarilo.command.tool.CommandCheck;
import ari.superarilo.enumType.Commands;
import ari.superarilo.tool.TextTool;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class CommandCheckImpl implements CommandCheck {

    private static final String i = "command";
    private static final String d = ".";
    private static final String NOTPLAYER = "not-player";
    private static final String PERMISSIONMESSAGE = "permission-message";
    private final FileConfiguration config;

    public CommandCheckImpl(FileConfiguration config) {
        this.config = config;
    }

    @Override
    public boolean isTheInstructionCorrect(Command command, Commands type) {
        return command.getName().equalsIgnoreCase(type.getShow());
    }

    @Override
    public boolean isPlayer(CommandSender commandSender, Commands type) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(TextTool.setHEXColorText(config.getString(i + d + type.getShow() + d + NOTPLAYER, "null")));
            return false;
        }
        return true;
    }

    @Override
    public boolean commandSenderHavePermission(CommandSender commandSender, Commands type) {
        if (!commandSender.hasPermission(type.getPermission())) {
            commandSender.sendMessage(TextTool.setHEXColorText(config.getString(i + d + type.getShow() + d + PERMISSIONMESSAGE, "null")));
            return false;
        }
        return true;
    }

    @Override
    public boolean isTheCommandIncomplete(String[] strings, String senderName) {
        return false;
    }
}
