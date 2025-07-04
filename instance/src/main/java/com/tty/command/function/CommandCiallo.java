package com.tty.command.function;

import com.tty.lib.tool.ComponentUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class CommandCiallo {

    private static final String CIALLO = "<#00B2EE>Ciallo～(∠・ω< )⌒☆</#473C8B>";
    private final CommandSender sender;

    public CommandCiallo(CommandSender sender) {
        this.sender = sender;
    }

    public void ciallo() {
        Bukkit.broadcast(ComponentUtils.text("玩家 " + this.sender.getName() + ": 我宣布个事，我是柚子厨 " + CIALLO));
    }

}
