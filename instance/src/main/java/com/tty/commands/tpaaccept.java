package com.tty.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.tty.Ari;
import com.tty.commands.check.TeleportCheck;
import com.tty.commands.function.CommandTeleport;
import com.tty.lib.command.BaseCommand;
import com.tty.lib.command.SuperHandsomeCommand;
import com.tty.lib.enum_type.TeleportType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class tpaaccept extends BaseCommand<String> {

    public tpaaccept() {
        super(false, StringArgumentType.string());
    }

    @Override
    public List<SuperHandsomeCommand> getSubCommands() {
        return List.of();
    }

    @Override
    public List<String> tabSuggestions(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        return TeleportCheck.TELEPORT_STATUS.stream()
                .filter(obj -> obj.getBePlayerUUID().equals(player.getUniqueId())
                        && obj.getType().equals(TeleportType.PLAYER))
                .map(e -> Ari.instance.getServer().getPlayer(e.getPlayUUID()))
                .filter(Objects::nonNull)
                .map(Player::getName)
                .collect(Collectors.toList());
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        new CommandTeleport((Player) sender, args[1]).tpaaccept();
    }

    @Override
    public String name() {
        return "tpaaccept";
    }

    @Override
    public String permission() {
        return "ari.command.tpaaccept";
    }
}
