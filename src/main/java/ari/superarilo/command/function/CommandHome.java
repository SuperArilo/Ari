package ari.superarilo.command.function;

import ari.superarilo.command.function.impl.CommandHomeImpl;
import org.bukkit.command.CommandSender;

import java.util.List;

public interface CommandHome {
    void setHome(String homeId);
    void home();
    void deleteHome(String homeId);
    List<String> getHomeList();

    static CommandHomeImpl build(CommandSender sender) {
        return new CommandHomeImpl(sender);
    }
}
