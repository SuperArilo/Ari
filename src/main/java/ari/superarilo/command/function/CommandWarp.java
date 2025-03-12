package ari.superarilo.command.function;

import ari.superarilo.command.function.impl.CommandWarpImpl;
import org.bukkit.command.CommandSender;

public interface CommandWarp {
    void warp();
    void setWarp(String warpId);
    void deleteWarp();
    static CommandWarp build(CommandSender sender) {
        return new CommandWarpImpl(sender);
    }
}
