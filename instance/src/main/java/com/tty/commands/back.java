package com.tty.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.tty.Ari;
import com.tty.entity.state.teleport.EntityToLocationState;
import com.tty.lib.command.BaseCommand;
import com.tty.lib.command.SuperHandsomeCommand;
import com.tty.enumType.TeleportType;
import com.tty.states.TeleportStateMachine;
import com.tty.tool.ConfigUtils;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

import static com.tty.listener.teleport.RecordLastLocationListener.TELEPORT_LAST_LOCATION;

public class back extends BaseCommand<String> {

    public back() {
        super(false, StringArgumentType.string(), 1);
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
        Location beforeLocation = TELEPORT_LAST_LOCATION.get(player);
        if(beforeLocation == null) {
            player.sendMessage(ConfigUtils.t("teleport.none-location"));
            return;
        }

        Ari.instance.stateMachineManager
                .get(TeleportStateMachine.class)
                .addState(new EntityToLocationState(
                        player,
                        TeleportType.getDelayTime(TeleportType.BACK),
                        beforeLocation,
                        TeleportType.BACK));
    }

    @Override
    public String name() {
        return "back";
    }

    @Override
    public String permission() {
        return "ari.command.back";
    }
}
