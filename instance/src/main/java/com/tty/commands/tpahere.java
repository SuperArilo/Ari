package com.tty.commands;

import com.tty.commands.function.CommandTeleport;
import com.tty.lib.command.BaseCommand;
import com.tty.lib.command.SuperHandsomeCommand;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class tpahere extends BaseCommand<PlayerSelectorArgumentResolver> {

    public tpahere() {
        super(false, ArgumentTypes.player(), 2);
    }

    @Override
    public List<SuperHandsomeCommand> getSubCommands() {
        return List.of();
    }

    @Override
    public List<String> tabSuggestions(CommandSender sender, String[] args) {
        return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .filter(name -> !name.equals(sender.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        new CommandTeleport((Player) sender, args[1]).tpahere();
    }

    @Override
    public String name() {
        return "tpahere";
    }

    @Override
    public String permission() {
        return "ari.command.tpahere";
    }
}
