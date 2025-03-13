package ari.superarilo.command.function;

import ari.superarilo.command.function.impl.CommandWarpImpl;
import org.bukkit.command.CommandSender;

import java.util.List;

public interface CommandWarp {
    void warp();
    void setWarp(String warpId);
    void deleteWarp(String warpId);
    List<String> getWarpList();
    static CommandWarp build(CommandSender sender) {
        return new CommandWarpImpl(sender);
    }
}
