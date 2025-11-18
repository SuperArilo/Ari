package com.tty.lib.command;

import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;


public interface SuperHandsomeCommand {
    LiteralCommandNode<CommandSourceStack> toBrigadier();
    String getName();
    String getPermission();
}
