package com.tty.commands;

import com.tty.Ari;
import com.tty.commands.check.TeleportCheck;
import com.tty.commands.function.CommandTeleport;
import com.tty.entity.state.State;
import com.tty.entity.state.teleport.PlayerToPlayerState;
import com.tty.lib.command.BaseCommand;
import com.tty.lib.command.SuperHandsomeCommand;
import com.tty.lib.enum_type.TeleportType;
import com.tty.tool.ConfigUtils;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class tpaaccept extends BaseCommand<PlayerSelectorArgumentResolver> {

    public tpaaccept() {
        super(false, ArgumentTypes.player(), 2);
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
        Player player = (Player) sender;
        Player target = Ari.instance.getServer().getPlayerExact(args[1]);

        PlayerToPlayerState anElse = (PlayerToPlayerState) Ari.instance.preTeleportStateMachine
                .getStates(target)
                .stream()
                .filter(i -> i instanceof PlayerToPlayerState state && state.getTarget().equals(player)).findFirst().orElse(null);

        if (anElse == null) {
            player.sendMessage(ConfigUtils.t("function.tpa.been-done"));
            return;
        }
        player.sendMessage(ConfigUtils.t("function.tpa.agree"));
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
