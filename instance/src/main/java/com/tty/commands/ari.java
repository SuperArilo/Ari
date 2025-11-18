package com.tty.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.tty.lib.command.BaseCommand;
import com.tty.lib.command.SuperHandsomeCommand;
import org.bukkit.command.CommandSender;

import java.util.List;

public class ari extends BaseCommand<String> {

    public ari() {
        super(true, StringArgumentType.word());
    }

    @Override
    public List<SuperHandsomeCommand> getSubCommands() {
        return List.of(
            new back(),
            new home(),
            new itemlore(),
            new itemname(),
            new rtp(),
            new sethome(),
            new setspawn(),
            new setwarp(),
            new spawn(),
            new time(),
            new tpa(),
            new tpaaccept(),
            new tpahere(),
            new tparefuse(),
            new warp(),
            new zako()
        );
    }

    @Override
    public List<String> tabSuggestions(CommandSender sender, String[] args) {
        return List.of("11414");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

    }

    @Override
    public String name() {
        return "ari";
    }

    @Override
    public String permission() {
        return null;
    }
}
