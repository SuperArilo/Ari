package com.tty.commands;

import com.tty.Ari;
import com.tty.commands.sub.tpa.TpaBase;

import com.tty.dto.state.teleport.PreEntityToEntityState;
import com.tty.enumType.TeleportType;
import com.tty.lib.command.SuperHandsomeCommand;
import com.tty.states.teleport.PreTeleportStateServiceImpl;
import com.tty.tool.ConfigUtils;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class tpa extends TpaBase<PlayerSelectorArgumentResolver> {

    public tpa() {
        super(false, ArgumentTypes.player(), 2);
    }

    @Override
    public List<SuperHandsomeCommand> getSubCommands() {
        return List.of();
    }

    @Override
    public List<String> tabSuggestions(CommandSender sender, String[] args) {
        return this.getOnlinePlayers(sender);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player owner = (Player) sender;
        Player player = Ari.instance.getServer().getPlayerExact(args[1]);
        if (player == null) {
            sender.sendMessage(ConfigUtils.t("teleport.unable-player"));
            return;
        }
        Ari.instance.stateMachineManager
                .get(PreTeleportStateServiceImpl.class)
                .addState(new PreEntityToEntityState(
                        owner,
                        player,
                        TeleportType.getCoolDownTime(TeleportType.TPA),
                        this.name()));
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
