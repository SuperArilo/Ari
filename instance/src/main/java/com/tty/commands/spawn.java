package com.tty.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.tty.Ari;
import com.tty.enumType.FilePath;
import com.tty.function.Teleport;
import com.tty.lib.command.BaseCommand;
import com.tty.lib.command.SuperHandsomeCommand;
import com.tty.lib.tool.Log;
import com.tty.tool.ConfigUtils;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class spawn extends BaseCommand<String> {

    public spawn() {
        super(false, StringArgumentType.string());
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
        Player player = (Player) sender;
        Location value = Ari.C_INSTANCE.getValue("main.location", FilePath.SpawnConfig, Location.class);
        if(value == null) {
            Log.debug("location null");
            player.sendMessage(ConfigUtils.t("function.spawn.no-spawn"));
            return;
        }
        Teleport.create(player, value, Ari.C_INSTANCE.getValue("main.teleport-delay", FilePath.SpawnConfig, Integer.class, 3)).teleport();
    }

    @Override
    public String name() {
        return "spawn";
    }

    @Override
    public String permission() {
        return "ari.command.spawn";
    }
}
