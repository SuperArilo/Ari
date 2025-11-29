package com.tty.lib.command;

import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;


public interface SuperHandsomeCommand {
    LiteralCommandNode<CommandSourceStack> toBrigadier();
    boolean isDisabledInGame(CommandSender sender, YamlConfiguration configuration);
}
