package com.tty.commands;

import com.tty.Ari;
import com.tty.commands.sub.tpa.TpaBase;
import com.tty.dto.state.teleport.PlayerToPlayerState;
import com.tty.dto.state.teleport.PreEntityToEntityState;
import com.tty.enumType.FilePath;
import com.tty.lib.command.SuperHandsomeCommand;
import com.tty.states.teleport.TeleportStateService;
import com.tty.tool.ConfigUtils;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class tpaaccept extends TpaBase<PlayerSelectorArgumentResolver> {

    public tpaaccept() {
        super(false, ArgumentTypes.player(), 2);
    }

    @Override
    public List<SuperHandsomeCommand> getSubCommands() {
        return List.of();
    }

    @Override
    public List<String> tabSuggestions(CommandSender sender, String[] args) {
        return this.getResponseList(sender);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!this.isDisabledInGame(sender, Ari.C_INSTANCE.getObject(FilePath.TPA_CONFIG.name()))) return;

        Player player = (Player) sender;
        Player target = Ari.instance.getServer().getPlayerExact(args[1]);

        PreEntityToEntityState anElse = this.checkAfterResponse(player, target);
        if (anElse == null) return;

        assert target != null;
        int value = Ari.C_INSTANCE.getValue("main.teleport.delay", FilePath.TPA_CONFIG, Integer.class, 3);
        PlayerToPlayerState state;
        if (anElse.getType().getKey().equals("tpa")) {
            state = new PlayerToPlayerState(target, player, value, "tpa");
        } else {
            state = new PlayerToPlayerState(player, target, value, "tpahere");
        }

        //添加传送请求
        if (Ari.instance.stateMachineManager.get(TeleportStateService.class).addState(state)) {
            player.sendMessage(ConfigUtils.t("function.tpa.agree"));
        }

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
