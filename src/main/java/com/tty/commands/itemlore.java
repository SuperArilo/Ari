package com.tty.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.tty.commands.sub.itemlore.ItemLoreAdd;
import com.tty.commands.sub.itemlore.ItemLoreRemove;
import com.tty.lib.command.BaseCommand;
import com.tty.lib.command.SuperHandsomeCommand;
import org.bukkit.command.CommandSender;

import java.util.List;

public class itemlore extends BaseCommand<String> {

    public itemlore() {
        super(false, StringArgumentType.string(), 2);
    }

    @Override
    public List<SuperHandsomeCommand> getSubCommands() {
        return List.of(new ItemLoreAdd(false),
                new ItemLoreRemove(false));
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
        return "itemlore";
    }

    @Override
    public String permission() {
        return "ari.command.itemlore";
    }
}
