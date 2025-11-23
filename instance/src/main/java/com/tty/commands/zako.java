package com.tty.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.tty.commands.sub.zako.ZakoAdd;
import com.tty.commands.sub.zako.ZakoInfo;
import com.tty.commands.sub.zako.ZakoRemove;
import com.tty.lib.command.BaseCommand;
import com.tty.lib.command.SuperHandsomeCommand;
import org.bukkit.command.CommandSender;

import java.util.List;

public class zako extends BaseCommand<String> {

    public zako() {
        super(true, StringArgumentType.string(), 2);
    }

    @Override
    public List<SuperHandsomeCommand> getSubCommands() {
        StringArgumentType string = StringArgumentType.word();
        return List.of(new ZakoAdd(true, string),
                new ZakoInfo(true, string),
                new ZakoRemove(true, string));
    }

    @Override
    public List<String> tabSuggestions(CommandSender sender, String[] args) {
        return List.of();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
    }

    @Override
    public String name() {
        return "zako";
    }

    @Override
    public String permission() {
        return "ari.command.zako";
    }
}
