package ari.superarilo.command.function;

import ari.superarilo.command.function.impl.CommandTeleportImpl;
import ari.superarilo.enumType.AriCommand;
import org.bukkit.command.CommandSender;

import java.util.List;

public interface CommandTeleport {
    void tpa();
    void tpaaccept();
    void tparefuse();
    void tpahere();
    List<String> getOnlinePlayers(AriCommand ariCommand);
    List<String> getHasRequestPlayers(AriCommand ariCommand);
    static CommandTeleportImpl build(CommandSender sender, String playerName) {
        return new CommandTeleportImpl(sender, playerName);
    }
}
