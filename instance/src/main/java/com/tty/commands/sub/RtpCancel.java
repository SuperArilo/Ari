package com.tty.commands.sub;

import com.mojang.brigadier.arguments.ArgumentType;
import com.tty.Ari;
import com.tty.entity.state.State;
import com.tty.lib.command.BaseCommand;
import com.tty.lib.command.SuperHandsomeCommand;
import com.tty.states.RandomTpStateMachine;
import com.tty.tool.ConfigUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;


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
        RandomTpStateMachine machine = Ari.instance.stateMachineManager.get(RandomTpStateMachine.class);
        List<State> states = machine.getStates((Entity) sender);
        if (states.isEmpty()) {
            player.sendMessage(ConfigUtils.t("function.rtp.no-rtp"));
            return;
        }
        if(machine.removeState(states.getFirst())) {
            player.sendMessage(ConfigUtils.t("function.rtp.rtp-cancel"));
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
