package com.tty.commands;

import com.tty.Ari;
import com.tty.commands.sub.tpa.TpaBase;
import com.tty.dto.state.teleport.PreEntityToEntityState;
import com.tty.enumType.FilePath;
import com.tty.lib.command.SuperHandsomeCommand;
import com.tty.states.teleport.PreTeleportStateServiceImpl;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class tpahere extends TpaBase<PlayerSelectorArgumentResolver> {

    public tpahere() {
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
        if (!this.isDisabledInGame(sender, Ari.C_INSTANCE.getObject(FilePath.TPA_CONFIG.name()))) return;

        Player owner = (Player) sender;
        Player player = Ari.instance.getServer().getPlayerExact(args[1]);

        Ari.instance.stateMachineManager
                .get(PreTeleportStateServiceImpl.class)
                .addState(new PreEntityToEntityState(
                        owner,
                        player,
                        Ari.C_INSTANCE.getValue("main.teleport.delay", FilePath.TPA_CONFIG, Integer.class, 3),
                        this.name()));
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
