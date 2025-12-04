package com.tty.commands;

import com.tty.Ari;
import com.tty.commands.sub.tpa.TpaBase;
import com.tty.enumType.FilePath;
import com.tty.lib.command.SuperHandsomeCommand;
import com.tty.lib.enum_type.LangType;
import com.tty.tool.ConfigUtils;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public class tparefuse extends TpaBase<PlayerSelectorArgumentResolver> {

    public tparefuse() {
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
        Player target = Bukkit.getPlayerExact(args[1]);
        if (this.checkAfterResponse(player, target) != null) {
            sender.sendMessage(ConfigUtils.t("function.tpa.refuse-success"));
            assert target != null;
            target.sendMessage(ConfigUtils.t("function.tpa.refused", Map.of(LangType.TPA_BE_SENDER.getType(), Component.text(sender.getName()))));
        }
    }

    @Override
    public String name() {
        return "tparefuse";
    }

    @Override
    public String permission() {
        return "ari.command.tparefuse";
    }
}
