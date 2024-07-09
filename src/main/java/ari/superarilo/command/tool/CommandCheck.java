package ari.superarilo.command.tool;

import ari.superarilo.command.tool.impl.CommandCheckImpl;
import ari.superarilo.enumType.AriCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public interface CommandCheck {
    boolean isTheInstructionCorrect(Command command, AriCommand type);
    boolean isPlayer(CommandSender commandSender, AriCommand type);
    boolean commandSenderHavePermission(CommandSender commandSender, AriCommand type);
    boolean allCheck(CommandSender commandSender, Command command, AriCommand ariCommand);
    boolean isTheCommandIncomplete(String[] strings, String senderName);
    static CommandCheckImpl create() {
        return new CommandCheckImpl();
    }
}
