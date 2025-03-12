package ari.superarilo.command.function.impl;

import ari.superarilo.command.function.CommandWarp;
import ari.superarilo.gui.warp.WarpList;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandWarpImpl implements CommandWarp {

    private final CommandSender sender;

    public CommandWarpImpl(CommandSender sender) {
        this.sender = sender;
    }

    @Override
    public void warp() {
        new WarpList((Player) this.sender).open();
    }

    @Override
    public void setWarp(String warpId) {

    }

    @Override
    public void deleteWarp() {

    }
}
