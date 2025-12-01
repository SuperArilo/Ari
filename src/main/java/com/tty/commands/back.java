package com.tty.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.tty.Ari;
import com.tty.dto.state.teleport.EntityToLocationState;
import com.tty.enumType.FilePath;
import com.tty.lib.command.BaseCommand;
import com.tty.lib.command.SuperHandsomeCommand;
import com.tty.lib.enum_type.TeleportType;
import com.tty.states.teleport.TeleportStateService;
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
        if (!this.isDisabledInGame(sender, Ari.C_INSTANCE.getObject(FilePath.BACK_CONFIG.name()))) return;

        Player player = (Player) sender;
        Location beforeLocation = TELEPORT_LAST_LOCATION.get(player);
        if(beforeLocation == null) {
            player.sendMessage(ConfigUtils.t("teleport.none-location"));
            return;
        }

        Ari.instance.stateMachineManager
                .get(TeleportStateService.class)
                .addState(new EntityToLocationState(
                        player,
                        Ari.C_INSTANCE.getValue("main.teleport.delay", FilePath.BACK_CONFIG, Integer.class, 3),
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
