package com.tty.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.tty.Ari;
import com.tty.enumType.FilePath;
import com.tty.gui.warp.WarpList;
import com.tty.lib.command.BaseCommand;
import com.tty.lib.command.SuperHandsomeCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class warp extends BaseCommand<String> {

    public warp() {
        super(false, StringArgumentType.string(), 1);
    }

    @Override
    public List<SuperHandsomeCommand> getSubCommands() {
        return List.of();
    }

    @Override
    public List<String> tabSuggestions(CommandSender sender, String[] args) {
        return List.of();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!this.isDisabledInGame(sender, Ari.C_INSTANCE.getObject(FilePath.WARP_CONFIG.name()))) return;

        new WarpList((Player) sender).open();
    }

    @Override
    public String name() {
        return "warp";
    }

    @Override
    public String permission() {
        return "ari.command.warp";
    }
}
