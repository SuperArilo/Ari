package com.tty.commands;

import com.tty.Ari;
import com.tty.entity.state.teleport.PlayerToPlayerState;
import com.tty.lib.command.BaseCommand;
import com.tty.lib.command.SuperHandsomeCommand;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class tpa extends BaseCommand<PlayerSelectorArgumentResolver> {

    public tpa() {
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
        Player owner = (Player) sender;
        Player player = Ari.instance.getServer().getPlayerExact(args[1]);

        Ari.instance.preTeleportStateMachine.addState(new PlayerToPlayerState(owner, player, 10, this.name()));
    }

    @Override
    public String name() {
        return "tpa";
    }

    @Override
    public String permission() {
        return "ari.command.tpa";
    }
}
