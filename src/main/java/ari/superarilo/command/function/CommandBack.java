package ari.superarilo.command.function;

import ari.superarilo.command.function.impl.CommandBackImpl;
import org.bukkit.command.CommandSender;

public interface CommandBack {
    void startDo();
    static CommandBackImpl build(CommandSender sender) {
        return new CommandBackImpl(sender);
    }
}
