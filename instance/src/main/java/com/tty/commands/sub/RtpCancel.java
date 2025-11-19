package com.tty.commands.sub;

import com.mojang.brigadier.arguments.ArgumentType;
import com.tty.commands.function.CommandRtp;
import com.tty.lib.command.BaseCommand;
import com.tty.lib.command.SuperHandsomeCommand;
import com.tty.tool.ConfigUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

import static com.tty.commands.rtp.RTP_LIST;


public class RtpCancel extends BaseCommand<String> {

    public RtpCancel(boolean allowConsole, ArgumentType<String> type) {
        super(allowConsole, type, 2);
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
        CommandRtp commandRtp = RTP_LIST.get(player);
        if (commandRtp == null) {
            player.sendMessage(ConfigUtils.t("function.rtp.no-rtp"));
        } else {
            commandRtp.cancelRtp();
        }
    }

    @Override
    public String name() {
        return "cancel";
    }

    @Override
    public String permission() {
        return "ari.command.rtp.cancel";
    }
}
