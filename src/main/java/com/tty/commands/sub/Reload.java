package com.tty.commands.sub;

import com.mojang.brigadier.arguments.ArgumentType;
import com.tty.dto.event.CustomPluginReloadEvent;
import com.tty.lib.command.BaseCommand;
import com.tty.lib.command.SuperHandsomeCommand;
import com.tty.tool.ConfigUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.List;

public class Reload extends BaseCommand<String> {

    public Reload(boolean allowConsole, ArgumentType<String> type, int correctArgsLength) {
        super(allowConsole, type, correctArgsLength);
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
        sender.sendMessage(ConfigUtils.t("function.reload.doing"));
        Bukkit.getPluginManager().callEvent(new CustomPluginReloadEvent(sender));
    }

    @Override
    public String name() {
        return "reload";
    }

    @Override
    public String permission() {
        return "ari.command.reload";
    }
}
