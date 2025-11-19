package com.tty.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.tty.commands.function.CommandRtp;
import com.tty.commands.sub.RtpCancel;
import com.tty.lib.command.BaseCommand;
import com.tty.lib.command.SuperHandsomeCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;


public class rtp extends BaseCommand<String> {

    public static final Map<Player, CommandRtp> RTP_LIST = Collections.synchronizedMap(new WeakHashMap<>());

    public rtp() {
        super(false, StringArgumentType.string(), 1);
    }

    @Override
    public List<SuperHandsomeCommand> getSubCommands() {
        return List.of(new RtpCancel(false, StringArgumentType.string()));
    }

    @Override
    public List<String> tabSuggestions(CommandSender sender, String[] args) {
        return List.of();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        CommandRtp commandRtp = new CommandRtp(player);
        commandRtp.rtp();
    }

    @Override
    public String name() {
        return "rtp";
    }

    @Override
    public String permission() {
        return "ari.command.rtp";
    }

}
